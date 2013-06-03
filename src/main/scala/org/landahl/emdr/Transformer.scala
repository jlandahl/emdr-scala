package org.landahl.emdr

import akka.actor.Actor
import model.UUDIF

class Transformer extends Actor {
  def receive = {
    case json: String => {
      val uudif = UUDIF.extract(json)
    }
    case _ => println _
  }
}
