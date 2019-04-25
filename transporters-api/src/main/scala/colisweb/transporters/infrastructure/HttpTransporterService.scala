package colisweb.transporters.infrastructure

import org.http4s.client.Client
import cats.effect.IO
import colisweb.shared.Filter
import colisweb.carriers.domain.CarrierService
import colisweb.carriers.domain.Carrier.Filters._
import colisweb.transporters.domain._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.client._
import org.http4s.client.dsl.io._
import io.circe.syntax._

class HttpTransporterService(httpClient: Client[IO]) extends TransporterService {
  val port = sys.env.getOrElse("TRANSPORTER_SERVICE_PORT", "8092")
  val host  = sys.env.getOrElse("TRANSPORTER_SERVICE_HOST", "localhost")
  val baseUri = Uri.fromString(s"http://$host:$port").right.get

  def createTransporter(transporter: Transporter): IO[Transporter] = {
    val request = POST(
      transporter.asJson,
      baseUri,
    )
    httpClient.expect[Transporter](request)( jsonOf[IO, Transporter])
  }

  def getTransporters(filters: List[Filter]): IO[List[Transporter]] = {
    val uri = filters.foldLeft(baseUri)((uri, filter) => filter match {
      case WithLicenseType(license) => uri.withQueryParam("with_license_type", license.toString)
      case _ => uri
    })
    val request = GET(uri)
    httpClient.expect[List[Transporter]](request)(jsonOf[IO, List[Transporter]])
  }
}
