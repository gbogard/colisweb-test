package colisweb.transporters.domain

import cats.effect.IO
import colisweb.shared.Filter

trait TransporterService {
  def createTransporter(transporter: Transporter): IO[Transporter]
  def getTransporters(filters: List[Filter]): IO[List[Transporter]]
}
