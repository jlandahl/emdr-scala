package org.landahl.emdr.converters

import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write, writePretty}
import org.json4s.NoTypeHints

import org.landahl.emdr.model.{UUDIF, HistoryRow}

object HistoryToCube {
  def convert(uudif: UUDIF): String = {
    val cubified = for {
      rowset <- uudif.rowsets
      HistoryRow(date, orders, quantity, low, high, average) <- rowset.rows
    } yield Map("type" -> "price_history",
                "time" -> date,
                "data" -> Map("orders" -> orders,
                              "quantity" -> quantity,
                              "low" -> low,
                              "high" -> high,
                              "average" -> average))
    implicit val formats = Serialization.formats(NoTypeHints)
    writePretty(cubified)
  }
}
