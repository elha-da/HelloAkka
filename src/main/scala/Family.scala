package com.familyDependence

import akka.actor.{Actor, Props}


object Family {

  class Parent extends Actor {

    val child = context.actorOf(Props[Child], "child")
    var ponged = false

    context.system.log.info("parent actor")
    def receive = {
      case "pingit" =>
        context.system.log.info("Parent - pingit")
        child ! "ping"
      case "pong" =>
        context.system.log.info("Parent - pong")
        ponged = true
//        println(ponged)
        context.self ! "pongIt"
      case "pongIt" =>
        context.system.log.info("Parent - pongIt")
        child ! "pong"
    }
  }

  class Child extends Actor {
    context.system.log.info("child actor")
    def receive = {
      case "ping" =>
        context.system.log.info("Child - ping")
        context.parent ! "pong"
      case "pong" =>
        context.system.log.info("Child - pong")
        println("ping")
    }
  }

}