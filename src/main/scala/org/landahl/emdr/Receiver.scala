package org.landahl.emdr

import akka.actor.Actor

class Receiver extends Actor {
  import akka.zeromq.{ZMQMessage, ZeroMQExtension, SocketType, Listener, Connect, SubscribeAll}

  ZeroMQExtension(context.system).newSocket(
      SocketType.Sub,
      Listener(self), 
      Connect("tcp://relay-us-central-1.eve-emdr.com:8050"), 
      SubscribeAll)

  def transformer = context.actorSelection("../transformer")
  
  def receive = {
    case message: ZMQMessage ⇒ {
      val data = message.frames(0).payload.toArray
      val json = inflate(data)
      transformer ! json
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
  import akka.actor.{ActorSystem, Props}
  val system = ActorSystem("EMDR")
  val transformer = system.actorOf(Props[Transformer], "transformer")
  val receiver = system.actorOf(Props[Receiver], "receiver")
}
