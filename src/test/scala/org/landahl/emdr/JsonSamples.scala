package org.landahl.emdr

object JsonSamples {
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

  val history1 = """
{
  "resultType" : "history",
  "version" : "0.1",
  "uploadKeys" : [
    { "name" : "emk", "key" : "abc" },
    { "name" : "ec" , "key" : "def" }
  ],
  "generator" : { "name" : "Yapeal", "version" : "11.335.1737" },
  "currentTime" : "2011-10-22T15:46:00+00:00",
  "columns" : ["date","orders","quantity","low","high","average"],
  "rowsets" : [
    {
      "generatedAt" : "2011-10-22T15:42:00+00:00",
      "regionID" : 10000065,
      "typeID" : 11134,
      "rows" : [
        ["2011-12-03T00:00:00+00:00",40,40,1999,499999.99,35223.50],
        ["2011-12-02T00:00:00+00:00",83,252,9999,11550,11550]
      ]
    }
  ]
}
  """
}