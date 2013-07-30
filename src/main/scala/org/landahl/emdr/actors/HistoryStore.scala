package org.landahl.emdr.actors

import akka.actor.{ Actor, Props }
import reactivemongo.api.{ MongoDriver, DB, Collection }
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{ BSONDocument, BSONDateTime }
import reactivemongo.core.commands.GetLastError
import org.landahl.emdr.Settings
import org.landahl.emdr.model.{ UUDIF, HistoryRow }

class HistoryStore extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  val settings = Settings(context.system)

  lazy val driver = new MongoDriver
  lazy val connection = driver.connection(List(settings.mongoServer))
  lazy val db = connection(settings.mongoDatabase)
  lazy val collection = db("history")

  def receive = {
    case uudif: UUDIF => save(uudif)
    case _ =>
  }

  def save(uudif: UUDIF) = {
    for {
      rowset <- uudif.rowsets
      HistoryRow(date, orders, quantity, low, high, average) <- rowset.rows
    }
    {
      println(s"Region: ${rowset.regionID}, TypeID: ${rowset.typeID}, $date $orders $quantity $low $high $average")
      val selector = BSONDocument(
        "regionID" -> rowset.regionID,
        "typeID" -> rowset.typeID,
        "date" -> BSONDateTime(date.getTime))
      val update = selector ++ BSONDocument(
        "orders" -> orders,
        "quantity" -> quantity,
        "low" -> low,
        "high" -> high,
        "average" -> average)
      collection.update(selector, update, upsert = true)
    }
  }
}

object HistoryStore {
  def props = Props[HistoryStore]
}
