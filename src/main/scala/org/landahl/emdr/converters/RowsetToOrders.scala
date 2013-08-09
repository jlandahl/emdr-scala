package org.landahl.emdr.converters

import org.landahl.emdr.model.{ Rowset, Order, OrderRow }

object RowsetToOrders {
  def extractOrders(rowset: Rowset): Seq[Order] = {
    for(OrderRow(solarSystemID, stationID, orderID, bid, price, volEntered, volRemaining, range, minVolume, issueDate, duration) <- rowset.rows)
    yield Order(
      generatedAt = rowset.generatedAt,
      generatedAt_ms = rowset.generatedAt.getTime,
      regionID = rowset.regionID.getOrElse(0),
      solarSystemID = solarSystemID.getOrElse(0),
      stationID = stationID,
      typeID = rowset.typeID,
      orderID = orderID,
      bid = bid,
      price = price,
      volEntered = volEntered,
      volRemaining = volRemaining,
      range = range,
      minVolume = minVolume,
      issueDate = issueDate,
      duration = duration)
  }
}