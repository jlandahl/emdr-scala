package org.landahl.emdr

import akka.actor.ActorSystem
import org.landahl.emdr.actors.{ZeroMQReceiver, KafkaProducer}

object Receiver extends App {
  val system = ActorSystem("EMDR")
  val queueProducer = system.actorOf(KafkaProducer.props, "processor")
  val receiver = system.actorOf(ZeroMQReceiver.props(queueProducer), "receiver")
}
