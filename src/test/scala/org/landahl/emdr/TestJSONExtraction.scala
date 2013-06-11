package org.landahl.emdr

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers._

import model.{UUDIF, UploadKey, Generator, OrderRow, HistoryRow, SampleData} 

class TestJSONExtraction extends FunSuite {
  test("extract order1") {
    val uudif = UUDIF.extract(SampleData.orders1)
    uudif.resultType should equal("orders")
    uudif.version should equal("0.1")
    uudif.uploadKeys.size should equal(2)
    uudif.uploadKeys(0) should equal(UploadKey("emk", Some("abc"), None))
    uudif.uploadKeys(1) should equal(UploadKey("ec", Some("def"), None))
    uudif.generator should equal (Generator("Yapeal", "11.335.1737"))
    uudif.currentTime should equal("2011-10-22T15:46:00+00:00")
    uudif.rowsets.size should equal(3)

    val rs0 = uudif.rowsets(0)
    rs0.generatedAt should equal("2011-10-22T15:43:00+00:00")
    rs0.regionID should equal(Some(10000065))
    rs0.typeID should equal(11134)
    rs0.rows.size should equal(3)

    rs0.rows(0) match {
      case row: OrderRow => {
        row.price should equal(8999)
        row.volRemaining should equal(1)
        row.range should equal(32767)
        row.orderID should equal(2363806077L)
        row.volEntered should equal(1)
        row.minVolume should equal(1)
        row.bid should equal(false)
        row.issueDate should equal("2011-12-03T08:10:59+00:00")
        row.duration should equal(90)
        row.stationID should equal(60008692)
        row.solarSystemID should equal(Some(30005038))
      }
    }

    rs0.rows(1) match {
      case row: OrderRow => {
        row.price should equal(11499.99)
        row.volRemaining should equal(10)
        row.range should equal(32767)
        row.orderID should equal(2363915657L)
        row.volEntered should equal(10)
        row.minVolume should equal(1)
        row.bid should equal(false)
        row.issueDate should equal("2011-12-03T10:53:26+00:00")
        row.duration should equal(90)
        row.stationID should equal(60006970)
        row.solarSystemID should equal(None)
      }
    }

    val rs2 = uudif.rowsets(2)
    rs2.generatedAt should equal("2011-10-22T15:43:00+00:00")
    rs2.regionID should equal(Some(10000065))
    rs2.typeID should equal(11136)
    rs2.rows should equal(Nil)
  }

  test ("extract history1") {
    val uudif = UUDIF.extract(SampleData.history1)
    uudif.resultType should equal("history")
    uudif.version should equal("0.1")
    uudif.uploadKeys.size should equal(2)
    uudif.uploadKeys(0) should equal(UploadKey("emk", Some("abc"), None))
    uudif.uploadKeys(1) should equal(UploadKey("ec", Some("def"), None))
    uudif.generator should equal (Generator("Yapeal", "11.335.1737"))
    uudif.currentTime should equal("2011-10-22T15:46:00+00:00")
    uudif.rowsets.size should equal(1)
    
    val rs0 = uudif.rowsets(0)
    rs0.generatedAt should equal("2011-10-22T15:42:00+00:00")
    rs0.regionID should equal(Some(10000065))
    rs0.typeID should equal(11134)
    rs0.rows.size should equal(2)

    rs0.rows(0) match {
      case row: HistoryRow => {
        row.date should equal("2011-12-03T00:00:00+00:00")
        row.orders should equal(40)
        row.quantity should equal(40)
        row.low should equal(1999)
        row.high should equal(499999.99)
        row.average should equal(35223.50)
      }
    }
  }
  
}
