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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages.{PreviousIndividualInformation}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers._
import viewmodels.govuk.summarylist._
import views.html.IndividualCheckYourAnswersView

class IndividualCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: IndividualCheckYourAnswersView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val answers = request.userAnswers

      val individualDetails = {
        val individualInformationChecks = answers.get(PreviousIndividualInformation).getOrElse(List.empty).length
        val y = (0 until individualInformationChecks).flatMap { index =>
          Seq(
            IndividualNameSummary.row(answers, index),
            IndividualAgeSummary.row(answers, index),
            IndividualDateOfBirthSummary.row(answers, index),
            IndividualAddressSummary.row(answers, index),
            IndividualContactDetailsSummary.row(index, answers),
            IndividualNationalInsuranceNumberSummary.row(answers, index),
            IndividualConnectionSummary.row(answers, index),
            IndividualBusinessDetailsSummary.row(answers, index)
          ).flatten
        }
        SummaryListViewModel(Seq(SelectConnectionBusinessSummary.row(answers, index = 0)).flatten ++ y)
      }

      Ok(view(individualDetails))
  }

}