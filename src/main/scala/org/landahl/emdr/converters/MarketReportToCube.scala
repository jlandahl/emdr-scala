package org.landahl.emdr.converters

import org.apache.camel.Converter
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write, writePretty}
import org.json4s.NoTypeHints

import org.landahl.emdr.model.MarketReport

@Converter
object MarketReportToCube {
  @Converter
  def convert(marketReports: List[MarketReport]): String = {
    val cubified = marketReports.map { report => 
      // create a map for the "data" element of the Cube event, using 
      // the report object minus the "generatedAt" field (to avoid duplication)
      val data = Map("regionID" -> report.regionID,
                     "solarSystemID" -> report.solarSystemID, 
                     "stationID" -> report.stationID,
                     "typeID" -> report.typeID,
                     "buy" -> report.buy,
                     "sell" -> report.sell)
      Map("type" -> "market_report", "time" -> report.generatedAt, "data" -> data)
    }
    implicit val formats = Serialization.formats(NoTypeHints)
    writePretty(cubified)
  }
}
