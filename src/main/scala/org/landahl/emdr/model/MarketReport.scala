package org.landahl.emdr.model

case class MarketReport(
  generatedAt: java.util.Date,
  regionID: Int,
  solarSystemID: Int,
  stationID: Int,
  typeID: Int,
  buy: Option[OrderSummary],
  sell: Option[OrderSummary])

object MarketReport {
  def fromOrders(orders: Iterable[Order], orderFilter: (Order) => Boolean = (order) => true): Iterable[MarketReport] = {
    for {
      group <- orders.filter(orderFilter).groupBy(o => (o.generatedAt, o.regionID, o.solarSystemID, o.stationID, o.typeID))
      ((generatedAt, regionID, solarSystemID, stationID, typeID), groupedOrders) = group
      (buyOrders, sellOrders) = groupedOrders.partition(_.bid)
    } yield MarketReport(
      generatedAt, regionID, solarSystemID, stationID, typeID,
      buy = OrderSummary.summarize(buyOrders),
      sell = OrderSummary.summarize(sellOrders))
  }
}
