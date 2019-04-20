package com.paintbatch.routes

import cats.Applicative
import com.paintbatch.models.{Healthcheck, Status}
import com.paintbatch.server.HealthcheckRoutes
import org.http4s.Request

final class HealthcheckRoutesImpl[F[_]](implicit F: Applicative[F]) extends HealthcheckRoutes[F] {
  override def getHealthcheck(_req: Request[F]) = F.pure(GetHealthcheckResponse.HTTP200(Healthcheck(Status.Ok)))

  override def apiVersionMatch(req: org.http4s.Message[F]) = true
}