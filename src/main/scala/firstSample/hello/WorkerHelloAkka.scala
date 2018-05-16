package firstSample.hello

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object WorkerHelloAkka {

  sealed trait Command
  final case class HelloMsg(message: String) extends Command
  final case class CountMsg(message: Int) extends Command

//  def init(): Behavior[WorkerHelloAkka.Command] =
  val initBehavior: Behavior[WorkerHelloAkka.Command] =
    Behaviors.receive[Command] { (ctx, msg) =>
      msg match {
        case HelloMsg(msgTxt) =>
          ctx.system.log.info("Print new message String ")
          println(s"Hello $msgTxt")
          Behaviors.same

        case CountMsg(msgInt) =>
          ctx.system.log.info("Print new message Int ")
          println(s"Count $msgInt")
          Behaviors.same
      }
    }

}
