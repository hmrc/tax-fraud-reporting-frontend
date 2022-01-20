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

package uk.gov.hmrc.taxfraudreportingfrontend.models.cache

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.taxfraudreportingfrontend.models.{
  ActivityType,
  BusinessDetails,
  BusinessInformationCheck,
  ConnectionType,
  IndividualContact,
  IndividualInformationCheck,
  IndividualName,
  IndividualNino,
  ReportingType
}

case class FraudReportDetails(
  activityType: Option[ActivityType] = None,
  reportingType: Option[ReportingType] = None,
  individualInformationCheck: Set[IndividualInformationCheck] = Set.empty,
  individualName: Option[IndividualName] = None,
  individualContact: Option[IndividualContact] = None,
  individualNino: Option[IndividualNino] = None,
  connectionType: Option[ConnectionType] = None,
  businessDetails: Option[BusinessDetails] = None,
  businessInformationCheck: Set[BusinessInformationCheck] = Set.empty
)

object FraudReportDetails {
  implicit val format: Format[FraudReportDetails] = Json.format[FraudReportDetails]
}
