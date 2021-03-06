package com.pingPong.unTypeToType

import akka.actor.{Actor, Props, Terminated}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter._

object MyUntyped1 {
  def myProps(): Props = Props(new MyUntyped1)
}

class MyUntyped1 extends Actor {

  // context.spawn is an implicit extension method
  val second: ActorRef[MyTyped1.Command] = context.spawn(MyTyped1.behavior, "second")

  // context.watch is an implicit extension method
  context.watch(second)

  // self can be used as the `replyTo` parameter here because
  // there is an implicit conversion from akka.Behaviors.ActorRef to
  // akka.Behaviors.typed.ActorRef
  second ! MyTyped1.Ping(self)

  override def receive = {
    case MyTyped1.Pong =>
      println(s"$self got PongF from ${sender()}")
      // context.stop is an implicit extension method
      context.stop(second)

    case Terminated(ref) =>
      println(s"$self observed termination of $ref")
      context.stop(self)
  }

}
