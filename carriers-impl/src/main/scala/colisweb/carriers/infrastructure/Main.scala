package colisweb.carriers.infrastructure

import cats.effect._
import cats.implicits._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = colisweb.shared.Db.transactor.use { xa =>
    val repo = new PostgresCarrierRepository(xa)
    val service = new CarrierServiceImpl(repo)
    val port = sys.env.getOrElse("CARRIER_SERVICE_PORT", "8091").toInt
    BlazeServerBuilder[IO]
      .bindHttp(port, "localhost")
      .withHttpApp(new HttpService(service).httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
