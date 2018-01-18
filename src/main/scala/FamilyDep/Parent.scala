package com.familyDep.FamilyDep

import akka.actor.typed._
//{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Actor
import com.familyDep.FamilyDep.Child._


object Parent {

  sealed trait Command
  final case class pingMsgParent(message: String) extends Command
  final case class pongMsgParent(message: Int) extends Command
  final case class pongItParent(message: Boolean) extends Command

  def startP(child: ActorRef[Child.Command])
  : Behavior[Parent.Command] =
    Actor.immutable[Command] { (context, msg) =>
      msg match {
        case pingMsgParent(msgString) =>
          context.system.log.info(s"Parent - pingit: $msgString")
          child ! pingMsgChild("ping")
          Actor.same

        case pongMsgParent(msgInt) =>
          context.system.log.info(s"Parent - pong: $msgInt")
          val ponged = true
          context.self ! pongItParent(ponged)
          Actor.same

        case pongItParent(msgBoolean) =>
          context.system.log.info(s"Parent - pongIt: $msgBoolean")
          child ! pongMsgChild(12)
          Actor.same
      }
    }
}
