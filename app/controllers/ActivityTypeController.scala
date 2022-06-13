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

import auditing.{ActivityTypeEvent, AuditAndAnalyticsEventDispatcher, PageLoadEvent}
import controllers.actions._
import forms.ActivityTypeFormProvider

import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.ActivityTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ActivityTypeView

import scala.concurrent.{ExecutionContext, Future}

class ActivityTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: ActivityTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ActivityTypeView,
  val eventDispatcher: AuditAndAnalyticsEventDispatcher
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) {
    implicit request =>
      val userAnswers = request.userAnswers getOrElse UserAnswers(request.userId)
      eventDispatcher.dispatchEvent(PageLoadEvent(request.path))
      val preparedForm = userAnswers get ActivityTypePage match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          val userAnswers = request.userAnswers getOrElse UserAnswers(request.userId)
          eventDispatcher.dispatchEvent(ActivityTypeEvent(value))
          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(ActivityTypePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ActivityTypePage, mode, updatedAnswers))
        }
      )
  }

}
