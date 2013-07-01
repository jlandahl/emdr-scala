package org.landahl.emdr

import scala.collection.JavaConversions._
import com.typesafe.config.ConfigFactory
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.main.Main
import org.apache.activemq.camel.component.ActiveMQComponent

object EsperProcessor extends App with RouteBuilderSupport {
  val config = ConfigFactory.load
  EsperStatements.createStatements(config)
  val main = new Main
  //main.enableHangupSupport
  main.addRouteBuilder(new EsperRouting(config))
  main.bind("activemq", ActiveMQComponent.activeMQComponent(config.getString("activemq.url")))
  main.run
}
