package example

import OpenWeather.{ClientPostAuth0, HelloAsk, Manager}
import akka.actor
import akka.actor.typed.receptionist.Receptionist.{Find, Register}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import com.familyDep.Family.Parent
import com.familyDep.FamilyDep.{Child, Parent}
import com.familyDep.FamilyDep.Parent.pingMsgParent
import firstSample.hello.WorkerHelloAkka
import firstSample.hello.WorkerHelloAkka._
import com.pingPong.unTypeToType._
import com.pingPong.typeToUnType._
import roundRobin.ImmutableRoundRobin
import com.msgProtocols.ChatRoom
import com.msgProtocols.ChatRoom._
import firstSample.{Greeter, WhoToGreet}
import pingPong.withReceptionist.First._
import pingPong.withReceptionist.{First, Guardian, Second, Third}
import reqResp.Dave

object Main extends App {

  import akka.actor.typed.scaladsl.adapter._

  implicit val actorSystem: ActorSystem = ActorSystem("hello-World")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val http: HttpExt = Http(actorSystem)

/*
  val greeter: actor.ActorRef = actorSystem.actorOf(Props[Greeter](), "greeter01")
  greeter ! WhoToGreet("all")
*/

/*
  val workerHelloAkka = actorSystem.spawn(WorkerHelloAkka.initBehavior, "worker-HelloAkka")
  workerHelloAkka.tell(HelloMsg("akka \" tell \""))
  workerHelloAkka ! HelloMsg("akka \" ! \"")
  workerHelloAkka ! HelloMsg("akka.")
  workerHelloAkka ! CountMsg(99)
*/

/*
  val myUntyped1 = actorSystem.actorOf(MyUntyped1.myProps(), "unTyped-Actor1")
*/

  // system.spawn is an implicit extension method
/*
  val myTyped2 = actorSystem.spawn(MyTyped2.behaviorTyped, "typed-Actor2")
*/


/*
  val immuRoundRobin= actorSystem.spawn(ImmutableRoundRobin.roundRobinBehavior(4, MyTyped2.behaviorTyped), "immuRoundRobin-Actor")
*/


/*
  val parent: actor.ActorRef = actorSystem.actorOf(Props[Parent](), "child2")
  parent ! "pingit"
*/

/*
  val parentDep = actorSystem.spawn(Parent.init(), "parent2")
*/

/*
  val parentDep = actorSystem.spawn(Parent.init, "parent2")
  println(s"parentDep : $parentDep")
  parentDep ! pingMsgParent("toto")
*/

/*
  val actChatRoom = actorSystem.spawn(ChatRoom.main, "ChatRoomDemo")
*/


/*
  actorSystem.spawn(Guardian.guardian(None, None), "guardian-Test")
*/

/*
  // Create the 'greeter' actor
  val greeter = actorSystem.actorOf(Props[Greeter], "greeter")
  // Send WhoToGreet Message to actor
  greeter ! WhoToGreet("Akka")
*/


//  val dave: ActorRef[Dave.DaveMessage] = actorSystem.spawn(Dave.daveBehavior(), "dave")

//  val execActor = actorSystem.spawn(HelloAsk.start(), "helloAsk-actor")


  // rub  : sbt run in terminal to import var from .envrc (direnv allow)
//  val managerActor = actorSystem.spawn(Manager.start(), "Open-Weather")

  ClientPostAuth0.managAuth
  //shutdown actorsystem
//  actorSystem.terminate()
}
