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
import models.{BusinessInformationCheck, CheckMode, Index, IndividualInformation, Mode, NormalMode}
import navigation.Navigator

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.hmrcfrontend.controllers.routes
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.{BusinessPart, IndividualPart}
import views.html.ConfirmAddressView

import scala.concurrent.ExecutionContext

class ConfirmAddressController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmAddressView,
  navigator: Navigator
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Index, forBusiness: Boolean, mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireData) {
      implicit request =>
        val isBusinessJourney = request.userAnswers.isBusinessJourney
        val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
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
                  routes.IndividualCheckYourAnswersController.onPageLoad(index, mode)
            }
        request.userAnswers getAddress (index, forBusiness) match {
          case Some(address) => Ok(view(index, address, isBusinessJourney, journeyPart, nextPage))
          case None          => Redirect(routes.IndividualAddressController.onPageLoad(index, NormalMode))
        }
    }

}
