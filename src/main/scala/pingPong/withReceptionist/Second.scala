package pingPong.withReceptionist

import akka.actor.typed.{ActorRef, Behavior, DeathPactException, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object Second {

  def pinger(pingService: ActorRef[First.PingF])
  : Behavior[First.PongF.type] =
    Behaviors.setup[First.PongF.type] { ctx =>
      ctx.system.log.info(s"pingService-Second : $pingService ! ${ctx.self}")

//      throw new IllegalStateException

      pingService ! First.PingF(ctx.self)

      Behaviors.receive[First.PongF.type] { (context, msg) =>
        context.system.log.info("I was ponged!! " + msg)
        Behaviors.stopped
      }
    }

}
