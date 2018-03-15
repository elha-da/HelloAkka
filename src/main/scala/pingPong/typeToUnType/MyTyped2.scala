package com.pingPong.typeToUnType

import akka.actor.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}

object MyTyped2 {
  sealed trait Command
  case object Pong extends Command

  final case class Ping(replyTo: akka.actor.typed.ActorRef[Pong.type])

//  val PingServiceKey = ServiceKey[Command]("pingService")

//  val behaviorTyped: Behavior[Command] =
  def behaviorTyped(): Behavior[Command] =
    Behaviors.setup { context =>
      // context.spawn is an implicit extension method
      val secondActRef: ActorRef = context.actorOf(MyUntyped2.myProps(), "second")

//      context.system.receptionist ! Receptionist.Register(PingServiceKey, context.self, context.system.deadLetters)

      // context.watch is an implicit extension method
      context.watch(secondActRef)

      // illustrating how to pass sender, toUntyped is an implicit extension method
      secondActRef.tell(MyTyped2.Ping(context.self), context.self.toUntyped)

      Behaviors.immutable[Command] { (ctx, msg) =>
        msg match {
          case Pong =>
            // it's not possible to get the sender, that must be sent in message
            println(s"${ctx.self} got Pong")
            // context.stop is an implicit extension method
            ctx.stop(secondActRef)
            Behaviors.same
        }
      } onSignal {
        case (ctx, akka.actor.typed.Terminated(ref)) =>
          println(s"${ctx.self} observed termination of $ref")
          Behaviors.stopped
      }
    }
}

