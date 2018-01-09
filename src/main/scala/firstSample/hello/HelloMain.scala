package firstSample.hello

import firstSample.WokerHelloAkka
import firstSample.WokerHelloAkka._

import akka.actor.ActorSystem
import akka.typed.{ActorRef}

object HelloMain extends App {

  import akka.typed.scaladsl.adapter._

  implicit val actorSystem: ActorSystem = ActorSystem("hello-World")

  val worker: ActorRef[WokerHelloAkka.Command] = actorSystem.spawn(WokerHelloAkka.init(), "worker")

  worker.tell(HelloMsg("akka \" tell \""))
  worker ! HelloMsg("akka \" ! \"")
  worker ! CountMsg(99)

  //shutdown actorsystem
//  actorSystem.terminate()
}
