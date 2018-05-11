package OpenWeather

import java.util.UUID

import example.Main._
import OpenWeather.model.{Weather}
import OpenWeather.model.Weather.{WeatherC}
import OpenWeather.model.Weather.GetNewWeather

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import io.circe.{HCursor, Json}
import io.circe.parser.parse

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object Manager {

  sealed trait Command

  private case class GetActualWeather(wH: ActorRef[Weather.Command]) extends Command


  /**
    * timers Key
    */
  private case object InitTimerKey

  private case object FetchWeatherTimer


  def start():
  Behavior[Command] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("startUp managerRef")
      val weatherActor: ActorRef[Weather.Command] = ctx.spawn(Weather.supervised(), s"weather${UUID.randomUUID()}")
      ctx.watch(weatherActor)

      preActive(weatherActor)
    }

  private def preActive(weatherA: ActorRef[Weather.Command])
  : Behavior[Command] =
    Behaviors.withTimers(timers => {
      timers.startSingleTimer(InitTimerKey,
        GetActualWeather(weatherA),
        FiniteDuration(1, "second"))
      timers.startPeriodicTimer(FetchWeatherTimer, GetActualWeather(weatherA), 10.second)

      activeBehavior()
    })


  private def activeBehavior()
  : Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      ctx.system.log.info("manager switch active behavior ")

      msg match {
        case GetActualWeather(askResponseTo) =>
          // asking someone requires a timeout and a scheduler
          // if the timeout hits without response the ask is failed with a TimeoutException
          implicit val timeout: Timeout = 5.seconds
          implicit val scheduler = actorSystem.scheduler
          // the response callback will be executed on this execution context
          implicit val ec = actorSystem.dispatcher

          ctx.system.log.info("manager switch active GetActualWeather ")

          val req = HttpRequest(
            method = HttpMethods.GET,
            uri = s"http://api.openweathermap.org/data/2.5/weather?q=Montpellier,FR&lang=fr&appid=2d92405c57690e69814a77b80e2184e3"
          )

          val respFuture: Future[String] = for {
            response <- Http().singleRequest(req)
            content <- Unmarshal(response.entity).to[String]

          } yield content

          respFuture.onComplete {
            case Success(result) =>
              val doc: HCursor = parse(result).getOrElse(Json.Null).hcursor
              val wResult: Future[Weather.Command] = askResponseTo ? (GetNewWeather(doc, _))

              wResult.onComplete {
                case Success(weather) ⇒
                  ctx.system.log.info(s"Yay, we got weather: $weather")

                case Failure(ex) ⇒
                  ctx.system.log.error(s"Boo! didn't get weather in time: $ex")
              }

            case Failure(e) =>
              ctx.system.log.error(s"Auth post failed: ${e.getMessage}")

          }


      }

      Behavior.same
    }
}
