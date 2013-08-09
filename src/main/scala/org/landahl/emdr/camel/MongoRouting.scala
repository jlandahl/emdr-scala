package org.landahl.emdr.camel

import org.apache.camel.scala.dsl.builder.RouteBuilder

import org.landahl.emdr.model.MarketReport
import org.landahl.emdr.converters.MarketReportToMongoUpdate

class MongoRouting extends RouteBuilder {
  from("direct:market-reports-to-mongo") ==> {
    split(_.in[List[MarketReport]]) {
      process { exchange => MarketReportToMongoUpdate.camelMongoUpdate(exchange) }
      to("mongodb:db?database=eve&collection=market_reports&operation=update")
    }
  }
}
