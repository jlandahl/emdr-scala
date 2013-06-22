package org.landahl.emdr

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.util.Date

import model.Order

class TestMVELOrderFilter extends FlatSpec {
  val order = Order(
    generatedAt = new Date,
    regionID = 1,
    solarSystemID = 2,
    stationID = 3,
    typeID = 4,
    orderID = 5,
    bid = false,
    price = 6.0,
    volEntered = 7,
    volRemaining = 8,
    range = 9,
    minVolume = 10,
    issueDate = new Date,
    duration = 11)
    
  behavior of "An MVELOrderFilter"

  it should "provide all Order properties in the local scope" in {
    assert(MVELOrderFilter.makeFilter("regionID == 1")(order))
    assert(MVELOrderFilter.makeFilter("solarSystemID == 2")(order))
    assert(MVELOrderFilter.makeFilter("stationID == 3")(order))
    assert(MVELOrderFilter.makeFilter("typeID == 4")(order))
    assert(MVELOrderFilter.makeFilter("orderID == 5")(order))
    assert(MVELOrderFilter.makeFilter("bid == false")(order))
    assert(MVELOrderFilter.makeFilter("price == 6.0")(order))
    assert(MVELOrderFilter.makeFilter("volEntered == 7")(order))
    assert(MVELOrderFilter.makeFilter("volRemaining == 8")(order))
    assert(MVELOrderFilter.makeFilter("range == 9")(order))
    assert(MVELOrderFilter.makeFilter("minVolume == 10")(order))
    assert(MVELOrderFilter.makeFilter("duration == 11")(order))
  } 
  
  it should "allow for matching against lists" in {
    assert(MVELOrderFilter.makeFilter("[1, 2, 3] contains regionID")(order))
    assert(MVELOrderFilter.makeFilter("[1, 2, 3] contains solarSystemID")(order))
    assert(MVELOrderFilter.makeFilter("[1, 2, 3] contains stationID")(order))
  }
}
