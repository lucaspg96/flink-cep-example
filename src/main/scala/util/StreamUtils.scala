package util

import java.util

import model.Transport
import org.apache.flink.cep.{PatternFlatSelectFunction, PatternSelectFunction}
import org.apache.flink.util.Collector

import scala.collection.JavaConverters._

object StreamUtils {

  def getDescribeTravelFunction = new PatternFlatSelectFunction[Transport, String] {
    override def flatSelect(pattern: util.Map[String, util.List[Transport]], out: Collector[String]): Unit = {
      out.collect(
        pattern.asScala
        .map{case (k,v) => s"$k: ${v.asScala.mkString(" | ")}"}
        .mkString("-----\n","\n","\n-----")
      )
    }
  }

}
