package org.landahl.emdr.model

import org.json4s.JsonAST.JValue

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
