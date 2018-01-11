package firstSample.hello

import akka.typed.Behavior
import akka.typed.scaladsl.Actor

object WokerHelloAkka {

  sealed trait Command
  final case class HelloMsg(message: String) extends Command
  final case class CountMsg(message: Int) extends Command

//  def init(): Behavior[WokerHelloAkka.Command] =
  val initBehavior: Behavior[WokerHelloAkka.Command] =
    Actor.immutable[Command] { (ctx, msg) =>
      msg match {
        case HelloMsg(msgTxt) =>
          ctx.system.log.info("Print new message String ")
          println(s"Hello $msgTxt")
          Actor.same

        case CountMsg(msgInt) =>
          ctx.system.log.info("Print new message Int ")
          println(s"Count $msgInt")
          Actor.same
      }
    }

}
