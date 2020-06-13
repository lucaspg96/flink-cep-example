package source

import java.util.concurrent.TimeUnit

import model.Transport
import org.apache.flink.streaming.api.functions.source.SourceFunction
import conditions._


object TransportsGeneratorSource extends SourceFunction[Transport]{

  private var productId = 0

  private def generateTravel: Seq[Transport] = {
    val travelSize = scala.util.Random.nextInt(10)
    productId += 1
    Seq(
      Seq(TRANSPORT_START_POSITION),
      0 to travelSize map (i => s"M$i"),
      Seq(TRANSPORT_FINISH_POSITION)
    )
      .flatten
      .sliding(2)
      .map{case from :: to :: Nil => Transport(productId, from, to)}
      .toSeq
  }

  private def loop(ctx: SourceFunction.SourceContext[Transport]): Unit = {
    print("Enter the number of travels to generate: ")
    val n = scala.io.StdIn.readInt()

    for(_ <- 1 to n) generateTravel.foreach(ctx.collect)

    loop(ctx)
  }

  override def run(ctx: SourceFunction.SourceContext[Transport]): Unit = {
    loop(ctx)
  }

  override def cancel(): Unit = {}
}
