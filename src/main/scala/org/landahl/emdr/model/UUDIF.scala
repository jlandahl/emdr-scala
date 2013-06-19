package org.landahl.emdr.model

import scala.collection.mutable.ListBuffer

case class UUDIF(
  resultType: String,
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: String,
  rowsets: List[Rowset]
)

case class UploadKey(
  name: String,
  key: Option[String],
  version: Option[String]  // "version" added as a workaround for EMDU bug
)

case class Generator(name: String, version: String)
case class Rowset(generatedAt: String, regionID: Option[Int], typeID: Int, rows: List[Row])
