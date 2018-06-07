package OpenWeather


import OpenWeather.config.Configs
import example.Main._
import OpenWeather.model.{Indicator, Weather}
import OpenWeather.model.Weather._
import OpenWeather.model.Indicator._
import akka.actor.Scheduler
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.parser.parse

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
//import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
//import scala.concurrent.ExecutionContext.Implicits.global


object Manager {

  case class Meteo(description: String, temp:Long, humidity: Long, pressure: Long)

  sealed trait Command

  private case object GetActualMeteo extends Command

  private case object FetchTokenAuth0 extends Command

  /**
    * timers Key
    */
  private case object InitTimerKey

  private case object FetchWeatherTimer

  /**
    *
    * auth0
    */
  case class OauthPayload(
                           client_id: String,
                           client_secret: String,
                           audience: String,
                           grant_type: String
                         )

  case class ManagerAuth(access_token: String, expires_in: Long, token_type: String)

  def start():
  Behavior[Command] =
    Behaviors.setup { ctx =>
      ctx.system.log.info("startUp manager")
      implicit val weatherActor: ActorRef[Weather.Command] = ctx.spawn(Weather.supervised(), s"weather") //${UUID.randomUUID()}")
      implicit val indicatorActor: ActorRef[Indicator.Command] = ctx.spawn(Indicator.supervised(), s"indicator") //${UUID.randomUUID()}")

      ctx.watch(weatherActor)
      ctx.watch(indicatorActor)

      preActive() //weatherActor, indicatorActor)
    }

  private def preActive()( implicit
                                weatherA  : ActorRef[Weather.Command],
                                indicatorA: ActorRef[Indicator.Command]
  )
  : Behavior[Command] =
    Behaviors.withTimers(timers => {
      timers.startSingleTimer(InitTimerKey,
        GetActualMeteo,
        FiniteDuration(1, "second"))
      timers.startPeriodicTimer(FetchWeatherTimer, GetActualMeteo, 10.second)

      // asking someone requires a timeout and a scheduler
      // if the timeout hits without response the ask is failed with a TimeoutException
      implicit val timeout: Timeout = 5.seconds
      implicit val scheduler: Scheduler = actorSystem.scheduler
      // the response callback will be executed on this execution context
      implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

      activeBehavior()
    })


  private def activeBehavior()( implicit
                                weatherA  : ActorRef[Weather.Command],
                                indicatorA: ActorRef[Indicator.Command],
                                timeout: Timeout,
                                scheduler: Scheduler,
                                ec: ExecutionContextExecutor
  )
    : Behavior[Command] =
  Behaviors.receive { (ctx, msg) =>
      ctx.system.log.info("manager switch active behavior ")


      msg match {
        //        case GetActualMeteo(askResponseTo) =>
        case GetActualMeteo =>

          ctx.system.log.info("manager switch active GetActualMeteo ")

          val uriM = s"http://api.openweathermap.org/data/2.5/weather?q=Montpellier,FR&lang=fr&units=metric&type=accurate&mode=json&appid=${Configs.openWeatherMapAppId}"

          val req = HttpRequest(
            method = HttpMethods.GET,
            uri = uriM
          )

//          println(req)
//          val respFuture: Future[String] = for {
          val respFuture: Future[Json] = for {
            httpResp <- Http().singleRequest(req)
            content <- Unmarshal(httpResp.entity).to[Json]

          } yield content

          respFuture.onComplete {
            case Success(result) =>
//              val doc: HCursor = parse(result).getOrElse(Json.Null).hcursor
              val doc: HCursor = result.hcursor
              val indicatorResult: Future[IndicatorC] = indicatorA ? (GetNewIndicator(doc, _))
              val weatherResult  : Future[WeatherC]   = weatherA ? (GetNewWeather(doc, _))


              val m = for {
                w <- weatherResult
                i <- indicatorResult
              } yield (Meteo(w.description, i.temp, i.humidity, i.pressure))

              m.onComplete {
                case Success(rst) ⇒
                  ctx.system.log.info(s"Yay, we got weather: $rst")
                  ctx.self ! FetchTokenAuth0
                case Failure(ex) ⇒
                  ctx.system.log.error(s"Boo! didn't get weather in time: ${ex.getMessage}")
                  None
              }

            case Failure(e) =>
              ctx.system.log.error(s"Auth GET failed: ${e.getMessage}")
              None
          }


        /**
          * ========================================= Auth0 Token =========================================
          */
        case FetchTokenAuth0 =>
          ctx.system.log.info("Fetch Manager Token ")


          post[OauthPayload, ManagerAuth]("https://daoudi-el.eu.auth0.com/oauth/token", Configs.manager).onComplete {
            case Success(result) =>
              result match {
                case Right(auth) =>
                  ctx.system.log.info(s"$auth")
                case Left(e) =>
                  ctx.system.log.error(s"Could not auth: $e")
                  None
              }
            case Failure(e) =>
              ctx.system.log.error(s"Auth post failed: ${e.getMessage}")
              None
          }

          /*val respPostAuth0Token = for {
            entity <- Marshal(Configs.manager).to[RequestEntity]
            httpResponse <- http.singleRequest(HttpRequest(
              method = HttpMethods.POST,
              uri = "https://daoudi-el.eu.auth0.com/oauth/token",
              entity = entity
            ))

            resp <-
              if (httpResponse.status.isSuccess()) {
                ctx.system.log.info(s"http request : ${HttpRequest(
                  method = HttpMethods.POST,
                  uri = "https://daoudi-el.eu.auth0.com/oauth/token",
                  entity = entity
                )}")
                ctx.system.log.info(s"http response status is success: ${httpResponse.status}")
                Unmarshal(httpResponse.entity).to[ManagerAuth].map(Right(_))
              }
              else {
                ctx.system.log.error(s"http response status failed")
                Unmarshal(httpResponse.entity).to[Json].map(Left(_))
              }
          } yield resp

          respPostAuth0Token.onComplete {
            case Success(result) =>
              result match {
                case Right(auth) =>
                  ctx.system.log.info(s"$auth")
                case Left(e) =>
                  ctx.system.log.error(s"Could not auth: $e")
                  None
              }
            case Failure(e) =>
              ctx.system.log.error(s"Auth post failed: ${e}")
              None
          }*/
      }
      Behavior.same
    }

