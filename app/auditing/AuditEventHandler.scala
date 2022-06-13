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
import play.api.i18n.{LangImplicits, MessagesApi}
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.AuditExtensions.AuditHeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.DataEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

object AuditType {
  val activityType = "activityType"
}

@Singleton
class AuditEventHandler @Inject() (connector: AuditConnector, appConfig: FrontendAppConfig)(implicit
  messagesApi: MessagesApi
) extends EventHandler {

  private lazy val factory = new AuditEventFactory(appConfig)

  override def handleEvent(
    event: MonitoringEvent
  )(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Unit =
    event match {
      case e: ActivityTypeEvent => sendEvent(e, factory.activityTypeSelectEvent)
      case _                    => ()
    }

  private def sendEvent[E <: MonitoringEvent](event: E, create: E => DataEvent)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ) = {
    val dataEvent = create(event)
    connector.sendEvent(dataEvent)
  }

}

private[auditing] class AuditEventFactory(config: FrontendAppConfig)(implicit override val messagesApi: MessagesApi)
    extends LangImplicits {

  private def generateDataEvent(
    auditType: String,
    event: MonitoringEvent,
    additionalDetail: Map[String, String]
  )(implicit request: Request[_], hc: HeaderCarrier) = {
    val carrier: AuditHeaderCarrier =
      AuditExtensions.auditHeaderCarrier(HeaderCarrier(requestId = hc.requestId, sessionId = hc.sessionId))
    val commonDetail = carrier.toAuditDetails(
      "application" -> "RISKana - application"
      //required Headers can be added - as per the request
    )

    DataEvent(
      auditSource = config.appName,
      auditType = auditType,
      tags = carrier.toAuditTags(auditType, request.path),
      detail = commonDetail ++ additionalDetail
    )
  }

  def activityTypeSelectEvent(event: ActivityTypeEvent)(implicit request: Request[_], hc: HeaderCarrier): DataEvent =
    generateDataEvent(
      AuditType.activityType,
      event,
      Map("failureUrl" -> event.activity, "failureReason" -> "User not matched with CID")
    )

}
