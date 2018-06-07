/*
package firstSampleTest

import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import akka.actor.{ActorKilledException, ActorSystem, Kill, Props}
import akka.testkit.typed.scaladsl.ActorTestKit
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike
import Greeter01Test._
import firstSample.{Greeter, WhoToGreet}
import io.akka.helloWorld.HelloWorld
import io.akka.helloWorld.HelloWorld.Greet

class Greeter01Test
  extends
    TestKit(testSystem)
//    ActorTestKit
    with WordSpecLike {

  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
//      val dispatcherId = CallingThreadDispatcher.Id
//      val props = Props[Greeter].withDispatcher(dispatcherId)
//      val greeter = system.actorOf(props)
//      EventFilter.info(/*message ="Hello World!",*/ occurrences = 1).intercept {
//        greeter ! WhoToGreet("World!")
//      }

        val greeterActor = system.actorOf(Props[Greeter])
        EventFilter(/*message ="Hello World!", occurrences = 1*/) intercept {
          greeterActor ! WhoToGreet("World!")
        }
    }

    "say Hello World! Greeting(\"World\") sent to it" in {
      import akka.actor.typed.scaladsl.adapter._

        val greeterActor = system.spawn(HelloWorld.greeter,"plouf")
        EventFilter.error(message ="NO Hello World!"/*, occurrences = 1*/) intercept {
          greeterActor ! Greet("World!")
        }
    }
  }
}

object Greeter01Test {
//  val testSystem = {
//    val config = ConfigFactory.parseString(
//      """
//         akka.loggers = [akka.testkit.TestEventListener]
//      """)
//    ActorSystem("testsystem", config)
//  }
}
*/
