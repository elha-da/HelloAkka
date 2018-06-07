/*
package testParentChild

import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.typed.scaladsl.Effects.Spawned
import akka.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import com.familyDep.FamilyDep.Parent
import com.familyDep.FamilyDep.Parent._
import org.scalatest.{BeforeAndAfterAll, WordSpec}


class AsyncTestingExampleSpec extends
  WordSpec
  with ActorTestKit
  with BeforeAndAfterAll {

  override protected def afterAll() = shutdownTestKit()

  val childActor = Behaviors.receive[String] { (_, _) ⇒
    Behaviors.same[String]
  }

  "A behaviorTestKit as parent Actor" should {
    "test its parent responses" in {
      val behaviorTestKit = BehaviorTestKit(Parent.init)
      val inbox = TestInbox[String]()
      behaviorTestKit.run(pingMsgParent("Hello"))
//      behaviorTestKit.expectEffect(Spawned(childActor, "child"))
      inbox.expectMessage("Hello")
    }
  }

}
*/
