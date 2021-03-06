package OpenWeather


import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AskPattern._
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

import example.Main._


/*object Main extends App {
  import akka.actor.typed.scaladsl.adapter._

  implicit val system: ActorSystem = ActorSystem("hello-ask")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val execActor = system.spawn(HelloAsk.active(), "execution-actor")

}*/

object HelloAsk {

//  import Main.actorSystem

  sealed trait CommandE

  private case class GetActualWeather(wH: ActorRef[HelloWorld.Command]) extends CommandE

  /**
    * timers Key
    */
  private case object InitTimerKey

  private case object SendGreetedTimer



  def start():
  Behavior[CommandE] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("startUp Weather")

      val geeterActor: ActorRef[HelloWorld.Command] = ctx.spawn(HelloWorld.supervised, "helloGreeter")
      ctx.watch(geeterActor)

      preActive(geeterActor)
    }

  private def preActive(geeterActor: ActorRef[HelloWorld.Command])
  : Behavior[CommandE] =
    Behaviors.withTimers {
      println(s"start Timers")
      timers =>
        timers.startSingleTimer(InitTimerKey,
          GetActualWeather(geeterActor),
//          FiniteDuration(1, "second"))
          1 second)
//        timers.startPeriodicTimer(SendGreetedTimer, GetActualWeather(geeterActor), 3.second)

        active()
    }

  private def active()
  : Behavior[CommandE] =
    Behaviors.receive { (context, msg) =>
      context.system.log.info(s"active HelloAsk !")
      msg match {
        case GetActualWeather(geeterActor) =>

          implicit val timeout: Timeout = 5.seconds
          implicit val scheduler = actorSystem.scheduler
          // the response callback will be executed on this execution context
          implicit val ec = actorSystem.dispatcher

          val future: Future[HelloWorld.Command] = geeterActor ? (HelloWorld.Greet("world", _))

          for {
            greeting ← future.recover {
              case ex ⇒ ex.getMessage
            }
            done ← {
              println(s"result: $greeting")
//              actorSystem.whenTerminated
              actorSystem.terminate()
            }
          } println("system terminated")

      }
      Behaviors.same
    }

}


object HelloWorld {

  sealed trait Command

  final case class Greet(whom: String, replyTo: ActorRef[Command]) extends Command

  final case class Greeted(whom: String) extends Command

  val supervised
  : Behavior[Command] =
    Behaviors.supervise {
      greeter()
    }.onFailure(
      SupervisorStrategy
        .restartWithBackoff(
          minBackoff = 500.millis,
          maxBackoff = 10.seconds,
          randomFactor = 0.1
        )
    )

  private def greeter()
  : Behavior[Command] =
    Behaviors.receive { (ctx, msg) ⇒
      msg match {
        case Greet(msg_whom, msg_replyTo) =>
          ctx.system.log.info(s"Hello ${msg_whom}!")
          msg_replyTo ! Greeted(msg_whom)

        case _ =>
          ctx.system.log.info(s"Other msg !!!")

      }
      Behaviors.same
    }
}
