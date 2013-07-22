package org.landahl.emdr.util

object Zip {
  def inflate(data: Array[Byte]): String = {
    val baos = new java.io.ByteArrayOutputStream
    val ios = new java.util.zip.InflaterOutputStream(baos)
    ios.write(data, 0, data.length)
    ios.close
    baos.toString
  }
}