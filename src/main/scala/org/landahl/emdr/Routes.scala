package org.landahl.emdr

import com.typesafe.config.Config
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Routes(config: Config) extends RouteBuilder {
  import org.apache.camel.Exchange
  import org.apache.camel.model.dataformat.ZipDataFormat
  import scala.collection.JavaConversions._
  import model.{UUDIF, MarketReport, Order, HistoryRow}
  import converters.{MarketReportToCube, HistoryToCube}

  val errorURI = config.getString("error.output")
  val emdrInputURI = config.getString("emdr.input")
  val emdrJsonURI = config.getString("emdr.json")
  val historyOutputURI = config.getString("history.output")
  val marketReportURI = config.getString("market-report.output")

  val stationList = config.getList("order-filter.stations").map(_.render.toLong)

  val orderFilter = (order: Order) => {
    stationList contains order.stationID
  }
  
  errorHandler(deadLetterChannel(errorURI))

  from(emdrInputURI) ==> {
    // unzip (inflate) the EMDR data
    unmarshal(new ZipDataFormat)
    convertBodyTo(classOf[String])
    to(emdrJsonURI)
  }
    
  from(emdrJsonURI) ==> {
    // use the registered type converter (JsonToUUDIF) to convert the JSON to a UUDIF object
    convertBodyTo(classOf[UUDIF])
    choice {
      when (_.in[UUDIF].resultType == "history") to("direct:history")
      when (_.in[UUDIF].resultType == "orders")  to("direct:orders")
    }
  }

  from("direct:orders") ==> {
    // use the UUDIF object to produce a list of MarketReports, filtered by orderFilter
    process(e => e.in = asJavaList(MarketReport.fromUUDIF(e.in[UUDIF], orderFilter).toList))
    // proceed only if we have a non-empty list of MarketReports
    when (_.in[List[MarketReport]] != Nil) to("direct:market-reports")
  }
  
  from("direct:market-reports") to ("direct:market-reports-to-mongo", "direct:market-reports-to-cube")

  from("direct:history") to("direct:history-to-cube")
  
  from("direct:market-reports-to-mongo") ==> {
    // the registered type converter will automatically convert the MarketReport objects to DBObjects
    to("mongodb:db?database=eve&collection=market_reports&operation=insert")
  }

  from("direct:market-reports-to-cube") ==> {
    // send data to Cube collector
    process { exchange => exchange.in = MarketReportToCube.convert(exchange.in[List[MarketReport]]) } 
    to(marketReportURI)
  }

  from("direct:history-to-cube") ==> {
    process { exchange => exchange.in = HistoryToCube.convert(exchange.in[UUDIF]) }
    to(historyOutputURI)    
  }
}
