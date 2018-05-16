package pingPong.withReceptionist

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors

object First {

  val pingServiceKeyFirst = ServiceKey[Ping]("pingService-First")

  final case class Ping(replyTo: ActorRef[Pong.type])

  final case object Pong

  def init()
  : Behavior[Ping] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("First init & register child actor")
      ctx.system.receptionist ! Receptionist.Register(pingServiceKeyFirst, ctx.self)

      start()
    }

  def start()
  : Behavior[Ping] =
    Behaviors.receive[Ping] { (contx, msg) =>
      msg match {
        case Ping(replyTo) =>
          contx.system.log.info(s"pingService-First : $replyTo")
          replyTo ! Pong
          Behaviors.stopped
      }
    }

}
