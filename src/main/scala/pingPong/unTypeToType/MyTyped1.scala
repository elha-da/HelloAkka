package com.pingPong.unTypeToType

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors


object MyTyped1 {
  sealed trait Command
  final case class Ping(replyTo: ActorRef[Pong.type]) extends Command

  case object Pong

  val behavior: Behavior[Command] =
    Behaviors.receive[Command] { (ctx, msg) =>
      msg match {
        case Ping(replyTo) =>
          println(s"${ctx.self} got PingF from $replyTo")
          replyTo ! Pong
          Behaviors.same
      }
    }
}