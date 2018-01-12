package com.msgProtocols

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor

object ChatRoom {
  sealed trait Command
  final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent])
    extends Command
  private final case class PostSessionMessage(screenName: String, message: String)
    extends Command

  sealed trait SessionEvent
  final case class SessionGranted(handle: ActorRef[PostMessage])
    extends SessionEvent
  final case class SessionDenied(reason: String)
    extends SessionEvent
  final case class MessagePosted(screenName: String, message: String)
    extends SessionEvent

  final case class PostMessage(message: String)

  val behavior: Behavior[Command] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionEvent]])
  : Behavior[Command] =
    Actor.immutable[Command] { (ctx, msg) =>
      msg match {
        case GetSession(screenName, client) =>
          ctx.system.log.info(s"chatRoom - GetSession: $PostMessage")
          val wrapper: ActorRef[PostMessage] = ctx.spawnAdapter{ p: PostMessage => PostSessionMessage(screenName, p.message) }
          client ! SessionGranted(wrapper)
          chatRoom(client :: sessions)

        case PostSessionMessage(screenName, message) =>
          ctx.system.log.info(s"chatRoom - PostSessionMessage: $screenName $message")
          val mp = MessagePosted(screenName, message)
          sessions foreach (_ ! mp)
          Actor.same
      }
    }
}
