package org.landahl.emdr

import com.typesafe.config.ConfigFactory
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport

object Processor extends App with RouteBuilderSupport {
  import org.apache.camel.main.Main
  import org.apache.activemq.camel.component.ActiveMQComponent

  val config = ConfigFactory.load
  val main = new Main
  //main.enableHangupSupport
  main.addRouteBuilder(new Routes(config))
  main.bind("activemq", ActiveMQComponent.activeMQComponent(config.getString("activemq.url")))
  main.run
}
