package org.landahl.emdr.actors

import akka.actor.{ Actor, ActorRef, Props, ActorLogging }
import akka.zeromq.{ ZMQMessage, ZeroMQExtension, Listener, Connect, SubscribeAll }

import org.landahl.emdr.Settings

class ZeroMQReceiver(processor: ActorRef) extends Actor with ActorLogging {
  val settings = Settings(context.system)

  ZeroMQExtension(context.system).newSubSocket(Connect(settings.emdrURI), Listener(self), SubscribeAll)

  def receive = {
    case message: ZMQMessage => {
      val data: Array[Byte] = message.frames(0).toArray
      processor ! data
    }
    case x => log.warning("Received unknown message: {}", x)
  }
}

object ZeroMQReceiver {
  def props(processor: ActorRef) = Props(new ZeroMQReceiver(processor))
}
