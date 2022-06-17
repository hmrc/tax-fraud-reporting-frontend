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
import controllers.helper.EventHelper
import forms.AddressFormProvider
import models.requests.DataRequest
import models.{AddressSansCountry, Index, Mode}
import navigation.Navigator
import pages.{BusinessAddressPage, BusinessSelectCountryPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
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
  val controllerComponents: MessagesControllerComponents,
  view: AddressView,
  val eventHelper: EventHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      eventHelper.pageLoadEvent(request.path)
      withCountry(
        index,
        mode,
        (countryCode, form) =>
          Future successful {
            val journeyPart = if (request.userAnswers.isBusinessJourney) BusinessPart else IndividualPart(true)
            val preparedForm = request.userAnswers get BusinessAddressPage(index) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, countryCode, index, mode, journeyPart))
          }
      )
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val journeyPart = if (request.userAnswers.isBusinessJourney) BusinessPart else IndividualPart(true)
      withCountry(
        index,
        mode,
        (countryCode, form) =>
          form.bindFromRequest().fold(
            formWithErrors => {
              eventHelper.formErrorEvent(
                request.path,
                messagesApi.preferred(List(Lang("en")))(formWithErrors.errors.head.message)
              )
              Future.successful(BadRequest(view(formWithErrors, countryCode, index, mode, journeyPart)))
            },
            address =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(BusinessAddressPage(index), address))
                _              <- sessionRepository set updatedAnswers
              } yield Redirect(routes.ConfirmAddressController.onPageLoad(index, true, mode))
          )
      )
  }

  private def withCountry[R](index: Index, mode: Mode, f: (String, Form[AddressSansCountry]) => Future[Result])(implicit
    request: DataRequest[AnyContent]
  ): Future[Result] =
    request.userAnswers get BusinessSelectCountryPage(index) match {
      case None => Future successful Redirect(routes.BusinessSelectCountryController.onPageLoad(index, mode))
      case Some(countryCode) =>
        f(countryCode, AddressFormProvider.get(countryCode == "gb"))
    }

}
