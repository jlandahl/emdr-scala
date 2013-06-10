package org.landahl.emdr.model

case class Order(
  generatedAt: String,
  regionID: Option[Long], 
  solarSystemID: Option[Long],
  stationID: Long,
  typeID: Long, 
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
        regionID = rowset.regionID,
        solarSystemID = row.solarSystemID,
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