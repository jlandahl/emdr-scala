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
    val _uudif = parse(json).extract[_UUDIF]
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
case class Rowset(generatedAt: String, regionID: Option[Int], typeID: Int, rows: List[Row])

trait Row

case class OrderRow(
  solarSystemID: Option[Int],
  stationID: Int,
  orderID: Long,
  bid: Boolean,
  price: Double,
  volEntered: Int,
  volRemaining: Int,
  range: Int,
  minVolume: Int,
  issueDate: String,
  duration: Int
) extends Row

case class HistoryRow(
  date: String,
  orders: Int,
  quantity: Int,
  low: Double,
  high: Double,
  average: Double
) extends Row

// _UUDIF and _Rowset are used only for initial JSON extraction

case class _UUDIF(
  resultType: String,
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: String,
  columns: List[String],
  rowsets: List[_Rowset]
)

case class _Rowset(generatedAt: String, regionID: Option[Int], typeID: Int, rows: List[JValue])
