package colisweb.shared

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.hikari._

object Db {
  private val host = sys.env.getOrElse("DB_HOST", "localhost")
  private val user = sys.env.getOrElse("DB_USER", "postgres")
  private val password = sys.env.getOrElse("DB_PASSWORD", "")
  private val port = sys.env.getOrElse("DB_PORT", "5436")
  private val name = sys.env.getOrElse("DB_NAME", "postgres")

  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      te <- ExecutionContexts.cachedThreadPool[IO]
      val cs = IO.contextShift(ce)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        s"jdbc:postgresql://$host:$port/$name", 
        user,                           
        password,
        ce,
        te
      )(implicitly[Async[cats.effect.IO]], cs)
    } yield xa
}
