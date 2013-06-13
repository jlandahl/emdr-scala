package org.landahl.emdr.model

import scala.beans.BeanProperty

case class Order(
  generatedAt: String,
  regionID: Int, 
  solarSystemID: Int,
  stationID: Int,
  typeID: Int,
  orderID: Long,
  bid: Boolean,
  price: Double,
  volEntered: Long,
  volRemaining: Long,
  range: Int,
  minVolume: Int,
  issueDate: String,
  duration: Int)

object Order {
  def fromRowset(rowset: Rowset): Iterable[Order] = {
    rowset.rows.collect { case row: OrderRow =>
      Order(
        generatedAt = rowset.generatedAt,
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
