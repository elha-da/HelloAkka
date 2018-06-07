package testingAkkaTyped

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import testingAkkaTyped.{ChildActor => Child}

object AkkaTypedActor {

  sealed trait Cmd

  case object CreateAnonymousChild extends Cmd

  case class CreateChild(childName: String) extends Cmd

  case class SayHelloToChild(childName: String) extends Cmd

  case object SayHelloToAnonymousChild extends Cmd

  case class SayHello(who: ActorRef[String]) extends Cmd

  case class FetchHello(what: String) extends Cmd

  case class EmitMessage(msg: String) extends Cmd

  case object SendToMe extends Cmd

  def start()
  : Behavior[Cmd] =
    Behaviors.setup[Cmd] { ctx =>
      ctx.system.log.info(s"start the Behavior")

      myBehavior
    }

  private val myBehavior
  : Behavior[Cmd] =
    Behaviors.receive[Cmd] { (ctx, msg) =>
      msg match {
        case CreateChild(name) ⇒
          ctx.spawn(Child.childActor, name)

        case CreateAnonymousChild ⇒
          ctx.spawnAnonymous(Child.childActor)

        case SayHelloToChild(childName) ⇒
          val child: ActorRef[String] = ctx.spawn(Child.init(), childName)
          child ! "hello"

        case SayHelloToAnonymousChild ⇒
          val child: ActorRef[String] = ctx.spawnAnonymous(Child.init())
          child ! "hello stranger"

        case SayHello(who) ⇒
          who ! "hello"

        case FetchHello(what) ⇒
          ctx.system.log.info(s"hello $what")

        case EmitMessage(msg) ⇒
//          ctx.system.log.error(s"receive : $msg")
          ctx.self ! FetchHello(msg)

        case SendToMe =>
          val random = new scala.util.Random
          val i = random.nextInt(10)
          if (i < 6) {
            ctx.system.log.info(s"$i")
            ctx.self ! EmitMessage(s"$i") //i.toString)
          }
          else {
            ctx.system.log.info(s"$i")
            ctx.self ! FetchHello(i.toString)
          }


      }
      Behaviors.same
    }


}
