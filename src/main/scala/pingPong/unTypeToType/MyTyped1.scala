package pingPong.unTypeToType

import akka.actor.typed.{Behavior, ActorRef}
import akka.actor.typed.scaladsl.Actor


object MyTyped1 {
  sealed trait Command
  final case class Ping(replyTo: ActorRef[Pong.type]) extends Command

  case object Pong

  val behavior: Behavior[Command] =
    Actor.immutable { (ctx, msg) =>
      msg match {
        case Ping(replyTo) =>
          println(s"${ctx.self} got Ping from $replyTo")
          replyTo ! Pong
          Actor.same
      }
    }
}