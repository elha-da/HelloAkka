package OpenWeather.model

import akka.actor.typed.ActorRef
import akka.actor.typed.{Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._
import io.circe._


object Indicator {

  sealed trait Command

  final case class GetNewIndicator(docHCurs: HCursor, sender: ActorRef[IndicatorC]) extends Command

  final case class IndicatorC(humidity: Long, pressure: Long, temp: Long, temp_max: Long, temp_min: Long) //extends Command


  implicit val decodeIndicator: Decoder[IndicatorC] = new Decoder[IndicatorC] {
    final def apply(c: HCursor): Decoder.Result[IndicatorC] =
      for {
        humidity <- c.downField("humidity").as[Long]
        pressure <- c.downField("pressure").as[Long]
        temp <- c.downField("temp").as[Float]
        temp_max <- c.downField("temp_max").as[Float]
        temp_min <- c.downField("temp_min").as[Float]
      } yield {
        new IndicatorC(humidity, pressure, Math.round(temp), Math.round(temp_max), Math.round(temp_min))
      }
  }

  def supervised()
  : Behavior[Command] =
    Behaviors.supervise {
      start()
    }.onFailure(
      SupervisorStrategy
        .restartWithBackoff(
          minBackoff = 500.millis,
          maxBackoff = 10.seconds,
          randomFactor = 0.1
        )
    )

  private def start():
  Behavior[Command] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("startUp Indicator")

      preActive()
    }

  private def preActive():
  Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      ctx.system.log.info("Indicator switch active behavior ")
      msg match {
        case GetNewIndicator(docHCurs, sender) =>
          val indicatorDecode = docHCurs.downField("main")
            .focus
            .map(e => e.as[IndicatorC])

          indicatorDecode.map {
            case Left(e) =>
              ctx.system.log.info(s"Decoding Indicator Failure: $e")
              None
            case Right(indicator) =>
              sender ! indicator
          }
      }
      Behavior.same
    }


}
