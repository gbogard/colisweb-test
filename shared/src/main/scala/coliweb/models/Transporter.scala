package colisweb.models

case class Transporter(
  name: String,
  SIRET: String,
  postal_codes: List[String],
  carriers: List[Carrier]
)
