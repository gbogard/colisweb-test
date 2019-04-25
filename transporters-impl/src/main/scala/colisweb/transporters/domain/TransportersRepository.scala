package colisweb.transporters.domain

import cats.effect._
import colisweb.shared.Filter

trait TransportersRepository {

  def getTransporters(filters: List[Filter])

  def createTransporter(transporter: Transporter): IO[Transporter]
}
