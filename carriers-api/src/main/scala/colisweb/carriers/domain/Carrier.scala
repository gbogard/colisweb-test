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

case class Carrier(
  id: Option[UUID],
  name: String,
  age: Int Refined GreaterEqual[W.`18`.T],
  licenses: List[LicenseType],
  transporterId: Option[UUID],
)

object Carrier {
  implicit val encodeCarrier: Encoder[Carrier] = deriveEncoder
  implicit val decodreCarrier: Decoder[Carrier] = deriveDecoder

  object Filters {
    case class WithLicenseType(licenseType: LicenseType) extends colisweb.shared.Filter
  }
}
