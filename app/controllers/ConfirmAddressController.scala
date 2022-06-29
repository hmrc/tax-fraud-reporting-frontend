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
import models.{Index, Mode, NormalMode}
import navigation.Navigator
import pages.ConfirmAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.IndividualPart
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
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireData) {
      implicit request =>
        eventHelper.pageLoadEvent(request.path)
        val preparedForm = request.userAnswers.get(ConfirmAddressPage(index)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        request.userAnswers getAddress (index, forBusiness = false) match {
          case Some(address) => Ok(view(preparedForm, index, mode, address, IndividualPart(false)))
          case None          => Redirect(routes.IndividualAddressController.onPageLoad(index, NormalMode))
        }
    }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors =>
          request.userAnswers getAddress (index, forBusiness = false) match {
            case Some(address) =>
              Future.successful(BadRequest(view(formWithErrors, index, mode, address, IndividualPart(false))))
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
