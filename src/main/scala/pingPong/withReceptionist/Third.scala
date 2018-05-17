package pingPong.withReceptionist

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors

object Third {

  val pingServiceKeyThird = ServiceKey[Comm]("pingService-Third")

  sealed trait Comm

  final case class PingT(replyTo: ActorRef[PongTC]) extends Comm
  final case object PongT
  final case class PongTC(pong: PongT.type)

  val pingService
  : Behavior[Comm] =
    Behaviors.setup { ctx =>
      ctx.system.receptionist ! Receptionist.Register(pingServiceKeyThird, ctx.self) //, ctx.system.deadLetters)
      Behaviors.receive[Comm] { (contx, msg) =>
        msg match {
          case PingT(replyTo) =>
            contx.system.log.info(s"pingService-Third: $replyTo")
//            println(s"pingService-Third : $replyTo")
            replyTo ! PongTC(PongT)
            Behaviors.stopped
        }
      }
    }
}
