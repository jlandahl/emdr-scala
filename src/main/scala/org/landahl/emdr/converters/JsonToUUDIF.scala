package org.landahl.emdr.converters

import org.json4s.native.JsonMethods.parse
import org.json4s.JsonAST.JValue
import org.json4s.{JObject, DefaultFormats}

import org.landahl.emdr.model.{UUDIF, JsonUUDIF, Rowset, OrderRow, HistoryRow}

object JsonToUUDIF {
  case object Formats extends DefaultFormats {
    override def dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
  }

  def extract(json: String): UUDIF = {
    implicit val formats = Formats
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
          })
      })
  }

  def extractRow[A: Manifest](columns: List[String], row: JValue) = {
    implicit val formats = Formats
    val fields = columns.zip(row.children)
    val obj = JObject(fields)
    obj.extract[A]
  }
}
