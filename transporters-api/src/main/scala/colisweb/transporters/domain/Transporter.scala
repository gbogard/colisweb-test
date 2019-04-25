package colisweb.transporters.domain

import colisweb.carriers.domain.Carrier
import io.circe._
import io.circe.generic.semiauto._

import java.util.UUID

case class Transporter(
  id: Option[UUID],
  name: String,
  SIRET: String,
  postal_codes: List[String],
  carriers: List[Carrier]
)

object Transporter {
  implicit val decodeTransporter: Decoder[Transporter] = deriveDecoder
  implicit val encodeTransporter: Encoder[Transporter] = deriveEncoder
}
