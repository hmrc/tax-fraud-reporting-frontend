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
import forms.IndividualSelectCountryFormProvider

import javax.inject.Inject
import models.{Index, Mode}
import navigation.Navigator
import pages.IndividualSelectCountryPage
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.Individual
import views.html.IndividualSelectCountryView

import scala.concurrent.{ExecutionContext, Future}

class IndividualSelectCountryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IndividualSelectCountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IndividualSelectCountryView,
  val eventHelper: EventHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      eventHelper.pageLoadEvent(request.path)
      val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
      val preparedForm = request.userAnswers.get(IndividualSelectCountryPage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, index, mode, Individual(false), isBusinessDetails))
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
      form.bindFromRequest().fold(
        formWithErrors => {
          eventHelper.formErrorEvent(
            request.path,
            messagesApi.preferred(List(Lang("en")))(formWithErrors.errors.head.message)
          )
          Future.successful(BadRequest(view(formWithErrors, index, mode, Individual(false), isBusinessDetails)))
        },
        country =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualSelectCountryPage(index), country))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualSelectCountryPage(index), mode, updatedAnswers))
      )
  }

}
