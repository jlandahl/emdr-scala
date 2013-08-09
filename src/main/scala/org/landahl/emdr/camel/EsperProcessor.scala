package org.landahl.emdr.camel

import scala.util.control.Exception.catching
import scala.collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, ConfigException}
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.camel.main.Main
import org.apache.activemq.camel.component.ActiveMQComponent

object EsperProcessor extends App with RouteBuilderSupport {
  val config = ConfigFactory.load
  val rowsetFilter = catching(classOf[ConfigException.Missing]) opt config.getString("emdr.rowset-filter")
  val orderFilter =  catching(classOf[ConfigException.Missing]) opt config.getString("emdr.order-filter")
  EsperStatements.createStatements(rowsetFilter, orderFilter)
  val main = new Main
  //main.enableHangupSupport
  main.addRouteBuilder(new EsperRouting(config))
  main.bind("activemq", ActiveMQComponent.activeMQComponent(config.getString("activemq.url")))
  main.run
}
