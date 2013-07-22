package org.landahl.emdr

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props }
import org.landahl.emdr.actors.{ ZeroMQReceiver, KafkaProducer }

object Receiver extends App {
  val config = ConfigFactory.load
  val emdrURI = config.getString("emdr.uri")
  val queueURI = config.getString("emdr.input")

  val system = ActorSystem("EMDR")
  val queueProducer = system.actorOf(Props[KafkaProducer])
  val receiver = system.actorOf(ZeroMQReceiver.props(emdrURI, queueProducer), "receiver")
}
