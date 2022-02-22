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
import models.{Index, IndividualBusinessDetails, Mode}
import navigation.Navigator
import pages.{IndividualBusinessDetailsPage, IndividualCheckYourAnswersPage}
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
  view: IndividualCheckYourAnswersView,
  navigator: Navigator
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val answers = request.userAnswers

      val individualDetails =
        SummaryListViewModel(
          Seq(
            IndividualNameSummary.row(answers, index.position, mode),
            IndividualDateFormatSummary.row(answers, index.position, mode),
            IndividualAgeSummary.row(answers, index.position, mode),
            IndividualDateOfBirthSummary.row(answers, index.position, mode),
            IndividualAddressSummary.row(answers, index.position, mode),
            IndividualContactDetailsSummary.row(answers, index.position, mode),
            IndividualNationalInsuranceNumberSummary.row(answers, index.position, mode),
            IndividualConnectionSummary.row(answers, index.position, mode),
            IndividualBusinessDetailsSummary.row(answers, index.position, mode)
          ).flatten
        )

      val individualBusinessDetails = {
        val rows =
          if (answers.get(IndividualBusinessDetailsPage(index)).contains(IndividualBusinessDetails.Yes))
            Seq(
              BusinessNameSummary.row(answers, index.position, mode),
              TypeBusinessSummary.row(answers, index.position, mode),
              BusinessAddressSummary.row(answers, index.position, mode),
              BusinessContactDetailsSummary.row(answers, index.position, mode),
              ReferenceNumbersSummary.row(answers, index.position, mode),
              SelectConnectionBusinessSummary.row(answers, index.position, mode)
            ).flatten
          else
            List.empty
        SummaryListViewModel(rows)
      }

      Ok(
        view(
          individualDetails,
          individualBusinessDetails,
          navigator.nextPage(IndividualCheckYourAnswersPage(index), mode, answers)
        )
      )
  }

}
