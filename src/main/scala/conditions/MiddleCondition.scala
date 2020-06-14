package conditions

import model.Transport
import org.apache.flink.cep.pattern.conditions.IterativeCondition
import scala.collection.JavaConverters._

object MiddleCondition extends IterativeCondition[Transport]{
  override def filter(value: Transport, ctx: IterativeCondition.Context[Transport]): Boolean = {
    // obtem os elementos intermediários
    val elements = ctx.getEventsForPattern("middle").asScala

    val currentLocation =
      // caso não haja elementos intermediários, obtemos o destino do elemento inicial
      if(elements.isEmpty) ctx.getEventsForPattern("start").asScala.head.to
      // caso haja, obtemos o último destino registrado
      else elements.last.to

    // verificamos se o transporte inicia-se do último destino recebido
    value.from == currentLocation
  }
}
