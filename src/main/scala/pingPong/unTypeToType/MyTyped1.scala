package pingPong.unTypeToType

import akka.typed.{Behavior, ActorRef}
import akka.typed.scaladsl.Actor


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