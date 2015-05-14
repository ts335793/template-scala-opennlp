package org.template.vanilla

import io.prediction.controller.PPreparator

import opennlp.maxent.{DataStream, BasicEventStream}

import org.apache.spark.SparkContext

case class Stream(var events: List[String])
  extends DataStream {

  override def nextToken = events match {
    case h :: t =>
      events = t
      h
  }

  override def hasNext = events.nonEmpty
}

class Preparator
  extends PPreparator[TrainingData, PreparedData] {

  def prepare(sc: SparkContext, trainingData: TrainingData): PreparedData = {
    val separator = " "
    val events = trainingData.labeledPhrases.map[String] { lp => s"${lp.phrase} ${lp.sentiment}" }
    val dataStream = Stream(events.collect.toList)
    val basicEventStream = new BasicEventStream(dataStream, separator)
    new PreparedData(basicEventStream = basicEventStream, separator = separator)
  }
}

case class PreparedData(
  basicEventStream: BasicEventStream,
  separator: String
) extends Serializable
