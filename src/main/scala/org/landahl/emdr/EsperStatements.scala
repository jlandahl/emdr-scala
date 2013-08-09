package org.landahl.emdr

import scala.collection.JavaConversions._
import com.espertech.esper.client.EPServiceProviderManager
import com.typesafe.config.Config

object EsperStatements {
  def createStatements(rowsetFilter: Option[String], orderFilter: Option[String]) = {
    val epService = EPServiceProviderManager.getDefaultProvider
    val admin = epService.getEPAdministrator

    admin createEPL "insert into Rowsets select * from org.landahl.emdr.model.Rowset" +
      (if (rowsetFilter.isDefined) s" where ${rowsetFilter.get}" else "")

    admin createEPL "insert into Orders select * from `org.landahl.emdr.model.Order`" +
      (if (orderFilter.isDefined) s" where ${orderFilter.get}" else "") +
      "group by orderID output last every 30 seconds" // remove duplicates

    admin createEPL "insert into BuyOrders select * from Orders where bid = true"
    admin createEPL """
      insert into BuyEvents
      select regionID, solarSystemID, stationID, typeID, max(price) as buy_price, sum(volRemaining) as volume
        from BuyOrders.win:time(60 sec)
    """

    admin createEPL "insert into SellOrders select * from Orders where bid = false"
    admin createEPL """
      insert into SellEvents
      select regionID, solarSystemID, stationID, typeID, min(price) as sell_price, sum(volRemaining) as volume
        from SellOrders.win:time(30 sec)
    group by regionID, solarSystemID, stationID, typeID
      output last every 30 seconds
    //order by volume desc
    //   limit 10
    """

  }
}
