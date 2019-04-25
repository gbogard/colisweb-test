package colisweb.carriers.infrastructure

import colisweb.carriers.domain._
import cats.effect.IO
import colisweb.shared.Filter

class HttpCarrierService extends CarrierService {

  def createCarrier(carrier: Carrier): IO[Carrier] = IO.pure(carrier)

  def getCarriers(filters: List[Filter]): IO[List[Carrier]] = IO.pure(Nil)
}
