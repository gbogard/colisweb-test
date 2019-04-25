package colisweb.gateway

import org.http4s._
import org.http4s.dsl.io._
import colisweb.carriers.domain.Carrier.Filters._
import colisweb.carriers.domain.LicenseType

object Filters {
  implicit val licenseTypeParamDecoder: QueryParamDecoder[WithLicenseType] =
    QueryParamDecoder[String].map(LicenseType(_).get).map(WithLicenseType(_))

  object LicenseTypeParamMatcher extends OptionalQueryParamDecoderMatcher[WithLicenseType]("with_license_type")
}
