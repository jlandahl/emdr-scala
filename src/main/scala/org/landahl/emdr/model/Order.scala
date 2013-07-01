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
