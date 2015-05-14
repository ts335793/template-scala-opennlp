package org.template.vanilla

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import opennlp.model.AbstractModel

import org.apache.spark.SparkContext

import grizzled.slf4j.Logger

import opennlp.maxent.GIS

case class AlgorithmParams(nearestWordsQuantity: Integer) extends Params

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): Model = {
    val gis = GIS.trainModel(data.basicEventStream, 100, 2, true, true)
    new Model(gis = gis)
  }

  def predict(model: Model, query: Query): PredictedResult = {
    val sentiment = model.gis.getBestOutcome(model.gis.eval(query.phrase.split(" "))).toInt
    PredictedResult(sentiment = sentiment)
  }
}

case class Model(
  gis: AbstractModel
) extends Serializable
