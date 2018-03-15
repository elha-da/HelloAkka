package com.familyDep.FamilyDep

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.Receptionist.Listing
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.dispatch.forkjoin.ThreadLocalRandom
import com.familyDep.FamilyDep.Parent._

object Child {

  sealed trait Command

  final case class pingMsgChild(message: String) extends Command

  final case class pongMsgChild(message: Int) extends Command

  //  private case class WrappedListingEvent(event: Listing[Parent.Command]) extends Command
  private case class WrappedListingEvent(event: Listing) extends Command

  val init
  : Behavior[Command] =
    Behaviors.setup[Command] { context =>
      context.system.log.info("Subscribe Child active behavior ")
      val ReceptionistAdapter: ActorRef[Listing] = context.messageAdapter(WrappedListingEvent.apply)
      context.system.receptionist ! Receptionist.Subscribe(Parent.ParentServiceKey, ReceptionistAdapter)

      startC(Vector.empty)
    }

  private def startC(parentActors: Vector[ActorRef[Parent.Command]])
  : Behavior[Child.Command] =
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case WrappedListingEvent(listing) => {
          listing match {
            case Parent.ParentServiceKey.Listing(services: Set[ActorRef[Parent.Command]]) =>
              //        case w:WrappedListingEvent =>
              ctx.system.log.info(s"new Parrent recived : $services")
              startC(services.toVector)
          }
        }
        case pingMsgChild(msgString) =>
          ctx.system.log.info(s"Child - ping: $msgString")
          ctx.system.log.info(s"Child - parentActors $parentActors")
//          val i = ThreadLocalRandom.current.nextInt(parentActors.size)
//          parentActors(i) ! pongMsgParent(0)
//          parentActors(0) ! pongMsgParent(0)
          if (parentActors.nonEmpty) {
            ctx.system.log.info(s"Child parentActors-nonEmpty : $parentActors")
            parentActors.last ! pongMsgParent(0)
          } else {
            ctx.system.log.info(s"Child parentActors-Empty : $parentActors")
            ctx.self ! pingMsgChild(msgString)
          }
          Behaviors.same

        case pongMsgChild(msgInt) =>
          ctx.system.log.info(s"Child - pong: $msgInt")
//          ctx.system.log.info("ping")
          Behaviors.stopped
      }
    }
}