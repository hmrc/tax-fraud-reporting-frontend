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
import forms.IndividualConfirmRemoveFormProvider

import javax.inject.Inject
import models.{Index, Mode, UserAnswers}
import navigation.Navigator
import pages.{IndividualConfirmRemovePage, NominalQuery}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndividualConfirmRemoveView

import scala.concurrent.{ExecutionContext, Future}

class IndividualConfirmRemoveController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: SessionRepository,
                                         navigator: Navigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: IndividualConfirmRemoveFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: IndividualConfirmRemoveView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def removeIndividual(answers: UserAnswers, index: Index): Future[UserAnswers] = for {
    updatedAnswers <- Future.fromTry(answers.remove(NominalQuery(index)))
    _              <- sessionRepository.set(updatedAnswers)
  } yield updatedAnswers

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Ok(view(form, index, mode))
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, index, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualConfirmRemovePage(index), value))
            updatedAnswers <- if (value) removeIndividual(request.userAnswers, index) else Future.successful(updatedAnswers)
          } yield Redirect(navigator.nextPage(IndividualConfirmRemovePage(index), mode, updatedAnswers))
      )
  }
}
