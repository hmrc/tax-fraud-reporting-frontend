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

  def internalServerError(event: InternalServerErrorEvent): Seq[Event] =
    Seq(Event("rkss_error", event.reason, s"rkss_error", Nil))

  def activityType(event: ActivityTypeEvent): Seq[Event] =
    Seq(Event(s"activity_type", event.activity, s"activity_type", dimensions(event.activity)))

  def radioButtonEvent(event: RadioButtonEvent): Seq[Event] =
    Seq(Event("rkss_radiobutton", event.path, s"rkss_radiobutton", dimensions(event.value)))

  def checkBoxEvent(event: CheckBoxEvent): Seq[Event] =
    Seq(Event("rkss_checkbox", event.path, s"rkss_checkbox", dimensions(event.value)))

  def approximateValue(event: ApproximateValueEvent): Seq[Event] =
    Seq(Event(s"approximate_value", event.action, s"approximate_value", dimensions(event.action)))

  def pageLoadEvent(event: PageLoadEvent): Seq[Event] =
    Seq(Event(s"rkss_pageurl", event.path, s"rkss_pageurl", dimensions(event.path)))

  def activityValue(event: ActivityValueEvent): Seq[Event] =
    Seq(Event(s" rkss_activityvalue", event.value, s" rkss_activityvalue", dimensions(event.value)))

  def formError(event: FormErrorEvent): Seq[Event] =
    Seq(Event(event.path, event.error, s"rkss_error", dimensions(event.error)))

}
