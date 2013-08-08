package org.landahl.emdr.actors

import akka.actor.{ Actor, ActorRef, Props }
import kafka.consumer.{ Consumer, ConsumerConfig }
import kafka.utils.ZkUtils

import org.landahl.emdr.Settings
import org.landahl.emdr.util.Zip

class KafkaConsumer(processor: ActorRef) extends Actor {
  case object Poll

  val settings = Settings(context.system)

  def consumerConfig = {
    val props = new java.util.Properties
    props.put("zookeeper.connect", settings.zookeeperURI)
    props.put("group.id", settings.kafkaGroupId)
    props.put("zookeeper.session.timeout.ms", "400")
    props.put("zookeeper.sync.time.ms", "200")
    props.put("auto.commit.interval.ms", "1000")
    props.put("auto.offset.reset", settings.kafkaOffset)
    new ConsumerConfig(props)
  }
  val consumer = Consumer.create(consumerConfig)
  val topicCountMap = Map(settings.kafkaTopic -> 1)
  val consumerMap = consumer.createMessageStreams(topicCountMap)
  val stream = consumerMap(settings.kafkaTopic)(0)
  val iterator = stream.iterator

  override def preStart = {
    if (settings.kafkaOffset == "smallest")
      ZkUtils.maybeDeletePath(settings.zookeeperURI, "/consumers/" + settings.kafkaGroupId)
    self ! Poll
  }

  def receive = {
    case Poll => poll

    case data: Array[Byte] => {
      val json = Zip.inflate(data)
      processor ! json
    }

    case _ =>
  }

  private def poll = {
    self ! iterator.next.message
    self ! Poll
  }
}

object KafkaConsumer {
  def props(processor: ActorRef) = Props(new KafkaConsumer(processor))
}
