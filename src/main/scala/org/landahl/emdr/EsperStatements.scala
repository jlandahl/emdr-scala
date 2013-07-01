package org.landahl.emdr

import scala.collection.JavaConversions._
import com.espertech.esper.client.EPServiceProviderManager
import com.typesafe.config.Config

object EsperStatements {
  def createStatements(config: Config) = {
    val epService = EPServiceProviderManager.getDefaultProvider
    val admin = epService.getEPAdministrator

    val stationList = config.getList("order-filter.stations").map(_.render.toInt).toList
    admin createEPL "insert into FilteredOrders select * from `org.landahl.emdr.model.Order` where stationID in [" + stationList.mkString(",") + "]"
    admin createEPL "insert into BuyOrders select * from FilteredOrders where bid = true"
    admin createEPL "insert into SellOrders select * from FilteredOrders where bid = false"
  }
}
