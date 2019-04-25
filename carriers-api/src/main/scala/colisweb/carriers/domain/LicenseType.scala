package colisweb.carriers.domain

import scala.util.{Try, Success, Failure}
import io.circe.{Decoder, Encoder}

sealed trait LicenseType {
  def toString: String
}

object LicenseType {
  case object A extends LicenseType {
    override def toString: String = "A"
  }
  case object B extends LicenseType {
    override def toString: String = "B"
  }
  case object C extends LicenseType {
    override def toString: String = "C"
  }

  def apply(str: String): Try[LicenseType] = str match {
    case "A" => Success(A)
    case "B" => Success(B)
    case "C" => Success(C)
    case _ => Failure(new RuntimeException("Invalid license type"))
  }

  implicit val encodeLicenseType: Encoder[LicenseType] = Encoder.encodeString.contramap[LicenseType](_.toString)

  implicit val decodeLicenseType: Decoder[LicenseType] = Decoder.decodeString.emapTry(LicenseType(_))
}
