package conditions

import model.Transport
import org.apache.flink.cep.pattern.conditions.IterativeCondition
import scala.collection.JavaConverters._

object MiddleCondition extends IterativeCondition[Transport]{
  override def filter(value: Transport, ctx: IterativeCondition.Context[Transport]): Boolean = {
    val elements = ctx.getEventsForPattern("middle").asScala

    val currentLocation =
      if(elements.isEmpty) ctx.getEventsForPattern("start").asScala.head.to
      else elements.last.to

    value.from == currentLocation
  }
}
