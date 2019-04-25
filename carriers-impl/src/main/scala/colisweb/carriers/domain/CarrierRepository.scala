package colisweb.carriers.domain

import cats.effect.IO
import colisweb.shared.Filter

trait CarrierRepository {

  def createCarrier(carrier: Carrier): IO[Carrier]

  def getCarriers(filters: List[Filter]): IO[List[Carrier]]
}
