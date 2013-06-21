package org.landahl.emdr.model

import java.util.Date
import org.json4s.JsonAST.JValue

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
