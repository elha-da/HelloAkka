package com.familyDependence.FamilyDep

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor
import com.familyDependence.FamilyDep.Child._


object Parent {

  sealed trait Command
  final case class pingMsgParent(message: String) extends Command
  final case class pongMsgParent(message: Int) extends Command
  final case class pongItParent(message: Boolean) extends Command

  def startP(child: ActorRef[Child.Command]): Behavior[Parent.Command] =
    Actor.immutable[Command] { (context, msg) =>
      msg match {
        case pingMsgParent(msgString) =>
          context.system.log.info(s"Parent - pingit: $msgString")
//          val act = pingMsgChild("ping")
          child ! pingMsgChild("ping")
          Actor.same

        case pongMsgParent(msgInt) =>
          context.system.log.info(s"Parent - pong: $msgInt")
          val ponged = true
//        println(ponged)
          context.self ! pongItParent(ponged)
          Actor.same

        case pongItParent(msgBoolean) =>
          context.system.log.info(s"Parent - pongIt: $msgBoolean")
          child ! pongMsgChild(12)
          Actor.same
      }
    }
}
