package roundRobin

import akka.actor.typed.scaladsl.Actor
import akka.actor.typed.{ActorRef, Behavior}

object ImmutableRoundRobin {

  def roundRobinBehavior[T](numberOfWorkers: Int, worker: Behavior[T]): Behavior[T] =
    Actor.deferred { ctx =>
      val workers = (1 to numberOfWorkers).map { n =>
        println(s"roundRobinBehavior $n")
        ctx.spawn(worker, s"worker-$n")
      }
      activeRoutingBehavior(index = 0, workers.toVector)
    }

  private def activeRoutingBehavior[T](index: Long, workers: Vector[ActorRef[T]]): Behavior[T] =
    Actor.immutable[T] { (ctx, msg) =>
      println(s"activeRoutingBehavior $index")

      workers((index % workers.size).toInt) ! msg
      activeRoutingBehavior(index + 1, workers)
    }
}