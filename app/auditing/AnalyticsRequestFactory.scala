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

package auditing

import config.FrontendAppConfig
import play.api.Logging
import play.api.mvc.Request

import javax.inject.{Inject, Singleton}

@Singleton
class AnalyticsRequestFactory @Inject() (config: FrontendAppConfig) extends Logging {

  private def dimensions(value: String) = Seq(
    //different dimensions can be added for Analytics reports
    DimensionValue(1, value)
  )

  def internalServerError(clientId: Option[String], event: InternalServerErrorEvent): AnalyticsRequest =
    AnalyticsRequest(clientId, Seq(Event("rks_id", s"rks_end", s"internal_technical_issue", Nil)))

  def activityType(clientId: Option[String], event: ActivityTypeEvent): AnalyticsRequest =
    AnalyticsRequest(clientId, Seq(Event("rks_id", s"rks_end", s"activity_type", dimensions(event.activity))))

}
