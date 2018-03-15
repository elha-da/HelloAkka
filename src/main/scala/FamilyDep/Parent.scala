package com.familyDep.FamilyDep

import akka.actor.typed.{ActorRef, Behavior, PostStop, PreRestart, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist._
import com.familyDep.FamilyDep.Child._


object Parent {

  sealed trait Command

  final case class pingMsgParent(message: String) extends Command

  final case class pongMsgParent(message: Int) extends Command

  final case class pongItParent(message: Boolean) extends Command

  val ParentServiceKey = ServiceKey[Command]("familyDep-Parent")

  //  def init(child: ActorRef[Child.Command])
  def init()
  : Behavior[Parent.Command] =
    Behaviors.setup[Parent.Command] { ctx =>
      ctx.system.log.info("init & register Parent actor")
      ctx.system.receptionist ! Receptionist.Register(ParentServiceKey, ctx.self)

      val child = ctx.spawn(Child.init, "child2")
//      ctx.watch(child)

      startP(child)
    }

  private def startP(child: ActorRef[Child.Command])
  : Behavior[Parent.Command] =
    Behaviors.immutable[Command] { (context, msg) =>
      msg match {
        case pingMsgParent(msgString) =>
          context.system.log.info(s"Parent - msgString : $msgString")
          context.system.log.info(s"Parent - child : $child")
          child ! pingMsgChild("ping")
          Behaviors.same

        case pongMsgParent(msgInt) =>
          context.system.log.info(s"Parent - pong: $msgInt")
          val ponged = true
          context.self ! pongItParent(ponged)
          Behaviors.same

        case pongItParent(msgBoolean) =>
          context.system.log.info(s"Parent - pongIt: $msgBoolean")
          child ! pongMsgChild(12)
          Behaviors.same
      }
    }
}
