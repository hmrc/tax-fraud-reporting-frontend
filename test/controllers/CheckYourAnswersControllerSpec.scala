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

import base.SpecBase
import models._
import pages._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.checkAnswers._
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val answers = emptyUserAnswers

      val isBusinessJourney = answers.isBusinessJourney

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        val activityDetails = SummaryListViewModel(
          Seq(
            ActivityTypeSummary.row(answers)(messages(application)),
            ApproximateValueSummary.row(answers)(messages(application)),
            ActivityTimePeriodSummary.row(answers)(messages(application)),
            HowManyPeopleKnowSummary.row(answers)(messages(application)),
            DescriptionActivitySummary.row(answers)(messages(application)),
            ActivitySourceOfInformationSummary.row(answers)(messages(application))
          ).flatten
        )

        val yourDetails = SummaryListViewModel(
          Seq(
            ProvideContactDetailsSummary.row(answers)(messages(application)).toList ++
              YourContactDetailsSummary.rows(answers)(messages(application))
          ).flatten
        )

        val supportingDocuments =
          SummaryListViewModel(Seq(SupportingDocumentSummary.row(answers)(messages(application))).flatten)

        val businessDetails = SummaryListViewModel(
          Seq(
            SelectConnectionBusinessSummary.row(answers, 0)(messages(application)),
            BusinessNameSummary.row(answers, 0)(messages(application)),
            TypeBusinessSummary.row(answers, 0)(messages(application)),
            BusinessAddressSummary.row(answers, 0)(messages(application)),
            BusinessContactDetailsSummary.row(answers, 0)(messages(application)),
            ReferenceNumbersSummary.row(answers, 0)(messages(application)),
            SelectConnectionBusinessSummary.row(answers, 0)(messages(application))
          ).flatten
        )

        val individualDetails = SummaryListViewModel(
          Seq(
            IndividualNameSummary.row(answers, 0)(messages(application)),
            IndividualAgeSummary.row(answers, 0)(messages(application)),
            IndividualDateOfBirthSummary.row(answers, 0)(messages(application)),
            IndividualAddressSummary.row(answers, 0)(messages(application)),
            IndividualContactDetailsSummary.row(0, answers)(messages(application)),
            IndividualNationalInsuranceNumberSummary.row(answers, 0)(messages(application)),
            IndividualConnectionSummary.row(answers, 0)(messages(application)),
            IndividualBusinessDetailsSummary.row(answers, 0)(messages(application))
          ).flatten
        )

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          isBusinessJourney,
          activityDetails,
          yourDetails,
          supportingDocuments,
          businessDetails,
          individualDetails
        )(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
