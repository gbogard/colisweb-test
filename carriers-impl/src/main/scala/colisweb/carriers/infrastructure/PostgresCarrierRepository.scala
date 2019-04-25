package colisweb.carriers.infrastructure

import java.util.UUID
import doobie._
import doobie.implicits._
import cats.effect.IO
import colisweb.carriers.domain._
import colisweb.carriers.domain.Carrier.Filters._
import colisweb.shared.Filter
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.refined._
import doobie.refined.implicits._

class PostgresCarrierRepository(transactor: Transactor[IO]) extends CarrierRepository {
  import PostgresCarrierRepository._

  type CarrierTuple = (UUID, String, Carrier.Age, List[LicenseType], UUID)

  def toCarrier(t: CarrierTuple) = t match {
    case (id, name, age, licenses, transporterId) => Carrier(
      Some(id),
      name,
      age,
      licenses,
      Some(transporterId)
    )
  }

  def getCarriers(filters: List[Filter]): IO[List[Carrier]] = {
    filters.map({
      case WithTransporterId(id) => sql"where transporter_id = $id"
      case WithLicenseType(license) => sql""
    }).foldLeft(sql"select id, name, age, licenses, transporter_id from carriers ")(_ ++ _)
      .query[CarrierTuple]
      .to[List]
      .transact(transactor)
      .map(_.map(toCarrier))
  }

  def createCarrier(carrier: Carrier): IO[Carrier] = {
    sql"""
      insert into carriers(name, age, licenses, transporter_id)
      values(${carrier.name}, ${carrier.age}, ${carrier.licenses}, ${carrier.transporter_id})
      returning id, name, age, licenses, transporter_id
    """
      .query[CarrierTuple]
      .unique
      .transact(transactor)
      .map(toCarrier)
  }

}

object PostgresCarrierRepository {
  implicit val licenseTypesGet: Get[List[LicenseType]] = Get[List[String]].tmap(_.map(LicenseType.apply(_).get))
  implicit val licenseTypesPut: Put[List[LicenseType]] = Put[List[String]].tcontramap(_.map(_.toString))
}
