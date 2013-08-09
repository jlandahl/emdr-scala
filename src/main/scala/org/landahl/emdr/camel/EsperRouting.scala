package org.landahl.emdr.camel

import scala.collection.JavaConverters._
import com.typesafe.config.Config
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.model.dataformat.ZipDataFormat
import com.espertech.esper.event.map.MapEventBean
import org.landahl.emdr.converters.UUDIFToOrders
import org.landahl.emdr.model.UUDIF
import org.landahl.emdr.model.Order
import org.landahl.emdr.model.Rowset
import org.landahl.emdr.converters.RowsetToOrders

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
      when(_.in[UUDIF].resultType == "history") to ("direct:uudif-history")
      when(_.in[UUDIF].resultType == "orders") to ("direct:uudif-orders")
    }
  }

  // TODO
  from("direct:uudif-history") to ("mock:history")

  from("direct:uudif-orders") ==> {
    as(classOf[java.util.List[Rowset]])
    split(body) { to("esper:rowsets") }
  }

  from("esper:rowsets?eql=select * from Rowsets") ==> {
    as(classOf[java.util.List[Order]])
    split(body) { to("esper:orders") }
  }

  from("esper:orders?eql=select * from SellEvents") ==> {
    process { exchange =>
      val event = exchange.in[MapEventBean].getProperties
      println(event)
    }
  }

 from("esper:orders?eql=select typeID, count(*) from Orders group by typeID output snapshot every 30 seconds order by count(*) desc limit 10") ==> {
   process { exchange =>
     val event = exchange.in[MapEventBean].getProperties
     println(event)
   }
 }
}
