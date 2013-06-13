package org.landahl.emdr.model

case class MarketReport(
  generatedAt: String,
  regionID: Int, 
  solarSystemID: Int, 
  stationID: Int,
  typeID: Int,
  buy: Option[OrderSummary], 
  sell: Option[OrderSummary]
)

object MarketReport {
  def fromUUDIF(uudif: UUDIF, orderFilter: (Order) => Boolean = (order) => true): Iterable[MarketReport] = {
    uudif.rowsets.flatMap { rowset => 
      val orders = Order.fromRowset(rowset).filter(orderFilter)
      MarketReport.fromOrders(orders)
    }
  }

  def fromOrders(orders: Iterable[Order]): Iterable[MarketReport] = {
    val byStation = orders.groupBy(o => (o.generatedAt, o.regionID, o.solarSystemID, o.stationID, o.typeID))
    byStation.map { case ((generatedAt, regionID, solarSystemID, stationID, typeID), orders) => 
      val (buyOrders, sellOrders) = orders.partition(_.bid)
      MarketReport(
        generatedAt,
        regionID,
        solarSystemID,
        stationID,
        typeID,
        buy = OrderSummary.summarize(buyOrders),
        sell = OrderSummary.summarize(sellOrders))
    }
  }
}
