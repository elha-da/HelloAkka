package pingPong.withReceptionist

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object Second {

  def pinger(pingService: ActorRef[First.Ping])
  : Behavior[First.Pong.type] =
    Behaviors.setup[First.Pong.type] { ctx =>
      ctx.system.log.info(s"pingService-Second : $pingService ! ${ctx.self}")
      pingService ! First.Ping(ctx.self)

      Behaviors.immutable { (contx, msg) =>
        contx.system.log.info("I was ponged!!" + msg)
        Behaviors.same
      }
    }

}
