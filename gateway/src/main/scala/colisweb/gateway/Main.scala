package colisweb.gateway

import cats.effect._
import cats.implicits._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import colisweb.carriers.infrastructure.HttpCarrierService
import colisweb.transporters.infrastructure.HttpTransporterService
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {
  val carrierService = new HttpCarrierService()
  val transporterService = new HttpTransporterService(carrierService)

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8090, "localhost")
      .withHttpApp(new HttpService(carrierService, transporterService).httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