  protected def httpPostRequest(url: String, messageEntity: MessageEntity): HttpRequest =
    HttpRequest(method = HttpMethods.POST, uri = url, entity = messageEntity)

  def post[E, R](url: String, e: E)(
                                    implicit
                                    enc: Encoder[E],
                                    dec: Decoder[R],
                                    ec: ExecutionContextExecutor
  ): Future[Either[Json, R]] = {

    for {
      entity <- Marshal(e).to[RequestEntity]
      httpResponse <- http.singleRequest(httpPostRequest(url, entity))
      respFuture <-
      if (httpResponse.status.isSuccess()) {
        //        println(s"httpPostRequest : ${httpPostRequest(url, entity)}")
        Unmarshal(httpResponse.entity).to[R].map(Right(_))
      }
      else {
        Unmarshal(httpResponse.entity).to[Json].map(Left(_))
      }
    } yield respFuture

  }

  def get(url: String,
          connectTimeout: Int = 5000,
          readTimeout: Int = 5000,
          requestMethod: String = "GET") =
  {
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = scala.io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close
    content
  }

//  import java.io._
//  import org.apache.commons._
//  import org.apache.http._
//  import org.apache.http.client._
//  import org.apache.http.client.methods.HttpPost
//  import org.apache.http.impl.client.DefaultHttpClient
//  import java.util.ArrayList
//  import org.apache.http.message.BasicNameValuePair
//  import org.apache.http.client.entity.UrlEncodedFormEntity
//
//  def postHttpApache(url: String) = {
////    val url = "http://localhost:8080/posttest";
//
//    val post = new HttpPost(url)
//    post.addHeader("appid","YahooDemo")
//    post.addHeader("query","umbrella")
//    post.addHeader("results","10")
//
//    val client = new DefaultHttpClient
//    val params = client.getParams
//    params.setParameter("foo", "bar")
//
//    val nameValuePairs = new ArrayList[NameValuePair](1)
//    nameValuePairs.add(new BasicNameValuePair("registrationid", "123456789"))
//    nameValuePairs.add(new BasicNameValuePair("accountType", "GOOGLE"))
//    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//    // send the post request
//    val response = client.execute(post)
//    println("--- HEADERS ---")
//    response.getAllHeaders.foreach(arg => println(arg))
//  }
}
