package org.landahl.emdr.actors

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.{Actor, ActorRef, Props, ActorLogging}
import kafka.consumer.{ Consumer, ConsumerConfig, Whitelist }
import kafka.utils.ZkUtils

import org.landahl.emdr.Settings
import org.landahl.emdr.util.Zip

class KafkaConsumer(processor: ActorRef) extends Actor with ActorLogging {
  case object Poll

  val settings = Settings(context.system)

  def consumerConfig = {
    val props = new java.util.Properties
    props.put("zookeeper.connect", settings.zookeeperURI)
    props.put("group.id", settings.kafkaGroupId)
    props.put("zookeeper.session.timeout.ms", "400")
    props.put("zookeeper.sync.time.ms", "200")
    props.put("auto.commit.enable", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("auto.offset.reset", settings.kafkaOffset)
    new ConsumerConfig(props)
  }
  lazy val consumer = Consumer.create(consumerConfig)
  lazy val topicCountMap = Map(settings.kafkaTopic -> 1)
  lazy val consumerMap = consumer.createMessageStreams(topicCountMap)
  lazy val stream = consumerMap(settings.kafkaTopic)(0)
  lazy val iterator = stream.iterator

  override def preStart = {
    super.preStart
    if (settings.kafkaOffset == "smallest") {
      log.debug("Resetting ZooKeeper consumer entry for group {}", settings.kafkaGroupId)
      ZkUtils.maybeDeletePath(settings.zookeeperURI, "/consumers/" + settings.kafkaGroupId)
    }
    self ! Poll
  }

  def receive = {
    case Poll => poll

    case data: Array[Byte] => {
      log.debug("Inflating byte array")
      val json = Zip.inflate(data)
      log.debug("Passing JSON to processor")
      processor ! json
    }

    case x => log.warning("Received unknown message: ", x)
  }

  private def poll = {
    import ExecutionContext.Implicits.global
    Future {
      log.debug("polling")
      iterator.next.message
    } onComplete { f =>
      self ! f.get
      self ! Poll
    }
  }
}

object KafkaConsumer {
  def props(processor: ActorRef) = Props(new KafkaConsumer(processor))
}
