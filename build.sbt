name := "HelloAkka"

version := "0.1"

//scalaVersion := "2.12.4"
scalaVersion := "2.12.5"

//val akkaVersion = "2.5.7"
//val akkaVersion = "2.5.9"
val akkaVersion = "2.5.11"


libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit-typed" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.4",
  "com.typesafe.akka" %% "akka-testkit-typed" % "2.5.11" % Test

).map(_.exclude("org.slf4j", "slf4j-log4j12"))



