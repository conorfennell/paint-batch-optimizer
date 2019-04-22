package com.paintbatch.routes

import cats.Applicative
import com.paintbatch.server.OptimizeRequestRoutes
import com.paintbatch.solver.Solver
import com.paintbatch.models.{OptimizeRequest, ReadableError}
import org.http4s.{Request, DecodeResult}
import io.circe.Encoder
import io.circe.syntax._
import io.circe._
import cats.implicits._

final class OptimizeRequestRoutesImpl[F[_]](implicit F: Applicative[F]) extends OptimizeRequestRoutes[F] {

  def postOptimize(
    _req: Request[F],
    body: => DecodeResult[F, OptimizeRequest]
  ): F[PostOptimizeResponse] = {
    val result = body
      .leftMap(error => PostOptimizeResponse.HTTP422(ReadableError(error.toString)))
      .map(optimizeRequest => {
        OptimizeRequestRoutesValidation.validate(optimizeRequest) match {
          case None => Either.right(Solver.optimize(optimizeRequest))
          case Some(constraint) => Either.left(constraint)
        }
      })
  
    result.fold(
      identity, 
      value => {
        value match {
          case Right(solution) => PostOptimizeResponse.HTTP200(solution, Nil)
          case Left(brokenConstraint) => PostOptimizeResponse.HTTP422(brokenConstraint)
        }
      })
  }

  override def apiVersionMatch(req: org.http4s.Message[F]) = true
}