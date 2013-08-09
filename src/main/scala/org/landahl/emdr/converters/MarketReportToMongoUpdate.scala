package org.landahl.emdr.converters

import org.apache.camel.Exchange
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._

import org.landahl.emdr.model.MarketReport

object MarketReportToMongoUpdate {
  def camelMongoUpdate(exchange: Exchange) = {
    val marketReport = exchange.getIn(classOf[MarketReport])
    val query = MongoDBObject(
      "regionID" -> marketReport.regionID,
      "solarSystemID" -> marketReport.solarSystemID,
      "stationID" -> marketReport.stationID,
      "typeID" -> marketReport.typeID)

    val builder = MongoDBObject.newBuilder
    builder += ("date" -> marketReport.generatedAt)
    marketReport.buy.map { buy =>
      builder += "buy_price" -> buy.max
      builder += "buy_volume" -> buy.volume
    }
    marketReport.sell.map { sell =>
      builder += "sell_price" -> sell.min
      builder += "sell_volume" -> sell.volume
    }

    val update = MongoDBObject(
      "$set" -> query,
      "$push" -> MongoDBObject(
        "reports" -> MongoDBObject(
          "$each" -> MongoDBList(builder.result),
          "$slice" -> -100,
          "$sort" -> MongoDBObject("date" -> 1))))

    val in = exchange.getIn
    in.setHeader("CamelMongoDbUpsert", true)
    in.setBody(List(query, update))
  }
}
