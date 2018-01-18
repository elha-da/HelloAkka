name := "HelloAkka"

version := "0.1"

scalaVersion := "2.12.4"

//val akkaVersion = "2.5.7"
val akkaVersion = "2.5.9"


libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit-typed" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.4"

).map(_.exclude("org.slf4j", "slf4j-log4j12"))



