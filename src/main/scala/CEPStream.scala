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

  /**
   * Inicialmente, precisamos definir o padrão de eventos a ser identificado
   * Um padrão é caracterizado por uma sequência de condições, as quais podem ser:
   * - Simples, onde apenas o valor atual é usado na condição;
   * - Iterativo, o qual utiliza eventos anteriores na condição.
   *
   * Todas as condições recebem um nome, o que permite que condiçoes Iterativas consultem pelos seus eventos
   */
  val pattern = Pattern
    // o evento inicial verifica se o ponto de partida é igual ao ponto inicial pré-definido
    .begin[Transport]("start").where(StartCondition)
    // em seguida, deve ocorrer um evento intermediário. Este evento é um evento Iterativo:
    // ele precisa checar se o ponto de partida do transporte é igual ao último ponto de destino registrado
    .followedBy("middle").where(MiddleCondition)
    // um evento intermediário pode ocorrer uma ou mais vezes, visto que a viagem pode ter diversas paradas
    .oneOrMore
    // por fim, quando o destino do transporte for igual ao ponto final pré-definido, o padrão é encerrado
    // (vale lembrar que é necessário checar se o transporte partiu do último destino registrado
    .followedBy("end").where(EndCondition)
    // caso necessário, podemos definir um tempo de vida para um padrão. Por exemplo: se o padrão não se completar
    // em 10 segundos, podemos descartá-lo
    //.within(Time.seconds(10))

  // A seguir temos um exemplo de uma sequência de transportes que segue a rota:
  // A -> M0 -> M1 -> M2 -> M3 -> B
  val sampleRecords = List(
    Transport(0, "A", "M0"),
    Transport(0, "M0", "M1"),
    Transport(0, "M1", "M2"),
    Transport(0, "M2", "M3"),
    Transport(0, "M3", "B"),
  )

  // Agora, vamos criar nosso pipeline para ser executado no Flink.
  // Precisamos, inicialmente, obter o ambiente de execução
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // Caso deseje executar o código com os dados de exemplo, remova o comentário da linha abaixo e comente
  // a outra criação da variável dataStream

//  val dataStream = env.fromCollection(sampleRecords)
  // Iremos adicionar um consumidor de um tópico do kafka que contém as mensagens com os transportes
  val dataStream = env
    .addSource(new FlinkKafkaConsumer(KafkaConfig.topic, new SimpleStringSchema() , KafkaConfig.properties))
    .map(Json.parse(_).as[Transport])

  // Em seguidas, realizamos um chaveamento para as mensagens de um mesmo produto.
  // Dessa maneira, podemos garantir que todos os elementos que estarão na próxima etapa da stream são de um mesmo produto
  val keyedStream = dataStream.keyBy(_.productId)

  // Com as mensagens chaveadas, aplicamos o padrão definido anteriormente e,
  // para cada padrão identificado, apresentamos o resumo da viagem
  val cepStream = CEP.pattern(keyedStream, pattern)
    .flatSelect(StreamUtils.getDescribeTravelFunction)

  // Por fim, apresentamos no terminal os resumos das viagens
  cepStream.print()

  // Uma vez que o pipeline esteja definido, podemos iniciá-lo
  env.execute("CEP example")

}
