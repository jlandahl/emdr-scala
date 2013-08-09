package org.landahl.emdr

import scala.util.control.Exception.catching
import akka.actor.{ ActorSystem, Extension, ExtensionId, ExtensionIdProvider, ExtendedActorSystem }
import scala.concurrent.duration.Duration
import com.typesafe.config.{ Config, ConfigException }
import java.util.concurrent.TimeUnit

class Settings(config: Config) extends Extension {
  val emdrURI = config.getString("emdr.uri")

  val zookeeperURI = config.getString("emdr.zookeeperURI")
  val kafkaGroupId = config.getString("emdr.kafka.groupID")
  val kafkaTopic = config.getString("emdr.kafka.topic")
  val kafkaBrokers = config.getString("emdr.kafka.brokers")
  val kafkaOffset = config.getString("emdr.kafka.offset")

  val mongoServer = config.getString("emdr.mongo.server")
  val mongoDatabase = config.getString("emdr.mongo.database")

  val rowsetFilter = catching(classOf[ConfigException.Missing]) opt config.getString("emdr.rowset-filter")
  val orderFilter =  catching(classOf[ConfigException.Missing]) opt config.getString("emdr.order-filter")
}

object Settings extends ExtensionId[Settings] with ExtensionIdProvider {
  override def lookup = Settings

  override def createExtension(system: ExtendedActorSystem) = new Settings(system.settings.config)

  /**
   * Java API: retrieve the Settings extension for the given system.
   */
  override def get(system: ActorSystem): Settings = super.get(system)
}
