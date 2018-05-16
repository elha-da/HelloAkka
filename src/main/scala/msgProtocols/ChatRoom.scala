package com.msgProtocols

import akka.actor.typed.{ActorRef, Behavior, Terminated}
import akka.actor.typed.scaladsl.Behaviors

object ChatRoom {
  sealed trait Command
  final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends Command
  private final case class PostSessionMessage(screenName: String, message: String) extends Command

  sealed trait SessionEvent
  final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
  final case class SessionDenied(reason: String)                      extends SessionEvent
  final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  final case class PostMessage(message: String)

  private def chatRoom(sessions: List[ActorRef[SessionEvent]])
  : Behavior[Command] =
    Behaviors.receive[Command] { (ctx, msg) =>
      msg match {
        case GetSession(screenName, client) =>
          ctx.system.log.info(s"chatRoom - GetSession - context   : $ctx")
          ctx.system.log.info(s"chatRoom - GetSession - msg       : $msg")
          ctx.system.log.info(s"chatRoom - GetSession - screenName: $screenName")
          ctx.system.log.info(s"chatRoom - GetSession - client    : $client")
          ctx.system.log.info(s"chatRoom - GetSession - sessions  : $sessions")
          val wrapper: ActorRef[PostMessage] = ctx.messageAdapter{ p: PostMessage => PostSessionMessage(screenName, p.message) }
          client ! SessionGranted(wrapper)
          chatRoom(client :: sessions)

        case PostSessionMessage(screenName, message) =>
          ctx.system.log.info(s"chatRoom - PostSessionMessage: $screenName | $message")
          ctx.system.log.info(s"chatRoom - PostSessionMessage - sessions: $sessions")
          val mp = MessagePosted(screenName, message)
          sessions foreach (_ ! mp)
          Behaviors.same
      }
    }

  val gabbler
  : Behavior[SessionEvent]  =
    Behaviors.receive[SessionEvent] { (context, msg) =>
      msg match {
        case SessionDenied(reason) =>
          context.system.log.info(s"chatRoom - SessionDenied: $reason")
          println(s"cannot start chat room session: $reason")
          Behaviors.stopped
        case SessionGranted(handle) =>
          context.system.log.info(s"chatRoom - SessionGranted: $handle")
          handle ! PostMessage("Hello - World!")
          Behaviors.same
        case MessagePosted(screenName, message) =>
          context.system.log.info(s"chatRoom - MessagePosted  : $screenName | $message")
          context.system.log.info(s"message has been posted by '$screenName': $message")
          Behaviors.stopped
        }
      }


  val behaviorChatRoom: Behavior[Command] = chatRoom(List.empty)
/*
  val root: Behavior[akka.NotUsed] =
    Behaviors.setup. { ctx =>
      ctx.system.log.info(s"chatRoom - root ! ")
      val chatRoom  : ActorRef[Command]      = ctx.spawn(ChatRoom.behaviorChatRoom, "chatroom")
      val gabblerRef: ActorRef[SessionEvent] = ctx.spawn(ChatRoom.gabbler, "gabbler")
      chatRoom ! GetSession("ol’ Gabbler", gabblerRef)

      Behaviors.empty
    }
*/

  val main: Behavior[akka.NotUsed] =
  Behaviors.setup[akka.NotUsed] { ctx =>
    ctx.system.log.info(s"chatRoom - main ! ")
    val chatRoom   = ctx.spawn(ChatRoom.behaviorChatRoom, "chatroom")
    val gabblerRef = ctx.spawn(ChatRoom.gabbler, "gabbler")
//    val gabblerRef = ctx.spawn(gabbler, "gabbler")
    ctx.watch(gabblerRef)
    chatRoom ! GetSession("ol’ Gabbler", gabblerRef)

    Behaviors.receive[akka.NotUsed] {
      ctx.system.log.info(s"chatRoom - main 01 ! ")
      (_, _) =>
        Behaviors.unhandled
    } receiveSignal {
      case (ctx, Terminated(ref)) =>
        import example.Main._
        ctx.system.log.info(s"chatRoom - stopped ! ")
        actorSystem.terminate()
        Behaviors.stopped
    }
  }

}
