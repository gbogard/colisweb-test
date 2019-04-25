package colisweb.carriers.infrastructure

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import colisweb.carriers.domain._
import colisweb.carriers.infrastructure.Filters._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.circe._
import io.circe.syntax._

class HttpService(
  carrierService: CarrierService
) {
  implicit val carrierDecoder = jsonOf[IO, Carrier]

  val httpApp = Router(
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root
          :? LicenseTypeParamMatcher(licenseTypeFilter)
          +& TransporterIdTypeParamMatcher(transporterIdFilter) =>
        val filters = (licenseTypeFilter :: transporterIdFilter :: Nil).flatten
        carrierService.getCarriers(filters)
          .map(_.asJson)
          .flatMap(Ok(_))
      case req @ POST -> Root =>
        req.as[Carrier]
          .flatMap(carrierService.createCarrier(_))
          .map(_.asJson)
          .flatMap(Ok(_))
    }
  ).orNotFound
}
