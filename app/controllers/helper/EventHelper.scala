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

package controllers.helper

import auditing._
import play.api.Configuration
import play.api.mvc.Request
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class EventHelper @Inject() (
  httpClient: HttpClient,
  configuration: Configuration,
  val eventDispatcher: AuditAndAnalyticsEventDispatcher
)(implicit ec: ExecutionContext) {

  def pageLoadEvent(path: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(PageLoadEvent(path))

  def formErrorEvent(path: String, error: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(FormErrorEvent(path, error))

  def radioButtonEvent(path: String, value: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(RadioButtonEvent(path, value))

  def activityTypeEvent(value: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(ActivityTypeEvent(value))

  def approximateValueEvent(value: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(ApproximateValueEvent(value))

  def checkBoxEvent(path: String, value: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(CheckBoxEvent(path, value))

  def internalServerErrorEvent(error: String)(implicit request: Request[_], hc: HeaderCarrier): Unit =
    eventDispatcher.dispatchEvent(InternalServerErrorEvent(error))

}
