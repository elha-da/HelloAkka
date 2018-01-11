package pingPong.typeToUnType

import akka.actor.ActorRef
import akka.typed.{Behavior}
import akka.typed.scaladsl.adapter._
import akka.typed.scaladsl.Actor

object MyTyped2 {
  sealed trait Command
  case object Pong extends Command

  final case class Ping(replyTo: akka.typed.ActorRef[Pong.type])

//  val behaviorTyped: Behavior[Command] =
  def behaviorTyped(): Behavior[Command] =
    Actor.deferred { context =>
      // context.spawn is an implicit extension method
      val secondActRef: ActorRef = context.actorOf(MyUntyped2.myProps(), "second")

      // context.watch is an implicit extension method
      context.watch(secondActRef)

      // illustrating how to pass sender, toUntyped is an implicit extension method
      secondActRef.tell(MyTyped2.Ping(context.self), context.self.toUntyped)

      Actor.immutable[Command] { (ctx, msg) =>
        msg match {
          case Pong =>
            // it's not possible to get the sender, that must be sent in message
            println(s"${ctx.self} got Pong")
            // context.stop is an implicit extension method
            ctx.stop(secondActRef)
            Actor.same
        }
      } onSignal {
        case (ctx, akka.typed.Terminated(ref)) =>
          println(s"${ctx.self} observed termination of $ref")
          Actor.stopped
      }
    }
}

