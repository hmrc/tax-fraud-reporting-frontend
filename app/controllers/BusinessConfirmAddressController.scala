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
import forms.BusinessConfirmAddressFormProvider
import models.{Index, Mode, NormalMode}
import navigation.Navigator
import pages.BusinessConfirmAddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.BusinessConfirmAddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessConfirmAddressController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: SessionRepository,
                                         navigator: Navigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: BusinessConfirmAddressFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: BusinessConfirmAddressView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
      val preparedForm = request.userAnswers.get(BusinessConfirmAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      request.userAnswers getAddress(index, forBusiness = true) match {
        case Some(address) => Ok(view(preparedForm, index, mode, address))
        case None => Redirect(routes.BusinessAddressController.onPageLoad(index, NormalMode))
      }
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val isBusinessDetails = request.userAnswers.isBusinessDetails(index)
      form.bindFromRequest().fold(
        formWithErrors =>
      request.userAnswers getAddress(index, forBusiness = true) match {
        case Some(address) =>
          Future.successful(BadRequest(view(formWithErrors, index, mode, address)))
        case None => Future.successful(Redirect(routes.BusinessAddressController.onPageLoad(index, NormalMode)))
      },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(BusinessConfirmAddressPage(index), value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(BusinessConfirmAddressPage(index), mode, updatedAnswers))
      )
  }
}
