package org.landahl.emdr

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers._

import model.{OrderRow, OrderSummary}

class TestOrderSummary extends FunSuite {
  test("empty list") {
    OrderSummary.summarize(List()) should not be ('defined)
  }
  
  val sellOrders = List(
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=false, price=10.50, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=false, price=10.75, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=false, price=11.00, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=false, price=11.25, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=false, price=75.00, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1)
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
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=true, price=9.00, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=true, price=9.01, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=true, price=9.50, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=true, price=9.51, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1),
    OrderRow(solarSystemID=Some(1), stationID=1, orderID=0, bid=true, price=9.60, volEntered=10, volRemaining=10, range=1, minVolume=1, issueDate="", duration=1)
  )

  test("buyOrders") {
    OrderSummary.summarize(sellOrders) match {
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
