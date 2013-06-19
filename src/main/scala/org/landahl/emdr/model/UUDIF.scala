package org.landahl.emdr.model

import scala.collection.mutable.ListBuffer
import org.json4s.native.JsonMethods.parse
import org.json4s.JsonAST.JValue
import org.json4s.{JObject, DefaultFormats}
import org.apache.camel.Converter

case class UUDIF(
  resultType: String,
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: String,
  rowsets: List[Rowset]
)

@Converter
object UUDIF {
  @Converter
  def extract(json: String): UUDIF = {
    implicit val formats = DefaultFormats
    val _uudif = parse(json).extract[JsonUUDIF]
    UUDIF(
      resultType = _uudif.resultType,
      version = _uudif.version,
      uploadKeys = _uudif.uploadKeys,
      generator = _uudif.generator,
      currentTime = _uudif.currentTime,
      rowsets = _uudif.rowsets.map { rowset =>
        Rowset(
          generatedAt = rowset.generatedAt,
          regionID = rowset.regionID,
          typeID = rowset.typeID,
          rows = _uudif.resultType match {
            case "orders" => rowset.rows.map(r => extractRow[OrderRow](_uudif.columns, r))
            case "history" => rowset.rows.map(r => extractRow[HistoryRow](_uudif.columns, r))
          }
        )
      }
    )
  }

  def extractRow[A: Manifest](columns: List[String], row: JValue) = {
    implicit val formats = DefaultFormats
    val fields = columns.zip(row.children)
    val obj = JObject(fields)
    obj.extract[A]
  }
}

case class UploadKey(
  name: String,
  key: Option[String],
  version: Option[String]  // "version" added as a workaround for EMDU bug
)

case class Generator(name: String, version: String)
case class Rowset(generatedAt: String, regionID: Option[Int], typeID: Int, rows: List[Row])
