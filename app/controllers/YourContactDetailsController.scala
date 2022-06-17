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

import controllers.actions._
import controllers.helper.EventHelper
import forms.YourContactDetailsFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.YourContactDetailsPage
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.YourContactDetailsView

import scala.concurrent.{ExecutionContext, Future}

class YourContactDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: YourContactDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: YourContactDetailsView,
  val eventHelper: EventHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
       eventHelper.pageLoadEvent(request.path)
      val preparedForm = request.userAnswers.get(YourContactDetailsPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          eventHelper.formErrorEvent(request.path, messagesApi.preferred(List(Lang("en")))(formWithErrors.errors.head.message))
          Future.successful(BadRequest(view(formWithErrors, mode)))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(YourContactDetailsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(YourContactDetailsPage, mode, updatedAnswers))
      )
  }

}
