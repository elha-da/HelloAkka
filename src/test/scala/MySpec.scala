/*
package firstSample

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import com.familyDep.Family
import com.familyDep.FamilyDep
import com.familyDep.FamilyDep.Child._
import com.familyDep.FamilyDep.Parent._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
//import com.familyDep.Family._

import scala.concurrent.duration._


class MySpec() extends TestKit(ActorSystem("MySpec-test"))
                with ImplicitSender
                with WordSpecLike
                with Matchers
                with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val printer: ActorRef = system.actorOf(TestActors.echoActorProps, "printer")
  val workerForwarder: ActorRef = system.actorOf(TestActors.forwardActorProps(printer))

  "An printer actor" must {
    "send back messages unchanged by forworder actor" in {
      within(200 millis) {
        workerForwarder ! "some work"
        expectMsg("some work")
        expectNoMsg // will block for the rest of the 200ms
        Thread.sleep(300) // will NOT make this block fail
      }
    }
  }

  "An echo actor" must {
    "send back messages unchanged" in {
//      val printer: ActorRef = system.actorOf(TestActors.echoActorProps, "echo-hello")
      printer ! "hello world"
      expectMsg(100 millis, "hello world")
    }
  }

  "A TestProbe serving as parent" should {
    "test its child responses" in {
      val parent = TestProbe()
      val child = parent.childActorOf(Props[Family.Child], "child")
      parent.send(child, "ping")
      parent.expectMsg("pong")
    }
  }
/*
  "A TestProbe serving as parent in FamilyDep" should {
    "test its child FamilyDep responses" in {
      val parent = TestProbe()
      val child = parent.childActorOf(Props[FamilyDep.Child.Command], "child")
      parent.send(child, pingMsgParent("ping"))
      parent.expectMsg("pong")
    }
  }
*/


  "A TestProbe serving as parent Actor" should {
      "test its child responses double Child" in {
        val parent = system.actorOf(Props[Family.Parent], "doubleChild")
        val child1 = TestProbe()
//        val child2 = TestProbe()
//        parent ! ((child1, child2))
        parent ! "pingit"
//        child1.send(parent, "pingit")
        child1.expectMsg("ping")
//        child2.expectMsg(10.second, "ping")
      }
    }

}*/
