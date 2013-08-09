package org.landahl.emdr.actors

import akka.actor.{Actor, Props, ActorLogging}
import org.landahl.emdr.model.{UUDIF, Rowset, OrderRow, Order}
import org.landahl.emdr.{Settings, EsperStatements}
import com.espertech.esper.client.{EventBean, UpdateListener, EPServiceProviderManager}

class EsperOrderProcessor extends Actor with ActorLogging {
  val settings = Settings(context.system)

  val epService = EPServiceProviderManager.getDefaultProvider
  val admin = epService.getEPAdministrator
  val runtime = epService.getEPRuntime

  val listener = new UpdateListener {
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      newEvents.map(self ! _.getUnderlying)
    }
  }

  EsperStatements.createStatements(settings.rowsetFilter, settings.orderFilter)

  admin createEPL("select * from Rowsets") addListener(listener)
  admin createEPL("select * from SellEvents") addListener new UpdateListener {
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      newEvents.foreach(eb => println(eb.getUnderlying))
    }
  }

  val topitems = admin createEPL """
    select typeID, count(*) as cnt
      from Rowsets.win:time(30 sec)
  group by typeID
    output last every 30 seconds
  order by cnt desc
     limit 10
  """

  topitems addListener new UpdateListener {
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      if (newEvents.length > 0) {
        println("Top 10 items")
        newEvents.foreach(eb => println(eb.getUnderlying))
      }
    }
  }

  def receive = {
    case uudif: UUDIF if uudif.resultType == "orders" => {
      log.debug("Received UUDIF with {} rowsets", uudif.rowsets.length)
      uudif.rowsets.foreach(runtime.sendEvent(_))
    }

    case rowset: Rowset => {
      log.debug("Received Rowset with {} rows", rowset.rows.length)
      for(OrderRow(solarSystemID, stationID, orderID, bid, price, volEntered, volRemaining, range, minVolume, issueDate, duration) <- rowset.rows)
        runtime.sendEvent(
          Order(
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
        )
    }
  }
}

object EsperOrderProcessor {
  def props = Props(new EsperOrderProcessor)
}