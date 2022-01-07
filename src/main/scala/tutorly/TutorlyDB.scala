package tutorly

import io.getquill.context.ZioJdbc._
import io.getquill.util.LoadConfig
import io.getquill._
import zio._
import javax.sql.DataSource

case class Person(id: Int, name: String, age: Int)

object TutorlyDB {

  import io.getquill.context.qzio.ImplicitSyntax._

  val impDs: DataSource = JdbcContextConfig(LoadConfig("tutorlyDB")).dataSource
  implicit val env: Implicit[Has[DataSource]] = Implicit(Has(impDs))

  object Ctx extends PostgresZioJdbcContext(Literal)

  import Ctx._

  val persons: Quoted[EntityQuery[Person]] = quote {
    querySchema[Person]("Person")
  }

  // type alias to use for other layers
  type TutorlyDBEnv = Has[TutorlyDB.Service]

  /**
   * Service Definition
   */
  trait Service {
    def insert(person: Person): Task[Long]

    def getAll: Task[List[Person]]

    def byName(name: String): Task[List[Person]]
  }

  /**
   * Service Implementation
   */
  val live: ZLayer[ZEnv, Nothing, TutorlyDBEnv] = ZLayer.succeed {
    new Service {
      override def insert(person: Person): Task[Long] = {
        val insertQuery = quote {
          persons.insert(lift(person))
        }
        for {
          i <- Ctx.run(insertQuery).implicitDS
        } yield i
      }

      override def getAll: Task[List[Person]] = for {
        ps <- Ctx.run(persons).implicitDS
      } yield ps

      override def byName(name: String): Task[List[Person]] = {
        val filterQuery = quote {
          query[Person].filter(p => p.name == lift(name))
        }
        for {
          ps <- Ctx.run(filterQuery).implicitDS
        } yield ps
      }
    }
  }

  // accessor
  def insert(person: Person): ZIO[TutorlyDBEnv, Throwable, Long] =
    ZIO.accessM(_.get.insert(person))

  def getAll: ZIO[TutorlyDBEnv, Throwable, List[Person]] =
    ZIO.accessM(_.get.getAll)

  def byName(name: String): ZIO[TutorlyDBEnv, Throwable, List[Person]] =
    ZIO.accessM(_.get.byName(name))

}