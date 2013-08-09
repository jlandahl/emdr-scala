package org.landahl.emdr.actors

import akka.actor.{ Actor, Props, ActorLogging }
import kafka.javaapi.producer.Producer
import kafka.producer.{ ProducerConfig, KeyedMessage }

import org.landahl.emdr.Settings

class KafkaProducer extends Actor with ActorLogging {
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
      log.debug("Sending {} bytes to Kafka", data.length)
      producer.send(new KeyedMessage[Int, Array[Byte]](settings.kafkaTopic, data))
    }

    case x => log.warning("Received unknown message: ", x)
  }

}

object KafkaProducer {
  def props = Props(new KafkaProducer)
}
