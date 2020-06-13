import config.KafkaConfig
import model.Transport
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import serialization.KafkaSimpleStringSerializationSchema
import source.TransportsGeneratorSource

object DataGenerator extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val kafkaProducer = new FlinkKafkaProducer[String](
    KafkaConfig.topic,
    new KafkaSimpleStringSerializationSchema(KafkaConfig.topic),
    KafkaConfig.properties,
    FlinkKafkaProducer.Semantic.EXACTLY_ONCE)

//  val kafkaProducer = new FlinkKafkaProducer[String](KafkaConfig.broker, KafkaConfig.topic, new SimpleStringSchema)

  env.setParallelism(1)

  val dataStream = env.addSource(TransportsGeneratorSource)

  dataStream.print()

  dataStream
    .map(Transport.format.writes(_))
    .map(_.toString())
    .addSink(kafkaProducer)

  env.execute("data generator")

}
