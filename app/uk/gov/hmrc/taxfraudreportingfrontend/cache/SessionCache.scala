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

import play.api.mvc.Request
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.mongo.{CurrentTimestampSupport, MongoComponent}
import uk.gov.hmrc.mongo.cache.{DataKey, SessionCacheRepository}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

@Singleton
class SessionCache @Inject() (appConfig: AppConfig, mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends SessionCacheRepository(
      mongoComponent = mongo,
      collectionName = "session-cache",
      ttl = appConfig.cacheTtl,
      timestampSupport = new CurrentTimestampSupport,
      sessionIdKey = SessionKeys.sessionId
    ) {

  private def getSessionId(implicit request: Request[_]): String = request.session.get(SessionKeys.sessionId) match {
    case Some(id) => id
    case _        => throw SessionCacheException(s"No Session data is cached for the sessionId ")
  }

  def get()(implicit request: Request[_]): Future[Option[FraudReportDetails]] =
    getFromSession[FraudReportDetails](DataKey(getSessionId))

  def store(sessionData: FraudReportDetails)(implicit request: Request[_]): Future[Option[FraudReportDetails]] =
    putSession[FraudReportDetails](DataKey(getSessionId), sessionData) flatMap { _ => get() }

  def createCacheIfNotPresent()(implicit request: Request[_]): Future[Boolean] =
    get() flatMap {
      case Some(_) => Future.successful(true)
      case _       => store(FraudReportDetails(None)) map (_.isDefined)
    }

  def saveFraudReportDetails(
    fraudReportDetails: FraudReportDetails
  )(implicit request: Request[_]): Future[FraudReportDetails] =
    store(fraudReportDetails) map (
      result => result.getOrElse(throw SessionCacheException(s"No match session id for signed in user with session"))
    )

  def getFraudReportDetails(implicit request: Request[_]): Future[FraudReportDetails] = get map (_.get)

}

case class SessionCacheException(errorMessage: String) extends NoStackTrace
