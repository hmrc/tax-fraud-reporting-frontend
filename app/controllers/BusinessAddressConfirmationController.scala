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
import models.{Index, Mode}
import navigation.Navigator
import pages.BusinessAddressConfirmationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.AddressLookupService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessAddressConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  navigator: Navigator,
  addressLookupService: AddressLookupService,
  sessionRepository: SessionRepository
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Index, mode: Mode, id: Option[String]): Action[AnyContent] =
    (identify andThen getData andThen requireData).async {
      implicit request =>
        id.map { id =>
          addressLookupService.retrieveAddress(id).flatMap {
            _.map { address =>
              for {
                updatedAnswers <- Future.fromTry(
                  request.userAnswers.set(BusinessAddressConfirmationPage(index), address)
                )
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(BusinessAddressConfirmationPage(index), mode, updatedAnswers))
            }.getOrElse(Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad())))
          }
        }.getOrElse(Future.failed(new Exception()))
    }

}
