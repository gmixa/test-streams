package de.mixas

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Source, Sink}

import scala.concurrent.duration.FiniteDuration

object Main extends App{
  implicit val actorSystem = ActorSystem("test-streams")
  implicit val actorMaterializer = ActorMaterializer()

  val sink = Sink foreach println _
  val delay = FiniteDuration(5, TimeUnit.SECONDS)
  val repeat = FiniteDuration(10, TimeUnit.SECONDS)
  val source = Source(delay,repeat, "Tick")

  source.runWith(sink)
}
