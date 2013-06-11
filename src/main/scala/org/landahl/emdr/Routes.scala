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
      when (_.in[UUDIF].resultType == "orders") {
        // produce a list of MarketReports for orders matching stationList
        process { exchange =>
          val uudif = exchange.in[UUDIF]
          exchange.in = MarketReport.fromUUDIF(uudif, orderFilter).toList
        }
        choice {
          // proceed only if there's data to report
          when (_.in[List[MarketReport]] != Nil) {
            // serialize to JSON 
            process { exchange =>
              implicit val formats = Serialization.formats(NoTypeHints)
              exchange.in = writePretty(exchange.in)
            }
            to(marketReportURI)
          }
        }
      }
    }
  }
}
