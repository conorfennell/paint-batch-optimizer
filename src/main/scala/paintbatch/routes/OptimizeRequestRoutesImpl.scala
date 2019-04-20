package com.paintbatch.routes

import cats.Applicative
import com.paintbatch.server.OptimizeRequestRoutes
import com.paintbatch.models.{OptimizeRequest, ReadableError}
import org.http4s.{Request, DecodeResult}
import io.circe.Encoder
import io.circe.syntax._
import io.circe._
import cats.data.EitherT

final class OptimizeRequestRoutesImpl[F[_]](implicit F: Applicative[F]) extends OptimizeRequestRoutes[F] {

  def postOptimize(
    _req: Request[F],
    body: => DecodeResult[F, OptimizeRequest]
  ): F[PostOptimizeResponse] = {
    val result = body
    .leftMap(error => PostOptimizeResponse.HTTP422(ReadableError(error.toString)))
    .map(optimize)
  
    result.fold(
      identity, 
      optimize => PostOptimizeResponse.HTTP200(optimize.toString(), Nil)
      )
  }

  def optimize(optimize: OptimizeRequest): String = {
    "0 1 0 1 1 1 1 1"
    "IMPOSSIBLE"
  }

  override def apiVersionMatch(req: org.http4s.Message[F]) = true
}