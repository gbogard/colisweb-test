package colisweb.transporters.infrastructure

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import cats.effect.ContextShift
import cats._
import cats.data._
import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._
import colisweb.transporters.domain._
import colisweb.carriers.domain._
import colisweb.carriers.domain.Carrier.Filters._
import colisweb.shared.Filter

class TransporterServiceImpl(
  carrierService: CarrierService,
  transporterRepository: TransporterRepository,
) extends TransporterService {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit val timer = IO.timer(ExecutionContext.global)

  private def retryWithBackoff[A](ioa: IO[A], initialDelay: FiniteDuration = 10 seconds, maxRetries: Int = 4)
    (implicit timer: Timer[IO]): IO[A] = {

    ioa.handleErrorWith { error =>
      if (maxRetries > 0)
        IO.sleep(initialDelay) *> retryWithBackoff(ioa, initialDelay * 2, maxRetries - 1)
      else
        IO.raiseError(error)
    }
  }

  private def createCarriers(carriers: List[Carrier]): IO[List[Carrier]] = {
    if (carriers.isEmpty) {
      IO.pure(Nil)
    } else {
      NonEmptyList.fromListUnsafe(carriers).parTraverse(carrierService.createCarrier(_)).map(_.toList)
    }
  }

  private def fetchCarriers(transporter: Transporter): IO[List[Carrier]] = transporter.id match {
    case Some(id) => carrierService.getCarriers(WithTransporterId(id) :: Nil)
    case None => IO.pure(Nil)
  }

  def createTransporter(transporter: Transporter): IO[Transporter] = for {
    newTransporter <- transporterRepository.createTransporter(transporter)
    val carriersWithTransporterId = transporter.carriers.map(_.copy(transporter_id = newTransporter.id))
    val createCarriersIO = createCarriers(carriersWithTransporterId)
    newCarriers <- createCarriersIO.handleErrorWith { _ =>
      retryWithBackoff(createCarriersIO).start.map(_ => Nil)
    }
  } yield newTransporter.copy(carriers = newCarriers)

  def getTransporters(filters: List[Filter]): IO[List[Transporter]] = {
    val transportersWithCarriers = transporterRepository.getTransporters(filters)
      .flatMap(transporters =>
        if (transporters.nonEmpty) {
          NonEmptyList.fromListUnsafe(transporters).parTraverse(t =>
            fetchCarriers(t).map(c => t.copy(carriers = c))
          ).map(_.toList)
        } else {
          IO.pure(Nil)
        }
      )

    // Mapping over filters allows any number of filters to work, even though there is only
    // one filter for the moment.
    transportersWithCarriers.map(_.filter { t =>
      filters.map({
        case WithLicenseType(license) => t.carriers.exists(_.licenses.contains(license))
      }).fold(true)(_ && _)
    })
  }
}
