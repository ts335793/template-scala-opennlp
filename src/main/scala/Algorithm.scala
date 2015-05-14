package org.template.vanilla

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import opennlp.model.AbstractModel

import org.apache.spark.SparkContext

import grizzled.slf4j.Logger

import opennlp.maxent.GIS

case class AlgorithmParams(
  iterations: Integer,
  cutoff: Integer,
  smoothing: Boolean,
  printMessagesWhileTraining: Boolean
) extends Params

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): Model = {
    val gis = GIS.trainModel(data.basicEventStream,
      ap.iterations, ap.cutoff, ap.smoothing, ap.printMessagesWhileTraining)
    new Model(gis = gis, separator = data.separator)
  }

  def predict(model: Model, query: Query): PredictedResult = {
    val sentiment = model.gis.getBestOutcome(model.gis.eval(query.phrase.toLowerCase.split(model.separator))).toInt
    PredictedResult(sentiment = sentiment)
  }
}

case class Model(
  gis: AbstractModel,
  separator: String
) extends Serializable
