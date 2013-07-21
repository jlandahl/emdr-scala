package org.landahl.emdr

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props }
import akka.camel.CamelExtension
import org.apache.activemq.camel.component.ActiveMQComponent

import org.landahl.emdr.actors.{ ZeroMQReceiver, CamelProducer }

object Receiver extends App {
  val config = ConfigFactory.load
  val emdrURI = config.getString("emdr.uri")
  val queueURI = config.getString("emdr.input")

  val system = ActorSystem("EMDR")
  val camel = CamelExtension(system)
  camel.context.addComponent("activemq", ActiveMQComponent.activeMQComponent(config.getString("activemq.uri")))

  val queueProducer = system.actorOf(CamelProducer.props(queueURI), "queueProducer")
  val receiver = system.actorOf(ZeroMQReceiver.props(emdrURI, queueProducer), "receiver")
}