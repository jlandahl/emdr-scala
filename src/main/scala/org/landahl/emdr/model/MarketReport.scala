package org.landahl.emdr.model

case class MarketReport(
  generatedAt: String,
  regionID: Long, 
  solarSystemID: Long, 
  stationID: Long,
  typeID: Long,
  buy: Option[OrderSummary], 
  sell: Option[OrderSummary]
)

object MarketReport {
  def fromUUDIF(uudif: UUDIF): List[MarketReport] = {
    uudif.rowsets.flatMap(summarizeRowset(_))
  }
  
  def summarizeRowset(rowset: Rowset): Iterable[MarketReport] = {
    // get a List[OrderRow] from rowset.rows, which is List[Row]
    val orders = rowset.rows.map { case r: OrderRow => r }
    if (orders == Nil)
      // A Rowset with an empty rows list means someone looked up an item
      // but it was not available in the given region. This is useful information, 
      // so return a MarketReport with buy and sell set to None
      List(MarketReport(
        generatedAt = rowset.generatedAt,
        regionID = rowset.regionID.getOrElse(0),
        solarSystemID = 0,
        stationID = 0,
        typeID = rowset.typeID,
        buy = None,
        sell = None))
    else {
      // group orders by stationID, the most significant location field
      val byStation = orders.groupBy(_.stationID)
      byStation.map { case (stationID, orders) => 
        val (buy, sell) = orders.partition(_.bid)
        MarketReport(
          generatedAt = rowset.generatedAt,
          regionID = rowset.regionID.getOrElse(0),
          solarSystemID = orders(0).solarSystemID.getOrElse(0),
          stationID = stationID,
          typeID = rowset.typeID,
          buy = OrderSummary.summarize(buy),
          sell = OrderSummary.summarize(sell))
      }
    }
  }
}
