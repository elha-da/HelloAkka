package pingPong.withReceptionist

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.Receptionist.Listing
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop, PreRestart, Terminated}
//import pingPong.withReceptionist.First._
//import pingPong.withReceptionist.Second._

object Guardian {

  sealed trait Command

  //  private final case class WrappedListingEvent(event: Listing[First.Ping])
  private case class WrappedListingEvent(event: Listing) extends Command

  def guardian()
  : Behavior[Command] =
    Behaviors.setup[Command] { ctx ⇒
      ctx.system.log.info("Guardian init child actor")
      val ReceptionistAdapter: ActorRef[Listing] = ctx.messageAdapter(WrappedListingEvent.apply)
      ctx.system.receptionist ! Receptionist.Subscribe(First.pingServiceKeyFirst, ReceptionistAdapter) //ctx.self)

      val ps: ActorRef[First.Ping] = ctx.spawnAnonymous(First.init)
      ctx.watch(ps)

      Behaviors.immutable[Command] {
      //Behaviors.immutablePartial[Command] {
        case (_, WrappedListingEvent(listing)) => {
          listing match {
            case First.pingServiceKeyFirst.Listing(services: Set[ActorRef[First.Ping]]) =>
              if (services.nonEmpty) {
                ctx.system.log.info(s"Guardian First services-nonEmpty : $services ")
                services.foreach(ps ⇒ ctx.spawnAnonymous(Second.pinger(ps)))
              }
              else
                ctx.system.log.info(s"Guardian First services-Empty : $services")

              Behaviors.same
          }
        }
      } onSignal {
        case (_, PreRestart) =>
          ctx.system.log.info("Worker {} is RESTARTED", ctx.self)
          Behaviors.same
        case (_, PostStop) =>
          ctx.system.log.warning("Worker {} is STOPPED", ctx.self)
          Behaviors.same
        case (_, Terminated(`ps`)) ⇒
          ctx.system.log.info("Ping service has shut down")
          Behaviors.stopped
      }
    }
}
