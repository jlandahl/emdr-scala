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
    // data in Orders1 should be filtered out, so nothing should end up in either mock endpoint
    getMockEndpoint("mock:market-report").expectedMessageCount(0)
    getMockEndpoint("mock:history").expectedMessageCount(0)
    template.sendBody("direct:emdr.json", model.SampleData.orders1)
    assertMockEndpointsSatisfied
  }
  
  @Test
  def testJsonRouteOrders4 {
    // data in Orders4 should pass the filter and make it to the mock:market-report endpoint
    getMockEndpoint("mock:market-report").expectedMessageCount(1)
    getMockEndpoint("mock:history").expectedMessageCount(0)
    template.sendBody("direct:emdr.json", model.SampleData.orders4)
    assertMockEndpointsSatisfied
  }
  
  @Test
  def testJsonRouteHistory1 {
    // data in History1 should make it to the mock:history endpoint
    getMockEndpoint("mock:market-report").expectedMessageCount(0)
    getMockEndpoint("mock:history").expectedMessageCount(1)
    template.sendBody("direct:emdr.json", model.SampleData.history1)
    assertMockEndpointsSatisfied
  }
}
