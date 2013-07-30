package org.landahl.emdr

import akka.actor.{ ActorSystem, Props }
import org.landahl.emdr.actors.{ KafkaConsumer, UUDIFProcessor, HistoryStore }
import akka.testkit.TestProbe

object Processor2 extends App {
  implicit val system = ActorSystem("EMDR")
  val historyStore = system.actorOf(HistoryStore.props)
  val processor = system.actorOf(UUDIFProcessor.props(historyStore))
  val queueConsumer = system.actorOf(KafkaConsumer.props(processor))
}
