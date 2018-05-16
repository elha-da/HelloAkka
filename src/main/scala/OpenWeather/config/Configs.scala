package OpenWeather.config

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import OpenWeather.Manager.OauthPayload

object Configs {
  val config = ConfigFactory.load
  val manager: OauthPayload = config.as[OauthPayload]("manager")
  val openWeatherMapAppId: String = config.as[String]("openWeatherMapAppId")

}
