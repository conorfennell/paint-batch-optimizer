package com.paintbatch.routes

import cats.effect.IO
import org.http4s.client.{Client => Http4sClient}
import com.paintbatch.{Client => PaintBatchOptimizerClient}
import com.paintbatch.models.{Healthcheck, Status}
import com.paintbatch.PaintBatchOptimizer
import org.http4s.Uri
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}

class HealthcheckServiceSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {
  val client = Http4sClient.fromHttpService(new HealthcheckRoutesImpl[IO].service)
  val httpClient = new PaintBatchOptimizerClient(
    baseUrl = Uri.unsafeFromString("http://localhost"),
    httpClient = client)
  
  "A healthcheck service" when {
    "receives a request to perform a healthcheck" must {
      "process GET correctly" in {
        httpClient.healthchecks.getHealthcheck().unsafeRunSync() mustBe Healthcheck(Status.Ok)
      }
    }
  }
}
