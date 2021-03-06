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

package models.backend

import play.api.libs.json.{Json, OFormat}

final case class FraudReportBody(
  activityType: String,
  nominals: Seq[Nominal],
  valueFraud: Option[BigDecimal],
  durationFraud: Option[String],
  howManyKnow: Option[String],
  additionalDetails: Option[String],
  reporter: Option[Reporter],
  hasEvidence: Boolean,
  informationSource: String,
  evidenceDetails: Option[String]
)

object FraudReportBody {
  implicit val format: OFormat[FraudReportBody] = Json.format
}
