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

import play.api.Logging
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

trait EventHandler {
  def handleEvent(event: MonitoringEvent)(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Unit
}

trait EventDispatcher extends Logging {

  protected def eventHandlers: Seq[EventHandler]

  def applyEventHandler(handler: EventHandler, event: MonitoringEvent)(implicit
    request: Request[_],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Unit =
    try handler.handleEvent(event)
    catch {
      case ex: Exception => logger.warn(s"Exception when invoking event handler:", ex)
    }

  def dispatchEvent(
    event: MonitoringEvent
  )(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Unit =
    eventHandlers foreach { applyEventHandler(_, event) }

}

@Singleton
class AuditAndAnalyticsEventDispatcher @Inject() (
  auditEventHandler: AuditEventHandler,
  analyticsEventHandler: AnalyticsEventHandler
) extends EventDispatcher {
  val eventHandlers = Seq(auditEventHandler, analyticsEventHandler)
}
