package colisweb.gateway

import java.util.UUID
import org.http4s._
import org.http4s.dsl.io._
import colisweb.carriers.domain.Carrier.Filters._
import colisweb.carriers.domain.LicenseType
import scala.util.Failure

object Filters {
  case object InvalidLicenseException extends Exception("Invalid License Type")

  implicit val licenseTypeParamDecoder: QueryParamDecoder[WithLicenseType] =
    QueryParamDecoder[String]
      .map(LicenseType(_)
        .orElse(Failure(InvalidLicenseException)).get)
      .map(WithLicenseType(_))

  object LicenseTypeParamMatcher
      extends OptionalQueryParamDecoderMatcher[WithLicenseType]("with_license_type")

  implicit val transporterIdParamDecoder: QueryParamDecoder[WithTransporterId] =
    QueryParamDecoder[String].map(UUID.fromString).map(WithTransporterId(_))

  object TransporterIdTypeParamMatcher
      extends OptionalQueryParamDecoderMatcher[WithTransporterId]("with_transporter_id")
}
