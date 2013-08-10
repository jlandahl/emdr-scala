package org.landahl.emdr.actors

import akka.actor.{Actor, Props, ActorLogging}
import com.espertech.esper.client.{EventBean, UpdateListener, EPServiceProviderManager}
import com.espertech.esper.event.map.MapEventBean

import org.landahl.emdr.{Settings, EsperStatements}
import org.landahl.emdr.model.{UUDIF, Rowset, OrderRow, Order}
import org.landahl.emdr.converters.RowsetToOrders

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
      val events = newEvents.collect {
        case mapEvent: MapEventBean if mapEvent.get("sell_price") != null => mapEvent
      }
      if (events.length > 0) {
        println
        events.foreach(eb => println(eb.getProperties))
      }
    }
  }

  admin createEPL("select count(*) as cnt from Orders.win:time(10 sec) output last every 15 seconds") addListener new UpdateListener{
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      newEvents.foreach{ eb =>
        eb.getUnderlying match {
          case mapEvent: java.util.Map[String, Any] =>
            println(s"Processed ${mapEvent.get("cnt")} orders")
        }
      }
    }
  }

  val topitems = admin createEPL """
    select typeID, count(*) as cnt
      from Rowsets.win:time(60 sec)
  group by typeID
    output last every 60 seconds
  order by cnt desc
     limit 20
  """

  topitems addListener new UpdateListener {
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      if (newEvents.length > 0) {
        println
        println("Top Items")
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
      RowsetToOrders.extractOrders(rowset).foreach(runtime.sendEvent(_))
    }
  }
}

object EsperOrderProcessor {
  def props = Props(new EsperOrderProcessor)
}
