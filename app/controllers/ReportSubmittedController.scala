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

package controllers

import auditing.{AuditAndAnalyticsEventDispatcher, PageLoadEvent}
import config.FrontendAppConfig
import controllers.actions._

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ReportSubmittedView

import scala.concurrent.ExecutionContext

class ReportSubmittedController @Inject() (
  override val messagesApi: MessagesApi,
  appConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ReportSubmittedView,
  sessionRepository: SessionRepository,
  val eventDispatcher: AuditAndAnalyticsEventDispatcher
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      eventDispatcher.dispatchEvent(PageLoadEvent(request.path))
      val isProvideContact = request.userAnswers.isProvideContact
      sessionRepository
        .clear(request.userId)
        .map {
          _ =>
            Ok(view(appConfig, isProvideContact))
        }
  }

}
