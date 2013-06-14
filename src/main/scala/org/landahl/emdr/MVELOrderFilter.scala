package org.landahl.emdr

import org.mvel2.MVEL

import model.Order

object MVELOrderFilter {
  def makeFilter(expression: String): (Order) => Boolean = {
    val compiledExpression = MVEL.compileExpression(expression)
    (order: Order) => {
      val result = MVEL.executeExpression(compiledExpression, order)
      try {
        result.asInstanceOf[Boolean]
      } catch {
        case _: Exception => false
      }
    }
  }
}