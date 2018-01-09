name := "HelloAkka"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.7"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-typed" % akkaVersion
).map(_.exclude("org.slf4j", "slf4j-log4j12"))