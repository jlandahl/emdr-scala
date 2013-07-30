package org.landahl.emdr

import akka.actor.{ ActorSystem, Extension, ExtensionId, ExtensionIdProvider, ExtendedActorSystem }
import scala.concurrent.duration.Duration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit

class Settings(config: Config) extends Extension {
  val zookeeperURI = config.getString("emdr.zookeeperURI")
  val kafkaGroupId = config.getString("emdr.kafka.groupID")
  val kafkaTopic = config.getString("emdr.kafka.topic")
  val kafkaBrokers = config.getString("emdr.kafka.brokers")
  val mongoServer = config.getString("emdr.mongo.server")
  val mongoDatabase = config.getString("emdr.mongo.database")
}

object Settings extends ExtensionId[Settings] with ExtensionIdProvider {
  override def lookup = Settings

  override def createExtension(system: ExtendedActorSystem) = new Settings(system.settings.config)

  /**
   * Java API: retrieve the Settings extension for the given system.
   */
  override def get(system: ActorSystem): Settings = super.get(system)
}
