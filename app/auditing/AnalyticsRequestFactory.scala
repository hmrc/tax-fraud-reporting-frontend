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

import javax.inject.{Inject, Singleton}

@Singleton
class AnalyticsRequestFactory @Inject() (config: FrontendAppConfig) extends Logging {

  private def dimensions(value: String) = Seq(
    //different dimensions can be added for Analytics reports
    DimensionValue(1, value)
  )

  def internalServerError(clientId: Option[String], event: InternalServerErrorEvent): AnalyticsRequest =
    AnalyticsRequest(clientId, Seq(Event("rkss_error", event.reason, s"rkss_error", Nil)))

  def activityType(clientId: Option[String], event: ActivityTypeEvent): AnalyticsRequest =
    AnalyticsRequest(
      clientId,
      Seq(Event(s"activity_type", event.activity, s"activity_type", dimensions(event.activity)))
    )

  def radioButtonEvent(clientId: Option[String], event: RadioButtonEvent): AnalyticsRequest =
    AnalyticsRequest(
      clientId,
      Seq(Event("rkss_radiobutton", event.action, s"rkss_radiobutton", dimensions(event.page)))
    )

  def checkBoxEvent(clientId: Option[String], event: RadioButtonEvent): AnalyticsRequest =
    AnalyticsRequest(
      clientId,
      Seq(Event("rkss_checkbox", event.action, s"rkss_checkbox", dimensions(event.page)))
    )

  def approximateValue(clientId: Option[String], event: ApproximateValueEvent): AnalyticsRequest =
    AnalyticsRequest(
      clientId,
      Seq(Event(s"approximate_value", event.action, s"approximate_value", dimensions(event.action)))
    )

  def pageLoadEvent(clientId: Option[String], event: PageLoadEvent): AnalyticsRequest =
    AnalyticsRequest(clientId, Seq(Event(s"rkss_pageurl", event.path, s"rkss_pageurl", dimensions(event.path))))

  def activityValue(clientId: Option[String], event: ActivityValueEvent): AnalyticsRequest =
    AnalyticsRequest(
      clientId,
      Seq(Event(s" rkss_activityvalue", event.value, s" rkss_activityvalue", dimensions(event.value)))
    )

}
