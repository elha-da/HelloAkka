package com.familyDep.FamilyDep

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor

import com.familyDep.FamilyDep.Parent._

object Child {

  sealed trait Command
  final case class pingMsgChild(message: String) extends Command
  final case class pongMsgChild(message: Int) extends Command

  def startC()
  : Behavior[Child.Command] =
    Actor.immutable[Command] { (context, msg) =>
      msg match {
        case pingMsgChild(msgString) =>
          context.system.log.info(s"Child - ping: $msgString")
          val act: ActorRef[Parent.Command] = context.spawn(Parent.startP(context.self), "actorChild")
          act ! pongMsgParent(0)
          Actor.same

        case pongMsgChild(msgInt) =>
          context.system.log.info(s"Child - pong: $msgInt")
          println("ping")
          Actor.same
      }
    }
}