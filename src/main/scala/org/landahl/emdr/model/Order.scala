package org.landahl.emdr.model

import scala.beans.BeanProperty

case class Order(
  @BeanProperty generatedAt: java.util.Date,
  @BeanProperty regionID: Int, 
  @BeanProperty solarSystemID: Int,
  @BeanProperty stationID: Int,
  @BeanProperty typeID: Int,
  @BeanProperty orderID: Long,
  @BeanProperty bid: Boolean,
  @BeanProperty price: Double,
  @BeanProperty volEntered: Long,
  @BeanProperty volRemaining: Long,
  @BeanProperty range: Int,
  @BeanProperty minVolume: Int,
  @BeanProperty issueDate: java.util.Date,
  @BeanProperty duration: Int)

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
