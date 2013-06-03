package org.landahl.emdr

import scala.io.Source
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers._

import model.UUDIF
import model.UploadKey
import model.Generator
import model.OrderRow

class TestJSONExtraction extends FunSuite {
  test("extract orders") {
    val uudif = UUDIF.extract(order1)
    uudif.resultType should equal("orders")
    uudif.version should equal("0.1")
    uudif.uploadKeys.size should equal(2)
    uudif.uploadKeys(0) should equal(UploadKey("emk", "abc"))
    uudif.uploadKeys(1) should equal(UploadKey("ec", "def"))
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
  
  val order1 = """
{
  "resultType" : "orders",
  "version" : "0.1",
  "uploadKeys" : [
    { "name" : "emk", "key" : "abc" },
    { "name" : "ec" , "key" : "def" }
  ],
  "generator" : { "name" : "Yapeal", "version" : "11.335.1737" },
  "currentTime" : "2011-10-22T15:46:00+00:00",
  "columns" : ["price","volRemaining","range","orderID","volEntered","minVolume","bid","issueDate","duration","stationID","solarSystemID"],
  "rowsets" : [
    {
      "generatedAt" : "2011-10-22T15:43:00+00:00",
      "regionID" : 10000065,
      "typeID" : 11134,
      "rows" : [
        [8999,1,32767,2363806077,1,1,false,"2011-12-03T08:10:59+00:00",90,60008692,30005038],
        [11499.99,10,32767,2363915657,10,1,false,"2011-12-03T10:53:26+00:00",90,60006970,null],
        [11500,48,32767,2363413004,50,1,false,"2011-12-02T22:44:01+00:00",90,60006967,30005039]
      ]
    },
    {
      "generatedAt" : "2011-10-22T15:42:00+00:00",
      "regionID" : null,
      "typeID" : 11135,
      "rows" : [
        [8999,1,32767,2363806077,1,1,false,"2011-12-03T08:10:59+00:00",90,60008692,30005038],
        [11499.99,10,32767,2363915657,10,1,false,"2011-12-03T10:53:26+00:00",90,60006970,null],
        [11500,48,32767,2363413004,50,1,false,"2011-12-02T22:44:01+00:00",90,60006967,30005039]
      ]
    },
    {
      "generatedAt" : "2011-10-22T15:43:00+00:00",
      "regionID" : 10000065,
      "typeID" : 11136,
      "rows" : []
    }
  ]
}
  """
}
