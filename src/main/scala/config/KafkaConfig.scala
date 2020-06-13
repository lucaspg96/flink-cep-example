package config

import java.util.Properties

object KafkaConfig {

  val broker: String = "kafka:9092"

  val topic: String = "transports"

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", broker)
//  properties.setProperty("group.id", "consumer")

}
