package colisweb.gateway

import org.scalatest.{FunSpec, Matchers}
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import colisweb.carriers.domain._
import colisweb.transporters.domain._
import org.http4s.circe._

class GatewayHttpServiceSpec extends FunSpec with Matchers {

  implicit val transporterDecoder = jsonOf[IO, List[Transporter]]

  val mockedTransporter = Transporter(
    Some(java.util.UUID.randomUUID()),
    "DHL",
    "SIRET",
    "59000" :: Nil,
    Nil
  )

  val transporterService = new TransporterService {
    def createTransporter(transporter: colisweb.transporters.domain.Transporter): cats.effect.IO[colisweb.transporters.domain.Transporter] = ???
    def getTransporters(filters: List[colisweb.shared.Filter]): cats.effect.IO[List[colisweb.transporters.domain.Transporter]] = {
      if (filters.isEmpty) {
        IO.pure(mockedTransporter :: Nil)
      } else {
        IO.pure(Nil)
      }
    }
  }

  describe("The Gateway Http Service.") {
    it("Should return 'Hello Colisweb!' when '/' is called.") {
      val r = new HttpService(transporterService).httpApp.run(
        Request(method = GET, uri = Uri.uri("/"))
      ).unsafeRunSync()
      assert(r.as[String].unsafeRunSync == "Hello Colisweb!")
    }

    it("Should return the result of calling 'getTransporters' on the transporters service when '/transporters' is called.") {
      val r = new HttpService(transporterService).httpApp.run(
        Request(method = GET, uri = Uri.uri("/transporters"))
      ).unsafeRunSync()
      assert(r.as[List[Transporter]].unsafeRunSync == mockedTransporter :: Nil)
    }

    it("Should pass the 'with_license_type' filter down to the transporters service when '/transporters' is called.") {
      val r = new HttpService(transporterService).httpApp.run(
        Request(method = GET, uri = Uri.uri("/transporters?with_license_type=A"))
      ).unsafeRunSync()
      assert(r.as[List[Transporter]].unsafeRunSync == Nil)
    }

    it("Should fail with a bad request when the 'with_license_type' filter is invalid on '/transporters'.") {
      val r = new HttpService(transporterService).httpApp.run(
        Request(method = GET, uri = Uri.uri("/transporters?with_license_type=F"))
      ).unsafeRunSync()
      assert(r.status.code === 400)
    }
  }
}
