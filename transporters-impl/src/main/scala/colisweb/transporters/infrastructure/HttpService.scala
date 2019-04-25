package colisweb.transporters.infrastructure

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import colisweb.transporters.domain._
import colisweb.transporters.infrastructure.Filters._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.circe._
import io.circe.syntax._

class HttpService(
  transporterService: TransporterService
) {
  implicit val transporterDecoder = jsonOf[IO, Transporter]

  val httpApp = Router(
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root
          :? LicenseTypeParamMatcher(licenseTypeFilter) =>
        val filters = (licenseTypeFilter :: Nil).flatten
        transporterService.getTransporters(filters)
          .map(_.asJson)
          .flatMap(Ok(_))
      case req @ POST -> Root =>
        req.as[Transporter]
          .flatMap(transporterService.createTransporter(_))
          .map(_.asJson)
          .flatMap(Ok(_))
    }
  ).orNotFound
}
