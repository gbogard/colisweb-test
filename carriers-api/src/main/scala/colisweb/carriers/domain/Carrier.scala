package colisweb.carriers.domain

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import java.util.UUID
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._
import eu.timepit.refined.numeric._
import colisweb.carriers.domain.LicenseType._
import colisweb.shared.Filter

case class Carrier(
  id: Option[UUID],
  name: String,
  age: Carrier.Age,
  licenses: List[LicenseType],
  transporter_id: Option[UUID],
)

object Carrier {
  type Age = Int Refined GreaterEqual[W.`18`.T]

  implicit val encodeCarrier: Encoder[Carrier] = deriveEncoder
  implicit val decodreCarrier: Decoder[Carrier] = deriveDecoder

  object Filters {
    case class WithLicenseType(licenseType: LicenseType) extends Filter
    case class WithTransporterId(transporterId: UUID) extends Filter
  }
}
