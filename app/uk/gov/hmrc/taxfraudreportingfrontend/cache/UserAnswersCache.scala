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
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.models._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersCache @Inject() (sessionCache: SessionCache)(implicit ec: ExecutionContext) {

  def saveFraudReportDetails(
    insertNewDetails: FraudReportDetails => FraudReportDetails
  )(implicit request: Request[_]): Future[FraudReportDetails] = sessionCache.getFraudReportDetails flatMap { details =>
    sessionCache.saveFraudReportDetails(insertNewDetails(details))
  }

  def cacheActivityType(activityType: ActivityType)(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(activityType = Some(activityType)))

  def getActivityType()(implicit request: Request[_]): Future[Option[ActivityType]] =
    sessionCache.getFraudReportDetails map (_.activityType)

  def cacheIndividualInformationCheck(
    individualInformationCheck: Set[IndividualInformationCheck]
  )(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(individualInformationCheck = individualInformationCheck))

  def getIndividualInformationCheck()(implicit request: Request[_]): Future[Set[IndividualInformationCheck]] =
    sessionCache.getFraudReportDetails map (_.individualInformationCheck)

  def cacheIndividualName(
    individualName: Some[IndividualName]
  )(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(individualName = individualName))

  def getIndividualName()(implicit request: Request[_]): Future[Option[IndividualName]] =
    sessionCache.getFraudReportDetails map (_.individualName)

  def cacheIndividualContact(
    individualContact: Some[IndividualContact]
  )(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(individualContact = individualContact))

  def getIndividualContact()(implicit request: Request[_]): Future[Option[IndividualContact]] =
    sessionCache.getFraudReportDetails map (_.individualContact)

  def cacheNino(individualNino: Some[IndividualNino])(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(individualNino = individualNino))

  def getNino()(implicit request: Request[_]): Future[Option[IndividualNino]] =
    sessionCache.getFraudReportDetails map (_.individualNino)

  def cacheConnection(connectionType: Some[ConnectionType])(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(connectionType = connectionType))

  def getConnection()(implicit request: Request[_]): Future[Option[ConnectionType]] =
    sessionCache.getFraudReportDetails map (_.connectionType)

  def cacheBusinessDetails(
    businessDetails: Some[BusinessDetails]
  )(implicit request: Request[_]): Future[FraudReportDetails] =
    saveFraudReportDetails(sd => sd.copy(businessDetails = businessDetails))

  def getBusinessDetails()(implicit request: Request[_]): Future[Option[BusinessDetails]] =
    sessionCache.getFraudReportDetails map (_.businessDetails)

}
