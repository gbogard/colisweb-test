package colisweb.carriers.infrastructure

import org.http4s.client.Client
import colisweb.carriers.domain._
import cats.effect._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.client._
import org.http4s.client.dsl.io._
import colisweb.shared.Filter
import colisweb.carriers.domain.Carrier.Filters._
import io.circe.syntax._


class HttpCarrierService(httpClient: Client[IO]) extends CarrierService {
  val port = sys.env.getOrElse("CARRIER_SERVICE_PORT", "8091")
  val host  = sys.env.getOrElse("CARRIER_SERVICE_HOST", "localhost")
  val baseUri = Uri.fromString(s"http://$host:$port").right.get

  def createCarrier(carrier: Carrier): IO[Carrier] = {
    val request = POST(
      carrier.asJson,
      baseUri,
    )
    httpClient.expect[Carrier](request)( jsonOf[IO, Carrier])
  }

  def getCarriers(filters: List[Filter]): IO[List[Carrier]] = {
    val uri = filters.foldLeft(baseUri)((uri, filter) => filter match {
      case WithTransporterId(id) => uri.withQueryParam("with_transporter_id", id.toString)
      case _ => uri
    })
    val request = GET(uri)
    httpClient.expect[List[Carrier]](request)(jsonOf[IO, List[Carrier]])
  }
}
