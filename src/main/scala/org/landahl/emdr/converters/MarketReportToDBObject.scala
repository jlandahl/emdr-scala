package org.landahl.emdr.converters

import org.apache.camel.Converter
import com.mongodb.DBObject
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.commons.MongoDBObject
import org.joda.time.DateTime

import org.landahl.emdr.model.MarketReport

@Converter
object MarketReportToDBObject {
  @Converter
  def convert(marketReport: MarketReport): DBObject = {
    RegisterJodaTimeConversionHelpers()

    val builder = MongoDBObject.newBuilder
    builder += (
      "generatedAt" -> DateTime.parse(marketReport.generatedAt),
      "regionID" -> marketReport.regionID,
      "solarSystemID" -> marketReport.solarSystemID,
      "stationID" -> marketReport.stationID,
      "typeID" -> marketReport.typeID)

    marketReport.buy.map { buy =>
      builder += "buy_price" -> buy.max
      builder += "buy_volume" -> buy.volume
    }
    
    marketReport.sell.map { sell =>
      builder += "sell_price" -> sell.min
      builder += "sell_volume" -> sell.volume
    }

    builder.result
  }
}
