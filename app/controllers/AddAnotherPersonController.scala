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
import forms.AddAnotherPersonFormProvider
import models.Mode
import navigation.Navigator
import pages.{AddAnotherPersonPage, NominalsQuery}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AddAnotherPersonView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherPersonController @Inject() (
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AddAnotherPersonFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherPersonView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val numberOfNominals = request.userAnswers.get(NominalsQuery).getOrElse(List.empty).length
      Ok(view(form, numberOfNominals, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          val numberOfNominals = request.userAnswers.get(NominalsQuery).getOrElse(List.empty).length
          Future.successful(BadRequest(view(formWithErrors, numberOfNominals, mode)))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAnotherPersonPage, value))
          } yield Redirect(navigator.nextPage(AddAnotherPersonPage, mode, updatedAnswers))
      )
  }
}
