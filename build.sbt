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
  "com.typesafe.akka" %% "akka-testkit-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.scalatest"     %% "scalatest"            % "3.0.4",
  "de.heikoseeberger" %% "akka-http-circe" % "1.20.1",
//  "com.typesafe.akka" %% "akka-testkit-typed" % "2.5.11" % Test
  "io.circe" %% "circe-core"    % circeV,
  "io.circe" %% "circe-generic" % circeV,
  "io.circe" %% "circe-parser"  % circeV,
  "io.circe" %% "circe-optics"  % circeV,
  "com.iheart" %% "ficus" % "1.4.3"

).map(_.exclude("org.slf4j", "slf4j-log4j12"))



