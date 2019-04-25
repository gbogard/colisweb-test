package colisweb.transporters.infrastructure

import java.util.UUID
import doobie._
import doobie.implicits._
import cats.effect.IO
import colisweb.transporters.domain.TransporterRepository
import colisweb.transporters.domain.Transporter
import colisweb.shared.Filter
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.refined._
import doobie.refined.implicits._

class PostgresTransporterRepository(transactor: Transactor[IO]) extends TransporterRepository {
  type TransporterTuple = (UUID, String, String, List[String])

  def toTransporter(t: TransporterTuple) = t match {
    case (id, name, siret, postal_codes) => Transporter(
      Some(id),
      name,
      siret,
      postal_codes,
      Nil
    )
  }

  def getTransporters(filters: List[Filter]): IO[List[Transporter]] = {
    sql"select id, name, SIRET, postal_codes from transporters"
      .query[TransporterTuple]
      .to[List]
      .transact(transactor)
      .map(_.map(toTransporter))
  }

  def createTransporter(transporter: Transporter): IO[Transporter] = {
    sql"""
      insert into transporters(name, SIRET, postal_codes)
      values(${transporter.name}, ${transporter.SIRET}, ${transporter.postal_codes})
      returning id, name, SIRET, postal_codes
    """
      .query[TransporterTuple]
      .unique
      .transact(transactor)
      .map(toTransporter)
  }

}
