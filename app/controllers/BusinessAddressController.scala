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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.AddressFormProvider
import models.{Index, Mode}
import navigation.Navigator
import pages.BusinessAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.{BusinessPart, IndividualPart}
import views.html.AddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val journeyPart = if (request.userAnswers.isBusinessJourney) BusinessPart else IndividualPart(true)
      val preparedForm = request.userAnswers get BusinessAddressPage(index) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, index, mode, journeyPart))
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val journeyPart = if (request.userAnswers.isBusinessJourney) BusinessPart else IndividualPart(true)
      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, index, mode, journeyPart))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(BusinessAddressPage(index), value))
            _              <- sessionRepository set updatedAnswers
          } yield Redirect(navigator.nextPage(BusinessAddressPage(index), mode, updatedAnswers))
      )
  }

}
