package org.landahl.emdr.model

import scala.collection.mutable.ListBuffer
import org.json4s.native.JsonMethods.parse
import org.json4s.JsonAST.JValue
import org.json4s.{JObject, DefaultFormats}

case class UUDIF(
  resultType: String,
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: String,
  rowsets: List[Rowset]
)

object UUDIF {
  def extract(json: String): UUDIF = {
    implicit val formats = DefaultFormats
    val _uudif = parse(json).extract[JsonUUDIF]
    val rowsets = _uudif.rowsets.map { rowset =>
      val rows = _uudif.resultType match {
        case "orders" => rowset.rows.map(r => extractRow[OrderRow](_uudif.columns, r))
        case "history" => rowset.rows.map(r => extractRow[HistoryRow](_uudif.columns, r))
      }
      Rowset(
        generatedAt = rowset.generatedAt,
        regionID = rowset.regionID,
        typeID = rowset.typeID,
        rows = rows
      )
    }
    UUDIF(
      resultType = _uudif.resultType,
      version = _uudif.version,
      uploadKeys = _uudif.uploadKeys,
      generator = _uudif.generator,
      currentTime = _uudif.currentTime,
      rowsets = rowsets
    )
  }

  def extractRow[A: Manifest](columns: List[String], row: JValue) = {
    implicit val formats = DefaultFormats
    val fields = columns.zip(row.children)
    val obj = JObject(fields)
    obj.extract[A]
  }
}

case class UploadKey(name: String, key: String)
case class Generator(name: String, version: String)
case class Rowset(generatedAt: String, regionID: Option[Long], typeID: Long, rows: List[Row])

trait Row

case class OrderRow(
  solarSystemID: Option[Long],
  stationID: Long,
  orderID: Long,
  bid: Boolean,
  price: Double,
  volEntered: Long,
  volRemaining: Long,
  range: Int,
  minVolume: Int,
  issueDate: String,
  duration: Int
) extends Row

case class HistoryRow(
  date: String,
  orders: Int,
  quantity: Long,
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
  currentTime: String,
  columns: List[String],   // not used in UUDIF
  rowsets: List[JsonRowset]
)

case class JsonRowset(
  generatedAt: String, 
  regionID: Option[Long], 
  typeID: Int, 
  rows: List[JValue]   // becomes List[Row] in Rowset
)
