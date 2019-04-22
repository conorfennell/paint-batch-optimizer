package com.paintbatch.solver

import com.paintbatch.models.{OptimizeRequest, ReadableError, Sheen, Demand, Paint}
import com.paintbatch.models.Sheen._

object Solver {
  private val IMPOSSIBLE = "IMPOSSIBLE"
  private val PAINT_INDEX_OFFSET = 1
  private def getSelectionIndex(paintId: Int) = paintId - PAINT_INDEX_OFFSET

  /**
   * The trick to the paint batch optimization is to satisfy the single preference matte customers.
   * When they are satisfied, the remaining customers can all be satisfied with a gloss selection.
   * 
   * The algorithm starts by partitioning customers into single preference matte customers and mixed
   * sheen customers.
   * 
   * Recursively the algorithm satisfies one matte customer if possible otherwise it is IMPOSSIBLE, then checks 
   * how this effects each mixed sheen customers resulting in the following for them:
   *  1. mixed sheen customer will be satisfied and removed from the mixed sheen customers
   *  2. mixed sheen customer will be left with one gloss preference and will be assigned that selection and removed from the mixed sheen customers
   *  3. mixed sheen customer will be left with one matte preference and added to the single preference matte customers
   *  4. mixed sheen customer will be left with no viable preferences and it will become IMPOSSIBLE
   * 
   * If there are no single preference matte customers to recurse on then we have a solution and assign remaining to GLOSS
   *  
   * @param paints paints the customer would be satisfied with
   */
  def optimize(optimize: OptimizeRequest): String = {
    val selection = Array.fill[Option[Sheen]](optimize.colors)(None)

    val (matteCustomers, mixedSheenCustomers) = optimize
      .demands
      .sortBy(demand => demand.paints.length)
      .partition(demand => {
        val matteOnly = demand.paints.length == 1 && demand.paints.head.sheen == Matte
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
                  case Paint(_, Matte) :: Nil => newMatteCustomers = Demand(viablePaints) :: newMatteCustomers
                  case _ => newMixedSheenCustomers = Demand(viablePaints) :: newMixedSheenCustomers
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