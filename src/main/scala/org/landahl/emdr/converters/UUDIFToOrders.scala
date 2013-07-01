package org.landahl.emdr.converters

import org.landahl.emdr.model.{ UUDIF, Order, OrderRow }

object UUDIFToOrders {
  def extractOrders(uudif: UUDIF): Iterable[Order] = {
    uudif.rowsets.flatMap { rowset =>
      rowset.rows.collect {
        case row: OrderRow =>
          Order(
            generatedAt = rowset.generatedAt,
            generatedAt_ms = rowset.generatedAt.getTime,
            regionID = rowset.regionID.getOrElse(0),
            solarSystemID = row.solarSystemID.getOrElse(0),
            stationID = row.stationID,
            typeID = rowset.typeID,
            orderID = row.orderID,
            bid = row.bid,
            price = row.price,
            volEntered = row.volEntered,
            volRemaining = row.volRemaining,
            range = row.range,
            minVolume = row.minVolume,
            issueDate = row.issueDate,
            duration = row.duration)
      }
    }
  }
}