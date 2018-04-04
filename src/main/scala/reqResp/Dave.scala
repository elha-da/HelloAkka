package reqResp

import Hal.HalResponse
import akka.util.Timeout
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Dave {

  sealed trait DaveMessage

  // this is a part of the protocol that is internal to the actor itself
  case class AdaptedResponse(message: String) extends DaveMessage

  case class ConcatResponse(resp: String) extends DaveMessage


  def daveBehavior()
  : Behavior[DaveMessage] =
    Behaviors.setup[DaveMessage] { ctx ⇒

      // asking someone requires a timeout, if the timeout hits without response
      // the ask is failed with a TimeoutException
      implicit val timeout: Timeout = 3.seconds

      // Note: The second parameter list takes a function `ActorRef[T] => Message`,
      // as OpenThePodBayDoorsPlease is a case class it has a factory apply method
      // that is what we are passing as the second parameter here it could also be written
      // as `ref => OpenThePodBayDoorsPlease(ref)`
      implicit val bufferSize: Int = (for {
        h <- (1 to 3)
        hal: ActorRef[Hal.HalCommand] = ctx.spawn(Hal.halBehavior, s"hal-$h")

        ask = ctx.ask(hal)(Hal.OpenThePodBayDoorsPlease) {
          case Success(HalResponse(message)) ⇒
            ConcatResponse(message + s"-$h")

          case Failure(ex) ⇒
            AdaptedResponse("Request failed" + s"-$h ")
        }

      } yield (ask)).size

      // we can also tie in request context into an interaction, it is safe to look at
      // actor internal state from the transformation function, but remember that it may have
      // changed at the time the response arrives and the transformation is done, best is to
      // use immutable state we have closed over like here.
      //      val requestId = 1
      //      ctx.ask(hal)(Hal.OpenThePodBayDoorsPlease) {
      //        case Success(HalResponse(message)) ⇒ AdaptedResponse(s"$requestId: $message")
      //        case Failure(ex) ⇒ AdaptedResponse(s"$requestId: Request failed")
      //      }

      val buf: ListBuffer[String] = ListBuffer.empty[String]
      start(buf)
    }

  def start(buf: ListBuffer[String])(implicit bufferSize: Int)
  : Behaviors.Immutable[DaveMessage] =
    Behaviors.immutable { (ctx, msg) ⇒
      msg match {
        case ConcatResponse(resp) ⇒
          buf += resp
          val newI = buf.size // i + 1
        val newBuffer = buf.mkString(" | ") //bufferTmp + resp

          if (newI == bufferSize) {
            ctx.self ! AdaptedResponse(newBuffer)
          }

          Behavior.same

        // the adapted message ends up being processed like any other
        // message sent to the actor
        case AdaptedResponse(msg) ⇒
          ctx.log.info("Got response from hal: {}", msg)
          Behaviors.same
      }
    }

}