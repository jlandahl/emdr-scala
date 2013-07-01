package org.landahl.emdr.model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers._
import java.util.Date

class TestOrderSummary extends FunSuite {
  def newOrder(price: Double, bid: Boolean) = {
    val generatedAt = new Date
    Order(
      generatedAt = generatedAt,
      generatedAt_ms = generatedAt.getTime,
      regionID = 1,
      solarSystemID = 1,
      stationID = 1,
      typeID = 1,
      orderID = 1,
      bid = bid,
      price = price,
      volEntered = 10,
      volRemaining = 10,
      range = 1,
      minVolume = 1,
      issueDate = new Date,
      duration = 1)
  }

  test("empty list") {
    OrderSummary.summarize(List()) should not be ('defined)
  }

  val sellOrders = List(
    newOrder(10.50, false),
    newOrder(10.75, false),
    newOrder(11.00, false),
    newOrder(11.25, false),
    newOrder(75.00, false)
  )

  test("sellOrders") {
    OrderSummary.summarize(sellOrders) match {
      case Some(summary) => {
        summary.min should equal(10.5)
        summary.max should equal(75.0)
        summary.mean should equal(23.7)
        summary.stddev.floor should equal(28.0)
        summary.volume should equal(50)
      }
      case _ => {}
    }
  }

  val buyOrders = List(
    newOrder(9.00, true),
    newOrder(9.01, true),
    newOrder(9.50, true),
    newOrder(9.51, true),
    newOrder(9.60, true)
  )

  test("buyOrders") {
    OrderSummary.summarize(buyOrders) match {
      case Some(summary) => {
        summary.min should equal(9.0)
        summary.max should equal(9.6)
        summary.mean should equal(9.324)
        summary.stddev.floor should equal(0.0)
        summary.volume should equal(50)
      }
      case _ => {}
    }
  }
}
