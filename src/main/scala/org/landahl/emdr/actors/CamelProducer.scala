package org.landahl.emdr.actors

import akka.actor.{ Actor, Props }
import akka.camel.{ Producer, Oneway, CamelExtension }

class CamelProducer(uri: String) extends Actor with Producer with Oneway {
  def endpointUri = uri
}

object CamelProducer {
  def props(uri: String) = Props(classOf[CamelProducer], uri)
}