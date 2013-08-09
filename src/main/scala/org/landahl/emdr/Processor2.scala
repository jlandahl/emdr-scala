package org.landahl.emdr

import akka.actor.ActorSystem
import org.landahl.emdr.actors.{EsperOrderProcessor, KafkaConsumer, UUDIFProcessor, HistoryStore, ZeroMQReceiver}

object Processor2 extends App {
  implicit val system = ActorSystem("EMDR")
  val historyStore = system.actorOf(HistoryStore.props, "historyStore")
  val orderProcessor = system.actorOf(EsperOrderProcessor.props, "orderProcessor")
  val uudifProcessor = system.actorOf(UUDIFProcessor.props(historyStore, orderProcessor), "uudifProcessor")

  val settings = Settings(system)
  settings.processingMethod match {
    case "direct" => system.actorOf(ZeroMQReceiver.props(uudifProcessor), "receiver")
    case "queued" => system.actorOf(KafkaConsumer.props(uudifProcessor), "queueConsumer")
  }
}
