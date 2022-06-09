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

import auditing.{AuditAndAnalyticsEventDispatcher, PageLoadEvent, RadioButtonEvent}
import controllers.actions._
import forms.ProvideContactDetailsFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.ProvideContactDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ProvideContactDetailsView

import scala.concurrent.{ExecutionContext, Future}

class ProvideContactDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ProvideContactDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ProvideContactDetailsView,
  val eventDispatcher: AuditAndAnalyticsEventDispatcher
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      eventDispatcher.dispatchEvent(PageLoadEvent(request.path))
      val preparedForm = request.userAnswers.get(ProvideContactDetailsPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          eventDispatcher.dispatchEvent(RadioButtonEvent(request.path, value.toString))
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ProvideContactDetailsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ProvideContactDetailsPage, mode, updatedAnswers))
        }
      )
  }

}
