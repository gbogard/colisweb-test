package colisweb.carriers.infrastructure

import cats.effect.IO
import colisweb.carriers.domain._
import colisweb.shared.Filter

class CarrierServiceImpl(repository: CarrierRepository) extends CarrierService {

  def getCarriers(filters: List[Filter]): IO[List[Carrier]] = repository.getCarriers(filters)

  def createCarrier(carrier: Carrier): IO[Carrier] = repository.createCarrier(carrier)
}
