package pingPong.withReceptionist

//import java.util.concurrent.Future

import akka.actor.Scheduler
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.Receptionist.Listing
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop, PreRestart, SupervisorStrategy, Terminated, _}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

import example.Main._

object Guardian {

  sealed trait Command

  //  private final case class WrappedListingEvent(event: Listing[First.PingF])
  private case class WrappedListingEvent(event: Listing) extends Command

  //  def supervised()
  //  : Behavior[Command] =
  //    Behaviors.supervise(guardian()).onFailure(SupervisorStrategy.restart)

  def guardian(firstActors: Option[ActorRef[First.PingF]], thirdActors: Option[ActorRef[Third.PingT]])
  : Behavior[Command] =
    Behaviors.setup[Command] { ctx =>
      ctx.system.log.info("Guardian init child actor")

      val firstActor: ActorRef[First.PingF] = ctx.spawn(First.supervised(), "First-guardian-Test")
      val thirdActor: ActorRef[Third.PingT] = ctx.spawn(Third.pingService, "Third-guardian-Test")

      val ReceptionistAdapter: ActorRef[Listing] = ctx.messageAdapter(WrappedListingEvent.apply)
      ctx.system.receptionist ! Receptionist.Subscribe(First.pingServiceKeyFirst, ReceptionistAdapter) //ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(Third.pingServiceKeyThird, ReceptionistAdapter) //ctx.self)

      ctx.watch(firstActor)
      ctx.watch(thirdActor)

      val selfActor = ctx.self

      Behaviors.receive[Command] {
        case (_, WrappedListingEvent(listing)) => {
          listing match {
            case First.pingServiceKeyFirst.Listing(services: Set[ActorRef[First.PingF]]) =>
              if (services.nonEmpty) {
                ctx.system.log.info(s"Guardian First services-nonEmpty : $services ")
//                val sec = ctx.spawnAnonymous(Second.pinger(firstActor))
                val sec = ctx.spawn(Second.pinger(services.head), "Anonymous-Second")

                guardian(Some(services.head), None)
              }
              else
                ctx.system.log.info(s"Guardian First services-Empty : $services")

            case Third.pingServiceKeyThird.Listing(services: Set[ActorRef[Third.Comm]]) =>
              if (services.nonEmpty) {
                ctx.system.log.info(s"Guardian Third services-nonEmpty : $services ")

                // asking someone requires a timeout and a scheduler
                // if the timeout hits without response the ask is failed with a TimeoutException
                implicit val timeout: Timeout = 5.seconds
                implicit val scheduler: Scheduler = actorSystem.scheduler
                // the response callback will be executed on this execution context
                implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
                services.foreach(
                  s => {
                    val f: Future[Third.PongTC] = s ? (Third.PingT(_))
                    f.onComplete {
                      case Success(rst) ⇒
                        ctx.system.log.info(s"Yay, we got response from Third: $rst")
                        guardian(None, Some(services.head))
                      case Failure(ex) ⇒
                        ctx.system.log.error(s"Boo! didn't get response in time from Third: ${ex.getMessage}")
                        None
                    }
                  }
                )
              }
              else
                ctx.system.log.info(s"Guardian Third services-Empty : $services")

          }
          Behaviors.same
        }
      } receiveSignal {
        case (_, PreRestart) =>
          ctx.system.log.info("Worker {} is RESTARTED", ctx.self)
          Behaviors.same
        case (_, PostStop) =>
          ctx.system.log.warning("Worker {} is STOPPED", ctx.self)
          Behaviors.same
        case (_, Terminated(`thirdActor`) | Terminated(`firstActor`)) ⇒
          ctx.system.log.info("services has shut down")
          actorSystem.terminate()
          Behaviors.stopped
      }
    }
}
