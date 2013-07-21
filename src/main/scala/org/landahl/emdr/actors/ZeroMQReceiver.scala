package org.landahl.emdr.actors

import akka.actor.{ Actor, ActorRef, Props }
import akka.zeromq.{ ZMQMessage, ZeroMQExtension, SocketType, Listener, Connect, SubscribeAll }

class ZeroMQReceiver(uri: String, queueProducer: ActorRef) extends Actor {
  ZeroMQExtension(context.system).newSocket(SocketType.Sub, Listener(self), Connect(uri), SubscribeAll)

  def receive = {
    case message: ZMQMessage => {
      val data = message.frames(0).toArray
      // pass the zipped data directly to the queue
      queueProducer ! data
    }
    case _ => {}
  }
}

object ZeroMQReceiver {
  def props(uri: String, queueProducer: ActorRef) = Props(classOf[ZeroMQReceiver], uri, queueProducer)
}