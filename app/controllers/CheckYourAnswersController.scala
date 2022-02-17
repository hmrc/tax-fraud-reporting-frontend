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
import pages.{PreviousBusinessInformation, PreviousIndividualInformation}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers._
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val answers           = request.userAnswers
      val isBusinessJourney = request.userAnswers.isBusinessJourney

      val activityDetails = SummaryListViewModel(
        Seq(
          ActivityTypeSummary.row(answers),
          ApproximateValueSummary.row(answers),
          ActivityTimePeriodSummary.row(answers),
          HowManyPeopleKnowSummary.row(answers),
          DescriptionActivitySummary.row(answers),
          ActivitySourceOfInformationSummary.row(answers)
        ).flatten
      )

      val yourDetails = SummaryListViewModel(
        ProvideContactDetailsSummary.row(answers).toList ++
          YourContactDetailsSummary.rows(answers)
      )

      val supportDoc = SummaryListViewModel (
        Seq(
          SupportingDocumentSummary.row(answers),
          DocumentationDescriptionSummary.row(answers)
        ).flatten
      )

      val supportingDocuments = SummaryListViewModel(Seq(SupportingDocumentSummary.row(answers)).flatten)

      val businessDetails = {
        val businessInformationChecks = answers.get(PreviousBusinessInformation).getOrElse(List.empty).length
        val x = (0 until businessInformationChecks).flatMap { index =>
          Seq(
            BusinessNameSummary.row(answers, index),
            TypeBusinessSummary.row(answers, index),
            BusinessAddressSummary.row(answers, index),
            BusinessContactDetailsSummary.row(answers, index),
            ReferenceNumbersSummary.row(answers, index),
            SelectConnectionBusinessSummary.row(answers, index)
          ).flatten
        }
        SummaryListViewModel(x)
      }

      val individualDetails = {
        val individualInformationChecks = answers.get(PreviousIndividualInformation).getOrElse(List.empty).length
        val v = (0 until individualInformationChecks).flatMap { index =>
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
        SummaryListViewModel(v)
      }

      Ok(view(isBusinessJourney, activityDetails, yourDetails, supportingDocuments, businessDetails, individualDetails, supportDoc))
  }

}
