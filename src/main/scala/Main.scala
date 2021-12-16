import zio._
import zhttp.http._
import zhttp.service.Server

object Main extends App {
  val app: Http[Any, Nothing, Request, UResponse] = Http.collect[Request] {
    case Method.GET -> !! => Response.text("Hello World!")
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Server.start(8090, app).exitCode
}