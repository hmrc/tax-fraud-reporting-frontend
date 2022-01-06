/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.taxfraudreportingfrontend.cache

import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.cache._
import uk.gov.hmrc.cache.model.{Cache, Id}
import uk.gov.hmrc.cache.repository.CacheMongoRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.taxfraudreportingfrontend.cache.CachedData.fraudReportDetailsKey
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

sealed case class CachedData(fraudReportDetails: Option[FraudReportDetails] = None) {

  def getFraudReportDetails: FraudReportDetails =
    fraudReportDetails getOrElse FraudReportDetails()

}

object CachedData {
  val fraudReportDetailsKey                = "fraudReportDetails"
  implicit val format: OFormat[CachedData] = Json.format
}

@Singleton
class SessionCache @Inject() (appConfig: AppConfig, mongo: ReactiveMongoComponent)(implicit ec: ExecutionContext)
    extends CacheMongoRepository("session-cache", appConfig.cacheTtl.toSeconds)(mongo.mongoConnector.db, ec) {

  private val eccLogger: Logger = Logger(this.getClass)

  def sessionId(implicit headerCarrier: HeaderCarrier): Id =
    headerCarrier.sessionId match {
      case None =>
        throw new IllegalStateException("Session id is not available")
      case Some(sessionId) => model.Id(sessionId.value)
    }

  def saveFraudReportDetails(
    fraudReportDetails: FraudReportDetails
  )(implicit hc: HeaderCarrier): Future[FraudReportDetails] =
    createOrUpdate(sessionId, fraudReportDetailsKey, Json.toJson(fraudReportDetails)) map { _ => fraudReportDetails }

  def isCachePresent(sessionId: String): Future[Boolean] = findById(sessionId) map {
    _ exists { cache => cache.data.isDefined }
  }

  def isCacheNotPresentCreateOne(sessionId: String)(implicit hc: HeaderCarrier): Future[FraudReportDetails] =
    isCachePresent(sessionId) flatMap { isPresent =>
      if (isPresent)
        getFraudReportDetails
      else
        saveFraudReportDetails(FraudReportDetails(None))
    }

  def getFraudReportDetails(implicit hc: HeaderCarrier): Future[FraudReportDetails] =
    getCached(sessionId, (cachedData, _) => cachedData.getFraudReportDetails)

  def remove(implicit hc: HeaderCarrier): Future[Boolean] =
    removeById(sessionId.id) map (x => x.writeErrors.isEmpty && x.writeConcernError.isEmpty)

  private def getCached[T](sessionId: Id, t: (CachedData, Id) => T): Future[T] =
    findById(sessionId.id) map {
      case Some(Cache(_, Some(data), _, _)) =>
        Json.fromJson[CachedData](data) match {
          case d: JsSuccess[CachedData] => t(d.value, sessionId)
          case _: JsError               =>
            // $COVERAGE-OFF$Loggers
            eccLogger.error(s"No Session data is cached for the sessionId : ${sessionId.id}")
            throw SessionTimeOutException(s"No Session data is cached for the sessionId : ${sessionId.id}")
          // $COVERAGE-ON
        }
      case _ =>
        // $COVERAGE-OFF$Loggers
        eccLogger.info(s"No match session id for signed in user with session: ${sessionId.id}")
        throw SessionTimeOutException(s"No match session id for signed in user with session : ${sessionId.id}")
      // $COVERAGE-ON
    }

}

case class SessionTimeOutException(errorMessage: String) extends NoStackTrace
