package colisweb.transporters.domain

import cats.effect._
import colisweb.shared.Filter

trait TransporterRepository {

  def getTransporters(filters: List[Filter]): IO[List[Transporter]]

  def createTransporter(transporter: Transporter): IO[Transporter]
}
