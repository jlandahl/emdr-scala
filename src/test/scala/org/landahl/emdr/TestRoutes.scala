package org.landahl.emdr

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.junit.{Test, Before}

class TestRoutes extends CamelTestSupport with RouteBuilderSupport {
  def config = {
    System.setProperty("config.resource", "test.conf")
    ConfigFactory.load
  }

  override def createRouteBuilder = new Routes(config)

  @Test
  def testJsonRouteOrders1 {
    getMockEndpoint("mock:market-report").expectedMessageCount(0)
    getMockEndpoint("mock:history").expectedMessageCount(0)
    template.sendBody("direct:emdr.json", model.SampleData.orders1)
    assertMockEndpointsSatisfied
  }
  
  @Test
  def testJsonRouteOrders4 {
    getMockEndpoint("mock:market-report").expectedMessageCount(1)
    getMockEndpoint("mock:history").expectedMessageCount(0)
    template.sendBody("direct:emdr.json", model.SampleData.orders4)
    assertMockEndpointsSatisfied
  }
  
  @Test
  def testJsonRouteHistory1 {
    getMockEndpoint("mock:market-report").expectedMessageCount(0)
    getMockEndpoint("mock:history").expectedMessageCount(1)
    template.sendBody("direct:emdr.json", model.SampleData.history1)
    assertMockEndpointsSatisfied
  }
}
