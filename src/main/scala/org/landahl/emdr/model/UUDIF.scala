package org.landahl.emdr.model

import java.util.Date
import org.json4s.JsonAST.JValue

case class UUDIF(
  resultType: String,
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: Date,
  rowsets: List[Rowset]
)

case class UploadKey(
  name: String,
  key: Option[String],
  version: Option[String]  // "version" added as a workaround for EMDU bug
)

case class Generator(name: String, version: String)
case class Rowset(generatedAt: Date, regionID: Option[Int], typeID: Int, rows: List[Row])

trait Row

case class OrderRow(
  solarSystemID: Option[Int],    // possibly null
  stationID: Int,
  orderID: Long,
  bid: Boolean,
  price: Double,
  volEntered: Long,
  volRemaining: Long,
  range: Int,
  minVolume: Int,
  issueDate: Date,
  duration: Int
) extends Row

case class HistoryRow(
  date: Date,
  orders: Int,
  quantity: Int,
  low: Double,
  high: Double,
  average: Double
) extends Row

// JsonUUDIF and JsonRowset are used only for initial JSON extraction

case class JsonUUDIF(
  resultType: String,
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: Date,
  columns: List[String],   // not used in UUDIF
  rowsets: List[JsonRowset]
)

case class JsonRowset(
  generatedAt: Date, 
  regionID: Option[Int], 
  typeID: Int, 
  rows: List[JValue]   // becomes List[Row] in Rowset
)
