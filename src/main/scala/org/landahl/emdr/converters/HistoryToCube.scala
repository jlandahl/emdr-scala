package org.landahl.emdr.converters

import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write, writePretty}
import org.json4s.NoTypeHints

import org.landahl.emdr.model.{UUDIF, HistoryRow}

object HistoryToCube {
  def convert(uudif: UUDIF): String = {
    val cubified = for {
      rowset <- uudif.rowsets
      row <- rowset.rows
    } yield row match {
      case row: HistoryRow =>
        Map("type" -> "price_history",
            "time" -> row.date,
            "data" -> Map("orders" -> row.orders,
                          "quantity" -> row.quantity,
                          "low" -> row.low,
                          "high" -> row.high,
                          "average" -> row.average))
    }
    implicit val formats = Serialization.formats(NoTypeHints)
    writePretty(cubified)
  }
}
