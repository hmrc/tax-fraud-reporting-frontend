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
import pages.NominalsQuery
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

  // scalastyle:off
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

      val supportDoc = SummaryListViewModel(
        Seq(SupportingDocumentSummary.row(answers), DocumentationDescriptionSummary.row(answers)).flatten
      )

      val supportingDocuments = SummaryListViewModel(Seq(SupportingDocumentSummary.row(answers)).flatten)

      val businessDetails = {
        SummaryListViewModel(Seq(
          BusinessNameSummary.row(answers, 0),
          TypeBusinessSummary.row(answers, 0),
          BusinessAddressSummary.row(answers, 0),
          BusinessContactDetailsSummary.row(answers, 0),
          ReferenceNumbersSummary.row(answers, 0),
          SelectConnectionBusinessSummary.row(answers, 0)
        ).flatten)
      }

      val numberOfNominals = answers.get(NominalsQuery).getOrElse(List.empty).length

      Ok(
        view(
          isBusinessJourney,
          activityDetails,
          yourDetails,
          supportingDocuments,
          businessDetails,
          numberOfNominals,
          supportDoc
        )
      )
  }

}
