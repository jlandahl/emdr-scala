package org.landahl.emdr.model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers._

import org.landahl.emdr.converters.{JsonToUUDIF, UUDIFToOrders}

class TestMarketReport extends FunSuite {
  test("SampleData.orders1") {
    val uudif = JsonToUUDIF.extract(SampleData.orders1)
    val orders = UUDIFToOrders.extractOrders(uudif)
    val reports = MarketReport.fromOrders(orders)
    reports.size should equal(6)
  }

  test("SampleData.orders4") {
    val uudif = JsonToUUDIF.extract(SampleData.orders4)
    val orders = UUDIFToOrders.extractOrders(uudif)
    val reports = MarketReport.fromOrders(orders)
    reports.size should equal(1)

    val mr = reports.head
    mr.regionID should equal (1)
    mr.solarSystemID should equal (1)
    mr.stationID should equal(1)
    mr.typeID should equal(1)

    mr.buy should be ('defined)
    val buy = mr.buy.get
    buy.min should equal(9.0)
    buy.max should equal(9.6)
    buy.mean should equal(9.324)
    buy.stddev.floor should equal(0.0)
    buy.volume should equal(5000)

    mr.sell should be ('defined)
    val sell = mr.sell.get
    sell.min should equal(10.5)
    sell.max should equal(75.0)
    sell.mean should equal(23.7)
    sell.stddev.floor should equal(28.0)
    sell.volume should equal(5000)
  }
}
