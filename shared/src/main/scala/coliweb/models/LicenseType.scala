package colisweb.models

sealed trait LicenseType

object LicenseType {
  case object A extends LicenseType
  case object B extends LicenseType
  case object C extends LicenseType
}
