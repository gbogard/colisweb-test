package colisweb.transporters.infrastructure

import cats.effect._
import cats.implicits._
import org.http4s.server.blaze._
import org.http4s.client.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import scala.concurrent.ExecutionContext.Implicits.global
import colisweb.carriers.infrastructure._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = colisweb.shared.Db.transactor.use { xa =>
    BlazeClientBuilder[IO](global).resource.use { client =>
      val repo = new PostgresTransporterRepository(xa)
      val carrierService = new HttpCarrierService(client)
      val transporterService = new TransporterServiceImpl(carrierService, repo)

      BlazeServerBuilder[IO]
        .bindHttp(8092, "localhost")
        .withHttpApp(new HttpService(transporterService).httpApp)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    }
  }
}
