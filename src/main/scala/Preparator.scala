package org.template.vanilla

import io.prediction.controller.PPreparator

import opennlp.maxent.{DataStream, BasicEventStream}

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.JavaConversions._

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
    val events = trainingData.labeledPhrases.map[String] { lp => s"${lp.phrase} ${lp.sentiment}" }
    val dataStream = Stream(events.collect.toList)
    val basicEventStream = new BasicEventStream(dataStream, " ")
    new PreparedData(basicEventStream = basicEventStream)
  }
}

class PreparedData(val basicEventStream: BasicEventStream) extends Serializable
