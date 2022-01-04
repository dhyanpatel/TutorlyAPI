package tutorly

import tutorly.TutorlyDB.TutorlyDBEnv
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._
import zio.console.putStrLn

import java.util.UUID
import scala.util.Try

object TutorlyServer extends App {
  // Set a port
  private val PORT = 8090

  val tutorlyBackendLayer: ZLayer[zio.ZEnv, Nothing, TutorlyDBEnv] =
    TutorlyDB.live

  import zhttp.http._

  private val app = Http.collectM[Request] {
    case Method.POST -> _ / "person" =>
      for {
        dyn <- ZIO.effect(UUID.randomUUID().toString)
        _ <- TutorlyDB.insert(Person(102, dyn, 27))
        persons <- TutorlyDB.getAll
      } yield Response.text(s"Persons: $persons")

    case Method.GET -> _ / "user" / name =>
      for {
        persons <- TutorlyDB.byName(name)
      } yield Response.text(s"Hello: $persons")
  }

  private val server =
    Server.port(PORT) ++ // Setup port
      Server.app(app) // Setup the Http app

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    // Configure thread count using CLI
    val nThreads: Int = args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(0)

    // horizontally compose dependencies
    val env = ServerChannelFactory.auto ++
      EventLoopGroup.auto(nThreads) ++
      tutorlyBackendLayer

    // Create a new server
    server.make
      .use(_ => putStrLn(s"Server started on port $PORT") *> ZIO.never)
      .provideCustomLayer(env) // inject dependencies
      .exitCode
  }
}