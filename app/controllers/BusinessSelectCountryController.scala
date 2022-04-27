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
import forms.IndividualSelectCountryFormProvider
import models.{Index, Mode}
import navigation.Navigator
import pages.BusinessSelectCountryPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.{Business, Individual}
import views.html.IndividualSelectCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessSelectCountryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IndividualSelectCountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IndividualSelectCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val countryJourney = if (request.userAnswers.isBusinessJourney) Business else Individual(true)
      val preparedForm = request.userAnswers.get(BusinessSelectCountryPage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, index, mode, countryJourney))
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val countryJourney = if (request.userAnswers.isBusinessJourney) Business else Individual(true)
      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, index, mode, countryJourney))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(BusinessSelectCountryPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(BusinessSelectCountryPage(index), mode, updatedAnswers))
      )
  }

}
