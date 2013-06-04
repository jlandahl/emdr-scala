package org.landahl.emdr

import com.typesafe.config.Config
import akka.actor.{Actor, ActorRef}

class Receiver(config: Config, queueProducer: ActorRef) extends Actor {
  import akka.zeromq.{ZMQMessage, ZeroMQExtension, SocketType, Listener, Connect, SubscribeAll}

  ZeroMQExtension(context.system).newSocket(
      SocketType.Sub,
      Listener(self), 
      Connect(config.getString("receiver.url")), 
      SubscribeAll)

  def receive = {
    case message: ZMQMessage ⇒ {
      val data = message.frames(0).payload.toArray
      val json = inflate(data)
      queueProducer ! json
    }
    case _ => {}
  }

  def inflate(data: Array[Byte]) = {
    val baos = new java.io.ByteArrayOutputStream
    val ios = new java.util.zip.InflaterOutputStream(baos)
    ios.write(data, 0, data.length)
    ios.close
    baos.toString
  }
}

object Receiver extends App {
  import com.typesafe.config.ConfigFactory
  import akka.actor.{ActorSystem, Props}
  import akka.camel.{Producer, Oneway, CamelExtension}
  import org.apache.activemq.camel.component.ActiveMQComponent

  class QueueProducer extends Actor with Producer with Oneway {
    def endpointUri = config.getString("queue.url")
  }

  val config = ConfigFactory.load()
  val system = ActorSystem("EMDR")
  val camel = CamelExtension(system)
  camel.context.addComponent("activemq", ActiveMQComponent.activeMQComponent(config.getString("activemq.url")))
  val queueProducer = system.actorOf(Props[QueueProducer], "queueProducer")
  val receiver = system.actorOf(Props(new Receiver(config, queueProducer)), "receiver")
}
