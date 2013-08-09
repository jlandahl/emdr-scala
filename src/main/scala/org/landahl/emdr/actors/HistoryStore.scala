package org.landahl.emdr.actors

import akka.actor.{ Actor, Props, ActorLogging }
import reactivemongo.api.{ MongoDriver, DB, Collection }
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{ BSONDocument, BSONDateTime }
import reactivemongo.core.commands.GetLastError
import org.landahl.emdr.Settings
import org.landahl.emdr.model.{ UUDIF, HistoryRow }

class HistoryStore extends Actor with ActorLogging {
  import scala.concurrent.ExecutionContext.Implicits.global

  val settings = Settings(context.system)

  lazy val driver = new MongoDriver
  lazy val connection = driver.connection(List(settings.mongoServer))
  lazy val db = connection(settings.mongoDatabase)
  lazy val collection: BSONCollection = db("history")

  def receive = {
    case uudif: UUDIF => save(uudif)
    case x => log.warning("Received unknown message: {}", x)
  }

  def save(uudif: UUDIF) = {
    if (log.isDebugEnabled) {
      val numRows = uudif.rowsets.foldLeft(0) { (numRows, rowset) => numRows + rowset.rows.length }
      log.debug("Saving {} rows", numRows)
    }
    for {
      rowset <- uudif.rowsets
      HistoryRow(date, orders, quantity, low, high, average) <- rowset.rows
    }
    {
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
      collection.uncheckedUpdate(selector, update, upsert = true)
    }
  }
}

object HistoryStore {
  def props = Props[HistoryStore]
}
