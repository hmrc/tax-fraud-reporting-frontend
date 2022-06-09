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

sealed trait MonitoringEvent

// Internal Server Error
case class InternalServerErrorEvent(reason: String)       extends MonitoringEvent
case class ActivityTypeEvent(activity: String)            extends MonitoringEvent
case class PageLoadEvent(path: String)                    extends MonitoringEvent
case class RadioButtonEvent(action: String, page: String) extends MonitoringEvent
case class CheckBoxEvent(action: String, page: String)    extends MonitoringEvent
case class ApproximateValueEvent(action: String)          extends MonitoringEvent
case class ActivityValueEvent(value: String)              extends MonitoringEvent
