name := "HelloAkka"

version := "0.1"

//scalaVersion := "2.12.4"
scalaVersion := "2.12.5"

//val akkaVersion = "2.5.7"
//val akkaVersion = "2.5.9"
//val akkaVersion = "2.5.11"
val akkaVersion = "2.5.12"
lazy val akkaHttpVersion = "10.1.1"
lazy val circeV = "0.9.0"


libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed"   % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"        % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.scalatest"     %% "scalatest" % "3.0.4",
  "de.heikoseeberger" %% "akka-http-circe" % "1.20.1",
  "io.circe" %% "circe-core"    % circeV,
  "io.circe" %% "circe-generic" % circeV,
  "io.circe" %% "circe-parser"  % circeV,
  "io.circe" %% "circe-optics"  % circeV,
  "com.iheart" %% "ficus" % "1.4.3",
  "org.apache.httpcomponents" % "httpclient" % "4.5.5",
//  "org.http4s" %% "http4s-core" % "0.1.0",
  "org.http4s" %% "http4s-blaze-client" % "0.18.12",
  "org.http4s" %% "http4s-client" % "0.18.12",
  "org.http4s" %% "http4s-dsl" % "0.18.12", // % Test,
  "org.http4s" %% "http4s-circe" % "0.18.12",
  "io.scalaland" %% "chimney" % "0.1.10",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"

).map(_.exclude("org.slf4j", "slf4j-log4j12"))


val scalazVersion = "7.1.0"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-concurrent" % "7.3.0-M24"
//  "org.scalaz" %% "scalaz-core" % scalazVersion,
//  "org.scalaz" %% "scalaz-effect" % scalazVersion
//  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
//  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion //% "test"
)




