package org.landahl.emdr

import java.util.{ List => JList }
import scala.collection.JavaConverters._
import com.typesafe.config.{ Config, ConfigFactory }
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.scala.dsl.builder.{ RouteBuilder, RouteBuilderSupport }
import org.junit.Test
import org.junit.Assert._
import org.landahl.emdr.model.{ UUDIF, Rowset, Order }
import org.landahl.emdr.model.SampleData
import org.landahl.emdr.converters.JsonToUUDIF

class TestTypeConverters extends CamelTestSupport with RouteBuilderSupport {
  def config = {
    System.setProperty("config.resource", "test.conf")
    ConfigFactory.load
  }

  override def createRouteBuilder = new RouteBuilder {
    "direct:json-to-uudif" as classOf[UUDIF] to "mock:uudif"

    "direct:uudif-to-rowsets" ==> {
      as(classOf[UUDIF])
      as(classOf[JList[Rowset]])
      split(body) to "mock:rowsets"
    }

    "direct:rowsets-to-orders" ==> {
      as(classOf[UUDIF])
      as(classOf[JList[Rowset]])
      split(body) {
        as(classOf[JList[Order]])
        split(body) to "mock:orders"
      }
    }
  }

  @Test
  def testJsonToUUDIF = {
    val endpoint = getMockEndpoint("mock:uudif")
    endpoint.expectedMessageCount(1)
    template.sendBody("direct:json-to-uudif", SampleData.orders4)
    assertMockEndpointsSatisfied
    val body = endpoint.getExchanges.asScala.head.getIn.getBody
    assertEquals(classOf[UUDIF], body.getClass)
  }

  @Test
  def testUUDIFToRowsets = {
    val endpoint = getMockEndpoint("mock:rowsets")
    endpoint.expectedMessageCount(3)
    template.sendBody("direct:uudif-to-rowsets", SampleData.orders1)
    assertMockEndpointsSatisfied
    val body = endpoint.getExchanges.asScala.head.getIn.getBody
    assertEquals(classOf[Rowset], body.getClass)
  }

  @Test
  def testRowsetsToOrders = {
    val endpoint = getMockEndpoint("mock:orders")
    endpoint.expectedMessageCount(10)
    template.sendBody("direct:rowsets-to-orders", SampleData.orders4)
    assertMockEndpointsSatisfied
    endpoint.getExchanges.asScala.foreach { exchange =>
      val body = exchange.getIn.getBody
      assertEquals(classOf[Order], body.getClass)
    }
  }
}
