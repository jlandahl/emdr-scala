package org.landahl.emdr.model

case class OrderSummary(min: Double, max: Double, mean: Double, median: Double, stddev: Double, volume: Long)

object OrderSummary {
  import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
  def summarize(orders: Iterable[Order]): Option[OrderSummary] = {
    if (orders == Nil)
      None
    else {
      val stats = orders.foldLeft(new DescriptiveStatistics) { (stats, order) => stats.addValue(order.price); stats}
      Some(OrderSummary(
        min = stats.getMin,
        max = stats.getMax,
        mean = stats.getMean,
        median = stats.getPercentile(50),
        stddev = stats.getStandardDeviation,
        volume = orders.map(_.volRemaining).sum))
    }
  }
}
