package org.landahl.emdr.camel

import com.typesafe.config.Config
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.landahl.emdr.converters.{MarketReportToMongoUpdate, HistoryToCube, MarketReportToCube, UUDIFToOrders}
import org.landahl.emdr.{converters, model}
import org.landahl.emdr.model.{MarketReport, UUDIF, Order}

class Routes(config: Config) extends RouteBuilder {
  import org.apache.camel.Exchange
  import org.apache.camel.model.dataformat.ZipDataFormat
  import scala.collection.JavaConversions._
  import model.{UUDIF, MarketReport, Order, HistoryRow}
  import converters.{MarketReportToCube, HistoryToCube, MarketReportToMongoUpdate}

  val errorURI = config.getString("error.output")
  val emdrInputURI = config.getString("emdr.input")
  val emdrJsonURI = config.getString("emdr.json")
  val historyOutputURI = config.getString("history.output")
  val marketReportURI = config.getString("market-report.output")

  val stationList = config.getList("order-filter.stations").map(_.render.toInt)

  val orderFilter = (order: Order) => {
    stationList contains order.stationID
  }

  errorHandler(deadLetterChannel(errorURI))

  //from(emdrInputURI) to("direct:decompress-json", "file:input/emdr-copies")
  from(emdrInputURI) to("direct:decompress-json")

  from("file:input/emdr?delay=5000") to("direct:decompress-json")
  from("file:input/json?delay=5000") to(emdrJsonURI)

  from("direct:decompress-json") ==> {
    // unzip (inflate) the EMDR data
    unmarshal(new ZipDataFormat)
    to(emdrJsonURI)
  }

  from(emdrJsonURI) ==> {
    // ensure the body starts as a string
    convertBodyTo(classOf[String])
    log("pre-uudif")
    // use the registered type converter (JsonToUUDIF) to convert the JSON to a UUDIF object
    convertBodyTo(classOf[UUDIF])
    log("post-uudif")
    choice {
      when (_.in[UUDIF].resultType == "history") to("direct:history")
      when (_.in[UUDIF].resultType == "orders")  to("direct:orders")
    }
  }

  from("direct:orders") ==> {
    log("orders1")
    // produce a list of Orders from the UUDIF object
    //process(e => e.in = asJavaList(MarketReport.fromUUDIF(e.in[UUDIF], orderFilter).toList))
    process(e => e.in = UUDIFToOrders.extractOrders(e.in[UUDIF]))
    log("orders2")
    // proceed only if we have a non-empty list of MarketReports
    when (_.in[List[MarketReport]] != Nil) to("direct:market-reports")
  }

  from("direct:market-reports") ==> {
    // send data to Cube collector
    process { exchange => exchange.in = MarketReportToCube.convert(exchange.in[List[MarketReport]]) } 
    to(marketReportURI)
  }

  from("direct:history") ==> {
    process { exchange => exchange.in = HistoryToCube.convert(exchange.in[UUDIF]) }
    to(historyOutputURI)
  }
}
