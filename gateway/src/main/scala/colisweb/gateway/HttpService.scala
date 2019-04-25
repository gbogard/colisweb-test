package colisweb.gateway

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import colisweb.carriers.domain.CarrierService
import colisweb.transporters.domain.TransporterService
import colisweb.transporters.domain.Transporter
import colisweb.gateway.Filters._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.circe._
import io.circe.syntax._

class HttpService(
  carrierService: CarrierService,
  transporterService: TransporterService
) {

  implicit val transporterDecoder = jsonOf[IO, Transporter]

  private val root = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(s"Hello Colisweb!")
  }

  private val transporters = HttpRoutes.of[IO] {
    case GET -> Root :? LicenseTypeParamMatcher(licenseTypeFilter) =>
      val filters = (licenseTypeFilter :: Nil).flatten
      transporterService.getTransporters(filters)
        .map(_.asJson)
        .flatMap(Ok(_))
    case req @ POST -> Root =>
      req.as[Transporter]
        .flatMap(transporterService.createTransporter)
        .map(_.asJson)
        .flatMap(Ok(_))
  }

  val httpApp = Router(
    "/" -> root,
    "/transporters" -> transporters,
  ).orNotFound
}