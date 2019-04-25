package colisweb.models

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._

import eu.timepit.refined.numeric._

case class Carrier(
  name: String,
  age: Int Refined GreaterEqual[W.`18`.T],
  licences: List[LicenseType]
)

object Carrier {

}
