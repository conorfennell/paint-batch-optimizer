package com.paintbatch

import com.paintbatch.models.{OptimizeRequest, Demand, Paint, Sheen}
import scala.io.Source

case class OptimizeTest(optimizeRequest: OptimizeRequest, expected: String)

object SolutionFileHarness {
  private val numberOfTestsIndex = 0
  private val numberOfColorsIndex = 0
  private val numberOfCustomerPaints = 1
  private val numberOfCustomerPaintsIndex = 0
  private val paintIndex = 0
  private val sheenIndex = 1
  private val testOutIndex = 0

  def getOptimizeTests(): List[OptimizeTest] = {
    val testsInFile  = getClass.getResource("/optimizer-tests-in.txt").getFile()
    val testsOutFile  = getClass.getResource("/optimizer-tests-out.txt").getFile()
    val testsInLines = Source.fromFile(testsInFile).getLines.toList
    val testsOutLines = Source.fromFile(testsOutFile).getLines.toList
    val numberOfTests = Integer.parseInt(testsInLines(numberOfTestsIndex))

    buildOptimizeTests(testsInLines.tail, testsOutLines)
  }

  def buildOptimizeTests(testsIn: List[String], testsOut: List[String]): List[OptimizeTest] = {
    testsIn match {
      case Nil => Nil
      case _ => {
        val colors = Integer.parseInt(testsIn(numberOfColorsIndex))
        val customers = Integer.parseInt(testsIn(numberOfCustomerPaints))
    
        val demands = testsIn.tail.tail.take(customers).map(customerLine => {
          val customer = customerLine.split(" ")
          val numberOfPaints = Integer.parseInt(customer(numberOfCustomerPaintsIndex))
    
          val paints = customer.tail.grouped(2)
            .map(pair => {
              val paint = Integer.parseInt(pair(paintIndex))
              val sheen = pair(sheenIndex) match {
                case "0" => Sheen.Gloss
                case "1" => Sheen.Matte
              }
              Paint(paint, sheen)
            })
            .toSeq
          
          Demand(numberOfPaints, paints)
        })
    
        List(OptimizeTest(OptimizeRequest(colors, customers, demands), testsOut(testOutIndex))) ++
        buildOptimizeTests(testsIn.drop(2 + customers), testsOut.drop(1))
      }
    }
  }
}