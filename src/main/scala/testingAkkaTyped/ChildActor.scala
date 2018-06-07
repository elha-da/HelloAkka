package testingAkkaTyped

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object ChildActor {

  def init() =
    Behaviors.setup[String] {
      ctx =>
        ctx.system.log.info(s"init Child Actor")

        childActor
    }

  val childActor
  : Behavior[String] =
    Behaviors.receive[String] {
      (ctx, msg) ⇒
        ctx.system.log.info(s"msg : $msg")
        Behaviors.same
    }

}
