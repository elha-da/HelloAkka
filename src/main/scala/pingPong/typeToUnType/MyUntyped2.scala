package pingPong.typeToUnType

import akka.actor.Props

object MyUntyped2 {
  def myProps(): Props = Props(new MyUntyped2)
}

class MyUntyped2 extends akka.actor.Actor {

  override def receive = {
    case MyTyped2.Ping(replyTo) =>
      // we use the replyTo ActorRef in the message,
      // but could use sender() if needed and it was passed
      // as parameter to tell
      println(s"$self got Ping from ${sender()}")
      replyTo ! MyTyped2.Pong
  }
}
