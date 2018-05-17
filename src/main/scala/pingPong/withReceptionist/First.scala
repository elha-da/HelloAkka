package pingPong.withReceptionist

import akka.actor.{OneForOneStrategy, PostRestartException}
import akka.actor.typed.{ActorRef, Behavior, DeathPactException, SupervisorStrategy}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object First {

  val pingServiceKeyFirst = ServiceKey[PingF]("pingService-First")

  final case class PingF(replyTo: ActorRef[PongF.type])

  final case object PongF


  def supervised()
  : Behavior[PingF] =
    Behaviors.supervise(
      preSupervised()
    ).onFailure[RuntimeException] {
//      SupervisorStrategy.stop
      SupervisorStrategy.restartWithLimit(
        maxNrOfRetries = 4,
        withinTimeRange = 3.seconds
      )
    }

  private def preSupervised()
  : Behavior[PingF] =
    Behaviors.supervise(
      init()
    ).onFailure[IllegalStateException] {
//      println(s"==== Illegal State Exception")
//      SupervisorStrategy.stop
      SupervisorStrategy.restartWithBackoff(
        minBackoff = 500.millis,
        maxBackoff = 10.seconds,
        randomFactor = 0.1
      )
    }


  private def init()
  : Behavior[PingF] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("First init & register child actor")
      ctx.system.receptionist ! Receptionist.Register(pingServiceKeyFirst, ctx.self)

      start()
    }

  private def start()
  : Behavior[PingF] =
    Behaviors.receive[PingF] { (contx, msg) =>
      msg match {
        case PingF(replyTo) =>
          contx.system.log.info(s"pingService-First : $replyTo")

//          throw new RuntimeException("Bad luck")
//          throw new IllegalStateException("Bad luck")

          replyTo ! PongF
          Behaviors.stopped
      }
    }

}
