package colisweb.gateway

import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.client.blaze._
import org.http4s.client._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.dsl.io._
import colisweb.carriers.infrastructure.HttpCarrierService
import colisweb.transporters.infrastructure.HttpTransporterService
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {
  def handleError(resp: Response[IO]) = resp

  def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](global).resource.use { client =>
      val carrierService = new HttpCarrierService(client)
      val transporterService = new HttpTransporterService(client)

      val httpApp = new HttpService(carrierService, transporterService)
        .httpApp
      .map(handleError)
      BlazeServerBuilder[IO]
        .bindHttp(8090, "localhost")
        .withHttpApp(httpApp)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    }
}
