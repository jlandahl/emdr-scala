package org.landahl.emdr

import scala.collection.JavaConversions._
import com.typesafe.config.Config
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.model.dataformat.ZipDataFormat
import com.espertech.esper.event.map.MapEventBean
import org.landahl.emdr.converters.UUDIFToOrders
import org.landahl.emdr.model.UUDIF
import org.landahl.emdr.model.Order

class EsperRouting(config: Config) extends RouteBuilder {
  val emdrInputURI = config.getString("emdr.input")
  val errorURI = config.getString("error.output")

  errorHandler(deadLetterChannel(errorURI))

  from(emdrInputURI) to ("direct:decompress-json")
  from("file:input/emdr?delay=5000") to ("direct:decompress-json")
  from("file:input/json?delay=5000") to ("seda:process-json")

  from("direct:decompress-json") ==> {
    // unzip (inflate) the EMDR data
    unmarshal(new ZipDataFormat)
    to("seda:process-json")
  }

  from("seda:process-json?concurrentConsumers=1") ==> {
    // ensure the body starts as a string
    as(classOf[String])
    // use the registered type converter (JsonToUUDIF) to convert the JSON to a UUDIF object
    as(classOf[UUDIF])
    choice {
      //when(_.in[UUDIF].resultType == "history") to ("direct:history")
      when(_.in[UUDIF].resultType == "orders") to ("direct:orders")
    }
  }

  from("direct:history") to ("mock:history")

  from("direct:orders") ==> {
    process(e => e.in = UUDIFToOrders.extractOrders(e.in[UUDIF]))
    as(classOf[java.util.List[Order]])
    split(body) { to("esper:orders") }
  }

  from("esper:orders?eql=select stationID, typeID, min(price) as sell_price from SellOrders.win:time(60 sec) group by stationID, typeID output last every 60 seconds") ==> {
    process { exchange =>
      val event = exchange.in[MapEventBean].getProperties
      if (event("sell_price") != null)
        println(event)
    }
  }
}
