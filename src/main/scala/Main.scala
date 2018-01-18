
import akka.actor.{ActorSystem, Props}
import akka.actor.typed.{ActorRef, Behavior}

import com.familyDep.Family.Parent
import com.familyDep.FamilyDep.{Child, Parent}
import com.familyDep.FamilyDep.Parent.pingMsgParent
import firstSample.hello.WokerHelloAkka
import firstSample.hello.WokerHelloAkka._
import pingPong.unTypeToType._
import pingPong.typeToUnType._
import roundRobin.ImmutableRoundRobin
import com.msgProtocols.ChatRoom
import com.msgProtocols.ChatRoom._

object Main extends App {

  import akka.actor.typed.scaladsl.adapter._

  implicit val actorSystem: ActorSystem =
      ActorSystem("hello-World")

//  val workerHelloAkka: ActorRef[WokerHelloAkka.Command] =
//      actorSystem.spawn(WokerHelloAkka.initBehavior, "worker-HelloAkka")

//  workerHelloAkka.tell(HelloMsg("akka \" tell \""))
//  workerHelloAkka ! HelloMsg("akka \" ! \"")
//  workerHelloAkka ! HelloMsg("akka.")
//  workerHelloAkka ! CountMsg(99)

//  val myUntyped1 = actorSystem.actorOf(MyUntyped1.myProps(), "unTyped-Actor1")

  // system.spawn is an implicit extension method
//  val myTyped2 = actorSystem.spawn(MyTyped2.behaviorTyped, "typed-Actor2")


//  val immuRoundRobin: ActorRef[MyTyped2.Command] =
//      actorSystem.spawn(ImmutableRoundRobin.roundRobinBehavior(4, MyTyped2.behaviorTyped), "immuRoundRobin-Actor")


//  val parent = actorSystem.actorOf(Props[Parent](), "child2")
//  parent ! "pingit"

/*
  val childDep = actorSystem.spawn(Child.startC, "child2")
  val parentDep = actorSystem.spawn(Parent.startP(childDep), "parent2")
  parentDep ! pingMsgParent("toto")
*/

//  val actChatRoom = actorSystem.spawn(ChatRoom.root, "chat-RoOm")
  val actChatRoom = actorSystem.spawn(ChatRoom.main, "ChatRoomDemo")



  //shutdown actorsystem
//  actorSystem.terminate()
}
