package pingPong.withReceptionist

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors

object Third {

  val pingServiceKeyThird = ServiceKey[Ping]("pingService-Third")

  final case class Ping(replyTo: ActorRef[Pong.type])
  final case object Pong

  val pingService
  : Behavior[Ping] =
    Behaviors.setup { ctx =>
      ctx.system.receptionist ! Receptionist.Register(pingServiceKeyThird, ctx.self) //, ctx.system.deadLetters)
      Behaviors.receive[Ping] { (contx, msg) =>
        msg match {
          case Ping(replyTo) =>
            contx.system.log.info(s"pingService-Third: $replyTo")
//            println(s"pingService-Third : $replyTo")
            replyTo ! Pong
            Behaviors.stopped
        }
      }
    }
}
