package com.paintbatch.solver

import com.paintbatch.models.{OptimizeRequest, ReadableError, Sheen, Demand, Paint}
import com.paintbatch.models.Sheen._

object Solver {
  private val IMPOSSIBLE = "IMPOSSIBLE"
  private val INDEX_OFFSET = 1
  private def getSelectionIndex(paintId: Int) = paintId - INDEX_OFFSET

  def optimize(optimize: OptimizeRequest): String = {
    val selection = Array.fill[Option[Sheen]](optimize.colors)(None)

    val (matteCustomers, mixedSheenCustomers) = optimize
      .demands
      .sortBy(demand => demand.numberOfPaints)
      .partition(demand => {
        val matteOnly = demand.numberOfPaints == 1 && demand.paints.head.sheen == Matte
        matteOnly
      })

    solve(matteCustomers, mixedSheenCustomers, selection)
  }

  private def solution(selection: Array[Option[Sheen]]): String =
    selection.map {
      case Some(Matte) => "1"
      case Some(Gloss) => "0"
      case _ => "0"
    }.mkString(" ")

  private def solve(matteCustomers: Seq[Demand], mixedSheenCustomers: Seq[Demand], selection: Array[Option[Sheen]]): String = {
    matteCustomers match {
      case Nil => solution(selection)
      case matteCustomer :: tailMatteCustomers => {
        val mattePaint = matteCustomer.paints.head
        val paintIndex = getSelectionIndex(mattePaint.id)

        selection(paintIndex) match {
          case Some(Gloss) => IMPOSSIBLE
          case Some(Matte) => solve(tailMatteCustomers, mixedSheenCustomers, selection)
          case _ => {
            selection(paintIndex) = Some(Matte)
            var newMatteCustomers = tailMatteCustomers
            var newMixedSheenCustomers = List.empty[Demand]
            var impossible = false

            for (customer <- mixedSheenCustomers) {
              var customerSatisfied = false
              var viablePaints = List.empty[Paint]

              for (paint <- customer.paints) {
                val paintIndex = getSelectionIndex(paint.id)
                selection(paintIndex) match {
                  case Some(sheen) if sheen == paint.sheen => customerSatisfied = true
                  case None => viablePaints = paint :: viablePaints
                  case _ => // different sheen already selected, drop paint
                }
              }

              if (!customerSatisfied) {
                viablePaints match {
                  case Nil => impossible = true
                  case Paint(id, Gloss) :: Nil => selection(getSelectionIndex(id)) = Some(Gloss)
                  case Paint(_, Matte) :: Nil => newMatteCustomers = Demand(viablePaints.length ,viablePaints) :: newMatteCustomers
                  case _ => newMixedSheenCustomers = Demand(viablePaints.length, viablePaints) :: newMixedSheenCustomers
                }
              }
            }

            if (impossible) IMPOSSIBLE
            else solve(newMatteCustomers, newMixedSheenCustomers, selection)
          }
        }
      }
    }
  }
}