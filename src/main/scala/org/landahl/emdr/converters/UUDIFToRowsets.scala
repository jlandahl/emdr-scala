package org.landahl.emdr.converters

import java.util.{ List => JList }
import scala.collection.JavaConverters._
import org.apache.camel.Converter

import org.landahl.emdr.model.{ UUDIF, Rowset }

object UUDIFToRowsets {
  @Converter
  def convert(uudif: UUDIF): JList[Rowset] = {
    uudif.rowsets.asJava
  }
}
