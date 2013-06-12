package org.landahl.emdr

import com.typesafe.config.Config
import org.apache.camel.scala.dsl.builder.RouteBuilder

class Routes(config: Config) extends RouteBuilder {
  import org.apache.camel.Exchange
  import org.apache.camel.model.dataformat.ZipDataFormat
  import scala.collection.JavaConversions._
  import org.json4s.NoTypeHints
  import org.json4s.native.Serialization
  import org.json4s.native.Serialization.{write, writePretty}
  import model.{UUDIF, MarketReport, Order}

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
    to(emdrJsonURI)
  }
    
  from(emdrJsonURI) ==> {
    // convert the JSON to a UUDIF object
    process(e => e.in = model.UUDIF.extract(e.in[String]))
    choice {
      when (_.in[UUDIF].resultType == "history") to(historyOutputURI)
      when (_.in[UUDIF].resultType == "orders")  to("direct:orders")
    }
  }

  from("direct:orders") ==> {
    // use the UUDIF object to produce a list of MarketReports, filtered  by orderFilter
    process(e => e.in = MarketReport.fromUUDIF(e.in[UUDIF], orderFilter).toList)
    // proceed only if we have a non-empty list of MarketReports
    when (_.in[List[MarketReport]] != Nil) to("direct:market-reports")
  }  

  from("direct:market-reports") ==> {
    // send data to Cube collector
    process { exchange =>
      val reports = exchange.in[List[MarketReport]]
      val cubified = reports.map { report => 
        // create a map for the "data" element of the Cube event,
        // using the report object minus the "generatedAt" field
        // (to avoid duplication)
        val data = Map("regionID" -> report.regionID,
                       "solarSystemID" -> report.solarSystemID, 
                       "stationID" -> report.stationID,
                       "typeID" -> report.typeID,
                       "buy" -> report.buy,
                       "sell" -> report.sell)
        Map("type" -> "market_report", "time" -> report.generatedAt, "data" -> data)
      }
      implicit val formats = Serialization.formats(NoTypeHints)
      exchange.in = writePretty(cubified)
    }
    to(marketReportURI)
  }
}
