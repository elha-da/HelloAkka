package OpenWeather


import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AskPattern._
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._


object HelloAsk extends App {
  import akka.actor.typed.scaladsl.adapter._

  implicit val system: ActorSystem = ActorSystem("hello-ask")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val execActor = system.spawn(Execution.supervised(), "execution-actor")

}

object Execution {

  import HelloAsk.system

  sealed trait CommandE

  private case class GetActualWeather(wH: ActorRef[HelloWorld.Greet]) extends CommandE

  /**
    * timers Key
    */
  private case object InitTimerKey

  private case object FetchWeatherTimer

  def supervised()
  : Behavior[CommandE] =
    Behaviors.supervise {
      active()
    }.onFailure(
      SupervisorStrategy
        .restartWithBackoff(
          minBackoff = 500.millis,
          maxBackoff = 10.seconds,
          randomFactor = 0.1
        )
    )

  private def active():
  Behavior[CommandE] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("startUp Weather")

      val geeterActor: ActorRef[HelloWorld.Greet] = ctx.spawn(HelloWorld.greeter, "helloGreeter")
      preActive(geeterActor)
    }

  private def preActive(geeterActor: ActorRef[HelloWorld.Greet])
  : Behavior[CommandE] =
    Behaviors.withTimers {
      print(s"start Timers")
      timers =>
        timers.startSingleTimer(InitTimerKey,
          GetActualWeather(geeterActor),
          FiniteDuration(1, "second"))
        timers.startPeriodicTimer(FetchWeatherTimer, GetActualWeather(geeterActor), 2.second)

        start()
    }

  private def start()
  : Behavior[CommandE] =
    Behaviors.receive { (context, msg) =>
      context.system.log.info(s"active exec !")
      msg match {
        case GetActualWeather(geeterActor) =>

          implicit val timeout: Timeout = 5.seconds
          implicit val scheduler = system.scheduler
          // the response callback will be executed on this execution context
          implicit val ec = system.dispatcher

          val future: Future[HelloWorld.Greeted] = geeterActor ? (HelloWorld.Greet("world", _))

          for {
            greeting ← future.recover {
              case ex ⇒ ex.getMessage
            }
            done ← {
              println(s"result: $greeting")
              system.terminate()
            }
          } println("system terminated")

      }
      Behaviors.same
    }

}


object HelloWorld {

  sealed trait Command

  final case class Greet(whom: String, replyTo: ActorRef[Greeted]) extends Command

  final case class Greeted(whom: String) extends Command

  val greeter
  : Behavior[Greet] =
    Behaviors.receive[Greet] { (ctx, msg) ⇒
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
