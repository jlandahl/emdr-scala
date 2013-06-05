package org.landahl.emdr.model

trait Row

case class OrderRow(
  solarSystemID: Option[Long],    // can be null (but rarely ever is)
  stationID: Long,
  orderID: Long,
  bid: Boolean,
  price: Double,
  volEntered: Long,
  volRemaining: Long,
  range: Int,
  minVolume: Int,
  issueDate: String,
  duration: Int
) extends Row

case class HistoryRow(
  date: String,
  orders: Int,
  quantity: Long,
  low: Double,
  high: Double,
  average: Double
) extends Row
