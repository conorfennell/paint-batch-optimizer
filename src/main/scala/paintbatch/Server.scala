package com.paintbatch

import com.paintbatch.routes.{HealthcheckRoutesImpl, OptimizeRequestRoutesImpl}
import fs2._
import cats.effect._
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.blaze.BlazeServerBuilder

import cats.implicits._
import org.http4s.implicits._

object PaintBatchOptimizerServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    PaintBatchOptimizer.stream[IO].compile.drain.as(ExitCode.Success)
}

object PaintBatchOptimizer {
  val bindHost = sys.env.getOrElse("HOST", "0.0.0.0")
  val bindPort = sys.env.getOrElse("PORT", "8080").toInt

  def httpRoutes[F[_]: ConcurrentEffect]: HttpRoutes[F] =
    new HealthcheckRoutesImpl[F].service() <+> new OptimizeRequestRoutesImpl[F].service()

  def httpApp[F[_]: ConcurrentEffect: ContextShift: Timer]: HttpApp[F] =
    httpRoutes.orNotFound

  def stream[F[_]: ConcurrentEffect: Timer: ContextShift]: Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(bindPort, bindHost)
      .withHttpApp(httpApp[F])
      .serve
  }
}