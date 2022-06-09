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

import auditing.{ApproximateValueEvent, AuditAndAnalyticsEventDispatcher, PageLoadEvent}
import controllers.actions._
import forms.ApproximateValueFormProvider
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.{ApproximateValuePage, WhenActivityHappenPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ApproximateValueView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApproximateValueController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ApproximateValueFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ApproximateValueView,
  val eventDispatcher: AuditAndAnalyticsEventDispatcher
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      eventDispatcher.dispatchEvent(PageLoadEvent(request.path))
      val whatActivityHappened = request.userAnswers.get(WhenActivityHappenPage).getOrElse(
        throw new Exception(s"activity duration is not saved in cache")
      )
      eventDispatcher.dispatchEvent(PageLoadEvent(request.path))
      val preparedForm = request.userAnswers.get(ApproximateValuePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, whatActivityHappened))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val whatActivityHappened = request.userAnswers.get(WhenActivityHappenPage).getOrElse(
        throw new Exception(s"activity duration is not saved in cache")
      )
      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, whatActivityHappened))),
        value =>
          eventDispatcher.dispatchEvent(ApproximateValueEvent(value.toString))
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ApproximateValuePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ApproximateValuePage, mode, updatedAnswers))
        }
      )
  }

}
