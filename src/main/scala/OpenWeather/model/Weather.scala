package OpenWeather.model

import akka.actor.typed.ActorRef
import akka.actor.typed.{Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._
import io.circe._

import io.circe.generic.auto._


object Weather {

  sealed trait Command

  final case class GetNewWeather(docHCurs: HCursor, sender: ActorRef[WeatherC]) extends Command

  final case class WeatherC(description: String, icon: String, id: Long, main: String) //extends Command

  //  final case class Wind(speed: Long, deg: Long)


  /**
    * replaced by "heikoseeberger" dependence
    */
  //  implicit val decodeWeather: Decoder[WeatherC] = new Decoder[WeatherC] {
//    final def apply(c: HCursor): Decoder.Result[WeatherC] =
//      for {
//        description <- c.downField("description").as[String]
//        icon <- c.downField("icon").as[String]
//        id <- c.downField("id").as[Long]
//        main <- c.downField("main").as[String]
//      } yield {
//        new WeatherC(description, icon, id, main)
//      }
//  }

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
      ctx.system.log.info("startUp Weather")
      preActive()
    }

  private def preActive():
  Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      ctx.system.log.info("Weather switch active behavior ")
      msg match {
        case GetNewWeather(docHCurs, sender) =>
          val weatherDecode = docHCurs.downField("weather")
            .focus
            .flatMap(o => o.asArray)
            .map(e => e.last.as[WeatherC])

          weatherDecode.map {
            case Left(e) =>
              ctx.system.log.info(s"Decoding Failure: $e")
              None
            case Right(weather) =>
              sender ! weather

          }

      }
      Behavior.same
    }


}

//{
//  "base": "stations",
//  "clouds": {
//    "all": 20
//  },
//  "cod": 200,
//  "coord": {
//    "lat": 43.61,
//    "lon": 3.88
//  },
//  "dt": 1525701600,
//  "id": 2992166,
//  "main": {
//    "humidity": 72,
//    "pressure": 1013,
//    "temp": 294.54,
//    "temp_max": 295.15,
//    "temp_min": 293.15
//  },
//  "name": "Montpellier",
//  "sys": {
//    "country": "FR",
//    "id": 5588,
//    "message": 0.0028,
//    "sunrise": 1525667304,
//    "sunset": 1525719259,
//    "type": 1
//  },
//  "visibility": 10000,
//  "weather": [
//  {
//    "description": "orage",
//    "icon": "11d",
//    "id": 211,
//    "main": "Thunderstorm"
//  }
//  ],
//  "wind": {
//    "deg": 200,
//    "speed": 3.6
//  }
//}