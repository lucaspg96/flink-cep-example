package conditions

import model.Transport
import org.apache.flink.cep.pattern.conditions.SimpleCondition

object StartCondition extends SimpleCondition[Transport] {
  // verifica se o transporte inicia-se do ponto inicial pré-estabelecido
  override def filter(value: Transport): Boolean = value.from == TRANSPORT_START_POSITION
}
