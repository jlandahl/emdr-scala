package org.landahl.emdr.actors

import akka.actor.{ Actor, ActorRef, Props, ActorLogging }

import org.landahl.emdr.converters.JsonToUUDIF
import org.landahl.emdr.model.UUDIF
import org.landahl.emdr.util.Zip

class UUDIFProcessor(historyStore: ActorRef, orderProcessor: ActorRef) extends Actor  with ActorLogging {
  def receive = {
    case data: Array[Byte] => {
      val json = Zip.inflate(data)
      processJson(json)
    }
    case json: String => processJson(json)
    case x => log.debug("Received unknown message: ", x)
  }

  def processJson(json: String) = {
    val uudif = JsonToUUDIF.extract(json)
    uudif.resultType match {
      case "history" => historyStore ! uudif
      case "orders"  => orderProcessor ! uudif
    }
  }
}

object UUDIFProcessor {
  def props(historyStore: ActorRef, orderProcessor: ActorRef) = Props(new UUDIFProcessor(historyStore, orderProcessor))
}
