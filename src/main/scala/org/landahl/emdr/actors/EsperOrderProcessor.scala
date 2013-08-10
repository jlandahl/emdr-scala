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
  admin createEPL("select * from SellEvents order by volume desc") addListener new UpdateListener {
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      val events = newEvents.collect {
        case map: java.util.Map[String,Any] if map.get("sell_price") != null => map
      }
      if (events.length > 0) {
        println
        events.foreach(println(_))
      }
    }
  }

  admin createEPL("select count(*) as cnt from Orders.win:time(10 sec) output last every 10 seconds") addListener new UpdateListener{
    def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
      newEvents.foreach{ eb =>
        eb.getUnderlying match {
          case mapEvent: java.util.Map[String, Any] =>
            println(s"Processed ${mapEvent.get("cnt")} events")
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
