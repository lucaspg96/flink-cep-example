package model

import play.api.libs.json.{Format, Json}

case class Transport(productId: Long,
                     from: String,
                     to: String) {
  override def toString: String =
    s"Product $productId traveled from $from to $to"
}

object Transport {
  implicit val format: Format[Transport] = Json.format
}
