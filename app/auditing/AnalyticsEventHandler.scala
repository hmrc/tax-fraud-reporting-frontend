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

import connector.AnalyticsConnector
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AnalyticsEventHandler @Inject() (connector: AnalyticsConnector, factory: AnalyticsRequestFactory)
    extends EventHandler {

  override def handleEvent(
    event: MonitoringEvent
  )(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Unit =
    event match {
      case e: ActivityTypeEvent        => sendEvent(e, factory.activityType)
      case e: RadioButtonEvent         => sendEvent(e, factory.radioButtonEvent)
      case e: CheckBoxEvent         => sendEvent(e, factory.checkBoxEvent)
      case e: ApproximateValueEvent    => sendEvent(e, factory.approximateValue)
      case e: PageLoadEvent            => sendEvent(e, factory.pageLoadEvent)
      case e: ActivityValueEvent       => sendEvent(e, factory.activityValue)
      case e: InternalServerErrorEvent => sendEvent(e, factory.internalServerError)
    }

  private def clientId(implicit request: Request[_]): Option[String] = request.cookies.get("_ga").map(_.value)

  private def sendEvent[E <: MonitoringEvent](event: E, reqCreator: (Option[String], E) => AnalyticsRequest)(implicit
    request: Request[_],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Unit = {
    val analyticsRequest = reqCreator(clientId, event)
    if (analyticsRequest.events.nonEmpty)
      connector.sendEvent(analyticsRequest)
  }

}
