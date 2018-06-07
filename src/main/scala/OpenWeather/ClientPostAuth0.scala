package OpenWeather


import OpenWeather.Manager.OauthPayload
import OpenWeather.config.Configs
import cats.effect.{IO, Sync}
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Status.{ClientError, Successful}
import org.http4s._
import org.http4s.circe._
import org.http4s.client.blaze.Http1Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._


object ClientPostAuth0 extends App with Http4sClientDsl[IO] {

  case class ManagerAuth(access_token: String, expires_in: Long, token_type: String)

  implicit def decoders[F[_] : Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

//  val auth: Map[String, String] = Map(
//    "client_id" -> "oLHki3D0I6DzPFV0PCVQeyme987aODXV",
//    "client_secret" -> "yFcyAl1Tiv_jKOUwy2FLSx_OfUQT-px8gXFo_QezSRhIaSUkU6aRT6HCK5_JNLzn",
//    "audience" -> "https://daoudi-el.eu.auth0.com/api/v2/",
//    "grant_type" -> "client_credentials"
//  )


  val managAuth: ManagerAuth = postResponse[ManagerAuth, OauthPayload]("https://daoudi-el.eu.auth0.com/oauth/token", Configs.manager)
  val managAuthSJ: Json = postResponseStatus[Json, Map[String, String]]("https://daoudi-el.eu.auth0.com/oauth/token", auth)

  import java.io._
  val pw = new PrintWriter(new File("../../dataTest/managAuthSJ.json" ))
  pw.write(managAuth.toString())
  pw.close

  //  implicit val tokenEntityDecoder : EntityDecoder[ManagerAuth] = jsonOf[ManagerAuth]

  def postResponse[M, O](url: String, o: O, token: Option[String] = None)(implicit enc: Encoder[O],
                                                                          dec: Decoder[M])
  : M = {

    val reqUrl: String = url

    val req: IO[Request[IO]] = token match {
//      case None => POST(Uri.fromString(url).getOrElse(Uri()), o.asJson)
//      case Some(t) => POST(Uri.fromString(url).getOrElse(Uri()), o.asJson).putHeaders(Header("X-Auth-Token", t))
      case None => POST(Uri.unsafeFromString(reqUrl), o.asJson)
      case Some(t) => POST(Uri.unsafeFromString(reqUrl), o.asJson).putHeaders(Header("X-Auth-Token", t))
    }

    val responseBody: IO[M] = Http1Client[IO]().flatMap { httpClient =>
      httpClient.expect(req)(jsonOf[IO, M])
    }
    println(responseBody.unsafeRunSync())
    responseBody.unsafeRunSync()
  }

  def postResponseStatus[M, O](url: String, o: O, token: Option[String] = None)(implicit enc: Encoder[O],
                                                                                dec: Decoder[M])
  : M = {

    //    implicit def decoders[F[_] : Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

    val req: IO[Request[IO]] = token match {
      case None => POST(Uri.unsafeFromString(url), o.asJson)
      case Some(t) => POST(Uri.unsafeFromString(url), o.asJson).putHeaders(Header("X-Auth-Token", t))
    }

    val responseBody: IO[Option[M]] = Http1Client[IO]().flatMap { httpClient =>
      httpClient.fetch(req) {
        case ClientError(response) =>
          response.status match {
//            case Status.NotFound =>
//              println(response)
//              println(s"Not Found")
//              IO(None)
//            case Status.BadRequest =>
//              println(s"Bad Request")
//              IO(None)
            case _ =>
//              println(response)
              println(s"Error Request")
              IO(None)
          }
        case Successful(response) =>
          response.status match {
            case Status.Ok =>
//              println(response)
              response.as[M].map(Some(_))

          }
      }
    }


    val r = responseBody.flatMap {
      case Some(m) =>
        println(IO(m).unsafeRunSync())
        IO(m)
    }

    r.unsafeRunSync()

  }

}