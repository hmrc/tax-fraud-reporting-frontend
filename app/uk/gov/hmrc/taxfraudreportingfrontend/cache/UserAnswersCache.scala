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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.models.{ActivityType, IndividualInformationCheck, IndividualName}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersCache @Inject() (sessionCache: SessionCache)(implicit ec: ExecutionContext) {

  def saveFraudReportDetails(
    insertNewDetails: FraudReportDetails => FraudReportDetails
  )(implicit hc: HeaderCarrier): Future[FraudReportDetails] = sessionCache.getFraudReportDetails flatMap { details =>
    sessionCache.saveFraudReportDetails(insertNewDetails(details))
  }

  def cacheActivityType(activityType: ActivityType)(implicit hc: HeaderCarrier): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(activityType = Some(activityType)))

  def getActivityType()(implicit hc: HeaderCarrier): Future[Option[ActivityType]] =
    sessionCache.getFraudReportDetails map (_.activityType)

  def cacheIndividualInformationCheck(
    individualInformationCheck: Set[IndividualInformationCheck]
  )(implicit hc: HeaderCarrier): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(individualInformationCheck = individualInformationCheck))

  def getIndividualInformationCheck()(implicit hc: HeaderCarrier): Future[Set[IndividualInformationCheck]] =
    sessionCache.getFraudReportDetails map (_.individualInformationCheck)

  def cacheIndividualName(
    individualName: Some[IndividualName]
  )(implicit hc: HeaderCarrier): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(individualName = individualName))

  def getIndividualName()(implicit hc: HeaderCarrier): Future[Option[IndividualName]] =
    sessionCache.getFraudReportDetails map (_.individualName)

}
