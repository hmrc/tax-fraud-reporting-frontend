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
import forms.ConfirmAddressFormProvider
import models.{BusinessInformationCheck, CheckMode, Index, IndividualInformation, Mode, NormalMode}
import navigation.Navigator
import pages.{BusinessConfirmAddressPage, ConfirmAddressPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.{BusinessPart, IndividualPart}
import views.html.ConfirmAddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmAddressView,
  formProvider: ConfirmAddressFormProvider,
  navigator: Navigator,
  val eventHelper: EventHelper
) (implicit ec: ExecutionContext)  extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(index: Index, forBusiness: Boolean, mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireData) {
      implicit request =>
        eventHelper.pageLoadEvent(request.path)
        val isBusinessJourney = request.userAnswers.isBusinessJourney
        val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
        val journeyPart       = if (request.userAnswers.isBusinessJourney) BusinessPart else IndividualPart(true)
        val preparedForm = request.userAnswers.get(BusinessConfirmAddressPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }
        val nextPage =
          if (isBusinessJourney)
            navigator.businessInformationRoutes(request.userAnswers, index, BusinessInformationCheck.Address, mode)
          else
            mode match {
              case NormalMode =>
                if (isBusinessDetails)
                  navigator.businessInformationRoutes(
                    request.userAnswers,
                    index,
                    BusinessInformationCheck.Address,
                    mode
                  )
                else
                  navigator.individualInformationRoutes(request.userAnswers, index, IndividualInformation.Address, mode)
              case CheckMode =>
                if (isBusinessDetails)
                  navigator.businessInformationRoutes(
                    request.userAnswers,
                    index,
                    BusinessInformationCheck.Address,
                    mode
                  )
                else
                  routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
            }
        request.userAnswers getAddress (index, forBusiness) match {
          case Some(address) => Ok(view(preparedForm, index, mode, address, isBusinessJourney, journeyPart))
          case None          => Redirect(routes.IndividualAddressController.onPageLoad(index, NormalMode))
        }
    }

  def onSubmit(index: Index, forBusiness: Boolean, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
      val isBusinessJourney = request.userAnswers.isBusinessJourney
      val journeyPart       = if (request.userAnswers.isBusinessJourney) BusinessPart else IndividualPart(true)
      val nextPage =
        if (isBusinessJourney)
          navigator.businessInformationRoutes(request.userAnswers, index, BusinessInformationCheck.Address, mode)
        else
          mode match {
            case NormalMode =>
              if (isBusinessDetails)
                navigator.businessInformationRoutes(
                  request.userAnswers,
                  index,
                  BusinessInformationCheck.Address,
                  mode
                )
              else
                navigator.individualInformationRoutes(request.userAnswers, index, IndividualInformation.Address, mode)
            case CheckMode =>
              if (isBusinessDetails)
                navigator.businessInformationRoutes(
                  request.userAnswers,
                  index,
                  BusinessInformationCheck.Address,
                  mode
                )
              else
                routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
          }
      form.bindFromRequest().fold(
        formWithErrors =>
          request.userAnswers getAddress(index, forBusiness) match {
            case Some(address) =>
              Future.successful(BadRequest(view(formWithErrors, index, mode, address, isBusinessJourney, journeyPart)))
            case None => Future.successful(Redirect(routes.BusinessAddressController.onPageLoad(index, NormalMode)))
          },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ConfirmAddressPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ConfirmAddressPage(index), mode, updatedAnswers))
      )
  }

}
