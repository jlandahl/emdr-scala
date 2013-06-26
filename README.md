emdr-scala
==========

Scala tools for working with the
[EVE Market Data Relay](https://eve-market-data-relay.readthedocs.org/en/latest/) project.

EMDR data is received, extracted, summarized, and stored in both MongoDB and
[Cube](https://github.com/square/cube/wiki).

Receiver
--------

The EMDR provides a constant torrent of compressed JSON strings representing buy and sell
orders and price histories. The `Receiver` process taps into the flood of EMDR data and
holds each packet for later processing by the `Processor`. The `Receiver` can be run
on-demand to process data as needed, or full-time to receive a constant stream of
data. The data can be held in anything supported by
[Apache Camel](http://camel.apache.org/) -- by default an ActiveMQ queue is used, but data
can be buffered to local files just as easily. For example, change the `emdr.input` URI in
the `application.properties` file to `file:input` and each packet of data data will be
written to a separate file in the `input` subdirectory of the current directory.

The `Receiver` can be run from [SBT](http://www.scala-sbt.org/) or
[Maven](http://maven.apache.org/). For SBT:

    sbt "run-main org.landahl.emdr.Receiver"

For Maven:

    mvn exec:java -Dexec.mainProgram=org.landahl.emdr.Receiver

Processor
---------

The `Processor` picks up the data from the `Receiver`, decompresses it, parses the JSON
into `UUDIF` objects, and processes order data (history data will be supported in the near
future). Orders are filtered by station to keep the load manageable. By default, orders
are kept for the top 5 trade hub stations, but this can be modified by changing the
`order-filter.stations` list in `application.conf`.

Orders are grouped by location and item type and then summarized into a `MarketReport`
object which is then sent to a Cube server via HTTP POST. A further summarized version
(containing only the buy and sell prices and volumes) is stored separately in a MongoDB
server.
