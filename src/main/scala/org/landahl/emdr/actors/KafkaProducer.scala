package org.landahl.emdr.actors

import akka.actor.{ Actor, Props }
import kafka.javaapi.producer.Producer
import kafka.producer.{ ProducerConfig, KeyedMessage }

import org.landahl.emdr.Settings

class KafkaProducer extends Actor {
  val settings = Settings(context.system)

  val producerConfig = {
    val props = new java.util.Properties
    props.put("serializer.class", "kafka.serializer.DefaultEncoder")
    props.put("metadata.broker.list", settings.kafkaBrokers)
    new ProducerConfig(props)
  }
  val producer = new Producer[Int, Array[Byte]](producerConfig)

  def receive = {
    case data: Array[Byte] => {
      producer.send(new KeyedMessage[Int, Array[Byte]](settings.kafkaTopic, data))
    }
    case _ =>
  }

}

object KafkaProducer {
  def props = Props(new KafkaProducer)
}
