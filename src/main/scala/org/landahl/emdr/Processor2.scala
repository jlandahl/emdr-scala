package org.landahl.emdr

import akka.actor.{ ActorSystem, Props }
import org.landahl.emdr.actors.{EsperOrderProcessor, KafkaConsumer, UUDIFProcessor, HistoryStore}
import akka.testkit.TestProbe

object Processor2 extends App {
  implicit val system = ActorSystem("EMDR")
  val historyStore = system.actorOf(HistoryStore.props)
  val orderProcessor = system.actorOf(EsperOrderProcessor.props)
  val uudifProcessor = system.actorOf(UUDIFProcessor.props(historyStore, orderProcessor))
  val queueConsumer = system.actorOf(KafkaConsumer.props(uudifProcessor))
}
