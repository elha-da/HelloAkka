import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import akka.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox, TestProbe}
import akka.testkit.typed.scaladsl.Effects.{Spawned, SpawnedAnonymous}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, OneInstancePerTest, WordSpec, WordSpecLike}
import testingAkkaTyped.AkkaTypedActor._
import testingAkkaTyped.{ChildActor, AkkaTypedActor => Typed}


class TestingTypedTest
  extends
    //    ActorTestKit
    TestKit(ActorSystem("testsystem"))
    with WordSpecLike
//    with ImplicitSender
    with BeforeAndAfterAll
    with OneInstancePerTest
{


//    override def afterAll() = shutdownTestKit()
  override def afterAll() {
    system.terminate()
  }


  "myBehavior" must {
    /**
      * Spawning children
      */
    "Spawning children with name" in {
      val testKit = BehaviorTestKit(Typed.start())
      testKit.run(Typed.CreateChild("child"))
      testKit.expectEffect(Spawned(ChildActor.childActor, "child"))
    }

    "Spawning children Anonymously" in {
      val testKit = BehaviorTestKit(Typed.start())
      testKit.run(CreateAnonymousChild)
      testKit.expectEffect(SpawnedAnonymous(ChildActor.childActor))
    }

    /**
      * Sending messages
      */
    "Sending messages - provides an ActorRef" in {
      val testKit: BehaviorTestKit[Typed.Cmd] = BehaviorTestKit(Typed.start())
      val inbox: TestInbox[String] = TestInbox[String]()
      testKit.run(SayHello(inbox.ref))
      inbox.expectMessage("hello")
    }

    "Sending messages by looking up the ‘TestInbox‘" in {
      val testKit = BehaviorTestKit(Typed.start())
      testKit.run(SayHelloToChild("child"))
      val childInbox = testKit.childInbox[String]("child")
      childInbox.expectMessage("hello")
    }

    "Sending messages by looking up the ‘TestInbox‘ For anonymous children" in {
      val testKit: BehaviorTestKit[Typed.Cmd] = BehaviorTestKit(Typed.start())
      testKit.run(SayHelloToAnonymousChild)
      // Anonymous actors are created as: $a $b etc
      val childInbox = testKit.childInbox[String](s"$$a")
      childInbox.expectMessage("hello stranger")
    }

    /**
      * Fetch messages
      */

    //    implicit val testActorSystem = {
    //      val config = ConfigFactory.parseString(
    //        """
    //         akka.loggers = [akka.testkit.TestEventListener]
    //      """)
    //      ActorSystem(name="testsystem", config=config)
    //    }

    //    implicit val actorSystem = ActorSystem("testsystem")

    "Fetching messages" in {
      /*
            val testKit: BehaviorTestKit[Typed.Cmd] = BehaviorTestKit(Typed.start(), "akkaTyped")
            val selfInbox = testKit.selfInbox()
      //      EventFilter.debug(message = "Hello World!" /*, occurrences = 1*/).intercept {
              testKit.run(FetchHello("inbox"))
      //      }
      */

      import akka.actor.typed.scaladsl.adapter._
      val akkaTyped = system.spawn(Typed.start(), "akkaTyped")
      EventFilter.debug(message = "Hello World!", occurrences = 1).intercept {
        akkaTyped ! FetchHello("hello")
      }
      expectMsg("some message")

    }

    /**
      * Emit messages to self
      */
    "Emitting messages" in {
      val testKit: BehaviorTestKit[Typed.Cmd] = BehaviorTestKit(Typed.start(), "akkaTyped")
      val selfInbox = testKit.selfInbox()
      testKit.run(EmitMessage("hello"))
      selfInbox.expectMessage(FetchHello("hello"))
    }

    /**
      * Send messages to self
      */
    "Sending messages" in {
      val testKit: BehaviorTestKit[Typed.Cmd] = BehaviorTestKit(Typed.start(), "akkaTyped")
      val selfInbox = testKit.selfInbox()
      EventFilter.warning().intercept {
        testKit.run(SendToMe)
      }

//      selfInbox.receiveMessage()
      selfInbox.expectMessage(FetchHello("6"))
//      selfInbox.expectMessage(EmitMessage("3"))
    }

  }

}
