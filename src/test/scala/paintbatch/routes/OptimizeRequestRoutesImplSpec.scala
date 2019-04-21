package com.paintbatch.routes

import com.paintbatch.SolutionFileHarness
import scala.io.Source
import cats.effect.IO
import org.http4s.client.{Client => Http4sClient}
import com.paintbatch.{Client => PaintBatchOptimizerClient}
import com.paintbatch.models.{OptimizeRequest, Demand, Paint, Sheen}
import com.paintbatch.models.{Healthcheck, Status}
import com.paintbatch.PaintBatchOptimizer
import org.http4s.Uri
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}

class OptimizeRequestRoutesImplSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {
  val client = Http4sClient.fromHttpService(new OptimizeRequestRoutesImpl[IO].service)
  val httpClient = new PaintBatchOptimizerClient(
    baseUrl = Uri.unsafeFromString("http://localhost"),
    httpClient = client)

      s"An optimize service" when {
        SolutionFileHarness
          .getOptimizeTests()
          .zipWithIndex
          .foreach(optimizeTestInc => {
            val (optimizeTest, inc) = optimizeTestInc
            s"receives a request ${inc + 1}" must {
              s"process the POST correctly returning ${optimizeTest.expected}" in {
                httpClient.OptimizeRequests
                  .postOptimize(optimizeTest.optimizeRequest)
                  .unsafeRunSync() mustBe optimizeTest.expected
              }
            }
        })
    }
}


  