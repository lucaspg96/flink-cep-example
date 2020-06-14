package conditions

import model.Transport
import org.apache.flink.cep.pattern.conditions.IterativeCondition
import scala.collection.JavaConverters._

object EndCondition extends IterativeCondition[Transport] {

  override def filter(value: Transport, ctx: IterativeCondition.Context[Transport]): Boolean = {
    val route = ctx.getEventsForPattern("middle").asScala
    val currentPosition = route.last.to

    // verificamos se o transporte finalizou no ponto pré-definido
    // e se ele parte da última localização registrada
    value.to == TRANSPORT_FINISH_POSITION && value.from == currentPosition
  }

}
