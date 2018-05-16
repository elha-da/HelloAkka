package reqResp


import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object Hal {


  sealed trait HalCommand

  case class OpenThePodBayDoorsPlease(respondTo: ActorRef[HalResponse]) extends HalCommand

  case class HalResponse(message: String)

  val halBehavior
  : Behavior[HalCommand] =
    Behaviors.receive[HalCommand] { (ctx, msg) ⇒
      msg match {
        case OpenThePodBayDoorsPlease(respondTo) ⇒
          respondTo ! HalResponse("I'm sorry, Dave. I'm afraid I can't do that.")
          Behaviors.same
      }
    }
}
