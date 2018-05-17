package reqResp

//import ReqRespTwoActors.Hal.{HalResponse, OpenThePodBayDoorsPlease}
//import akka.util.Timeout
//import akka.actor.typed.{ActorRef, Behavior}
//import akka.actor.typed.scaladsl.Behaviors
//import scala.concurrent.duration._
//import scala.util.{Failure, Success}

object ReqRespTwoActors {
//
//  object Hal {
//
//    sealed trait HalCommand
//
//    case class OpenThePodBayDoorsPlease(respondTo: ActorRef[HalResponse]) extends HalCommand
//
//    case class HalResponse(message: String)
//
//    val halBehavior
//    : Behavior[HalCommand] =
//      Behaviors.immutable[HalCommand] { (ctx, msg) ⇒
//        msg match {
//          case OpenThePodBayDoorsPlease(respondTo) ⇒
//            respondTo ! HalResponse("I'm sorry, Dave. I'm afraid I can't do that.")
//            Behaviors.same
//        }
//      }
//  }
//
//  object Dave {
//
//    sealed trait DaveMessage
//
//    // this is a part of the protocol that is internal to the actor itself
//    case class AdaptedResponse(message: String) extends DaveMessage
//
//    def daveBehavior(hal: ActorRef[Hal.HalCommand])
//    : Behavior[DaveMessage] =
//      Behaviors.setup[DaveMessage] { ctx ⇒
//
////        val hal: ActorRef[Hal.HalCommand] = ctx.spawn(Hal.halBehavior, "hal")
//
//        // asking someone requires a timeout, if the timeout hits without response
//        // the ask is failed with a TimeoutException
//        implicit val timeout: Timeout = 3.seconds
//
//        // Note: The second parameter list takes a function `ActorRef[T] => Message`,
//        // as OpenThePodBayDoorsPlease is a case class it has a factory apply method
//        // that is what we are passing as the second parameter here it could also be written
//        // as `ref => OpenThePodBayDoorsPlease(ref)`
//        ctx.ask(hal)(Hal.OpenThePodBayDoorsPlease) {
//          case Success(HalResponse(message)) ⇒ AdaptedResponse(message)
//          case Failure(ex) ⇒ AdaptedResponse("Request failed")
//        }
//
//        // we can also tie in request context into an interaction, it is safe to look at
//        // actor internal state from the transformation function, but remember that it may have
//        // changed at the time the response arrives and the transformation is done, best is to
//        // use immutable state we have closed over like here.
//        val requestId = 1
//        ctx.ask(hal)(Hal.OpenThePodBayDoorsPlease) {
//          case Success(HalResponse(message)) ⇒ AdaptedResponse(s"$requestId: $message")
//          case Failure(ex) ⇒ AdaptedResponse(s"$requestId: Request failed")
//        }
//
//        start()
//      }
//
//    def start()
//    : Behavior[DaveMessage] =
//      Behaviors.immutable { (ctx, msg) ⇒
//        msg match {
//          // the adapted message ends up being processed like any other
//          // message sent to the actor
//          case AdaptedResponse(msg) ⇒
//            ctx.log.info("Got response from hal: {}", msg)
//            Behaviors.same
//        }
//      }
//  }


}

