package org.landahl.emdr

import akka.actor.{Actor, ActorRef}
import akka.camel.{Producer, Oneway}

class Receiver(queueProducer: ActorRef) extends Actor {
  import akka.zeromq.{ZMQMessage, ZeroMQExtension, SocketType, Listener, Connect, SubscribeAll}

  ZeroMQExtension(context.system).newSocket(
      SocketType.Sub,
      Listener(self), 
      Connect("tcp://relay-us-central-1.eve-emdr.com:8050"), 
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

class QueueProducer extends Actor with Producer with Oneway {
  def endpointUri = "activemq:queue:emdr"
}

object Receiver extends App {
  import akka.actor.{ActorSystem, Props}
  import akka.camel.CamelExtension
  import org.apache.activemq.camel.component.ActiveMQComponent

  val system = ActorSystem("EMDR")
  val camel = CamelExtension(system)
  camel.context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://10.1.1.12:61616"))
  val queueProducer = system.actorOf(Props[QueueProducer], "queueProducer")
  val receiver = system.actorOf(Props(new Receiver(queueProducer)), "receiver")
}
