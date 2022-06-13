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
import scala.collection.Seq
import scala.concurrent.ExecutionContext

@Singleton
class AnalyticsEventHandler @Inject() (connector: AnalyticsConnector, factory: AnalyticsRequestFactory)
    extends EventHandler {

  override def handleEvent(
    event: MonitoringEvent
  )(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Unit =
    event match {
      case e: ActivityTypeEvent        => sendEvent(factory.activityType(e))
      case e: RadioButtonEvent         => sendEvent(factory.radioButtonEvent(e))
      case e: CheckBoxEvent            => sendEvent(factory.checkBoxEvent(e))
      case e: ApproximateValueEvent    => sendEvent(factory.approximateValue(e))
      case e: PageLoadEvent            => sendEvent(factory.pageLoadEvent(e))
      case e: ActivityValueEvent       => sendEvent(factory.activityValue(e))
      case e: InternalServerErrorEvent => sendEvent(factory.internalServerError(e))
    }

  private def clientId(implicit request: Request[_]): Option[String] = request.cookies.get("_ga").map(_.value)

  private def sendEvent(
    events: Seq[Event]
  )(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Unit =
    connector.sendEvent(AnalyticsRequest(clientId, events))

}
