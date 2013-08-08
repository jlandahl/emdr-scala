package org.landahl.emdr.actors

import akka.actor.{ Actor, ActorRef, Props }
import org.landahl.emdr.converters.JsonToUUDIF
import org.landahl.emdr.model.UUDIF

class UUDIFProcessor(historyStore: ActorRef, orderProcessor: ActorRef) extends Actor {
  def receive = {
    case json: String => processJson(json)
    case _ =>
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
