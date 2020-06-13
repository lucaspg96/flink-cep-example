package conditions

import model.Transport
import org.apache.flink.cep.pattern.conditions.SimpleCondition

object StartCondition extends SimpleCondition[Transport] {
  override def filter(value: Transport): Boolean = value.from == TRANSPORT_START_POSITION
}
