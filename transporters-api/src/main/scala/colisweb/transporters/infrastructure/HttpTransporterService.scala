package colisweb.transporters.infrastructure

import cats.effect.IO
import colisweb.shared.Filter
import colisweb.carriers.domain.CarrierService
import colisweb.transporters.domain._

class HttpTransporterService(carrierService: CarrierService) extends TransporterService {

  def createTransporter(transporter: Transporter): IO[Transporter] = IO.pure(transporter)

  def getTransporters(filters: List[Filter]): IO[List[Transporter]] = IO.pure(Nil)

}
