import conditions.{EndCondition, MiddleCondition, StartCondition}
import config.KafkaConfig
import model.Transport
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.api.common.time.Time
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import play.api.libs.json.Json
import util.StreamUtils

object CEPStream extends App {

  val pattern = Pattern.begin[Transport]("start")
    .where(StartCondition)
    .followedBy("middle").where(MiddleCondition).oneOrMore
    .followedBy("end").where(EndCondition)
    //.within(Time.seconds(10))

  val sampleRecords = List(
    Transport(0, "A", "M0"),
    Transport(0, "M0", "M1"),
    Transport(0, "M1", "M2"),
    Transport(0, "M2", "M3"),
    Transport(0, "M3", "B"),
  )

  val env = StreamExecutionEnvironment.getExecutionEnvironment
//  env.setParallelism(1)

//  val dataStream = env.fromCollection(sampleRecords)
  val dataStream = env.addSource(
    new FlinkKafkaConsumer(KafkaConfig.topic, new SimpleStringSchema() , KafkaConfig.properties)
  )

  val transportStream = dataStream
    .map(Json.parse(_).as[Transport])

  val keyedStream = transportStream.keyBy(_.productId)

  val cepStream = CEP.pattern(keyedStream, pattern)
    .flatSelect(StreamUtils.getDescribeTravelFunction)

    cepStream.print()

  env.execute("CEP example")

}
