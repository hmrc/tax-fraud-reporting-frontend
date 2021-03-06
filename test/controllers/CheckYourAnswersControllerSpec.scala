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

import java.time.LocalDate

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val approxVal = 1000

      val answers = emptyUserAnswers
        .set(BusinessNamePage(Index(0)), "name").success.value
        .set(TypeBusinessPage(Index(0)), "businessType").success.value
        .set(
          BusinessAddressPage(Index(0)),
          AddressSansCountry("123 Example Street", None, None, "Townsville", Some("postcode"))
        ).success.value
        .set(
          BusinessContactDetailsPage(Index(0)),
          BusinessContactDetails(Some("landLine"), Some("mobileNumber"), Some("email"))
        ).success.value
        .set(
          ReferenceNumbersPage(Index(0)),
          ReferenceNumbers(Some("vatRegistration"), Some("employeeRefNo"), Some("corporationTax"))
        ).success.value
        .set(SelectConnectionBusinessPage(Index(0)), SelectConnectionBusiness.Accountant).success.value
        .set(
          IndividualNamePage(Index(0)),
          IndividualName(Some("firstname"), Some("middlename"), Some("lastname"), Some("aliases"))
        ).success.value
        .set(IndividualAgePage(Index(0)), 45).success.value
        .set(IndividualDateOfBirthPage(Index(0)), LocalDate.now).success.value
        .set(
          IndividualAddressPage(Index(0)),
          AddressSansCountry("1234 Example Street", None, None, "Townsville", Some("postcode"))
        ).success.value
        .set(
          IndividualContactDetailsPage(Index(0)),
          IndividualContactDetails(Some("landLine"), Some("mobileNumber"), Some("email"))
        ).success.value
        .set(IndividualNationalInsuranceNumberPage(Index(0)), "AB123456A").success.value
        .set(IndividualConnectionPage(Index(0)), IndividualConnection.Partner).success.value
        .set(IndividualBusinessDetailsPage(Index(0)), IndividualBusinessDetails.Yes).success.value
        .set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
        .set(
          YourContactDetailsPage,
          YourContactDetails("FirstName", "LastName", "Tel", Some("Email"), Some("MemorableWord"))
        ).success.value
        .set(WhenActivityHappenPage, WhenActivityHappen.OverFiveYears).success.value
        .set(SupportingDocumentPage, SupportingDocument.Yes).success.value
        .set(HowManyPeopleKnowPage, HowManyPeopleKnow.MoreThanTenIndividuals).success.value
        .set(DocumentationDescriptionPage, "documentation").success.value
        .set(DescriptionActivityPage, "description").success.value
        .set(ApproximateValuePage, BigDecimal(approxVal)).success.value
        .set(ActivityTypePage, "activityType").success.value
        .set(ActivityTimePeriodPage, ActivityTimePeriod.NextWeek).success.value
        .set(ActivitySourceOfInformationPage, ActivitySourceOfInformation.ObservedTheActivity).success.value

      val isBusinessJourney = answers.isBusinessJourney

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        val activityDetails = SummaryListViewModel(
          Seq(
            ActivityTypeSummary.row(answers)(messages(application)),
            ApproximateValueSummary.row(answers)(messages(application)),
            WhenActivityHappenSummary.row(answers)(messages(application)),
            ActivityTimePeriodSummary.row(answers)(messages(application)),
            HowManyPeopleKnowSummary.row(answers)(messages(application)),
            DescriptionActivitySummary.row(answers)(messages(application)),
            ActivitySourceOfInformationSummary.row(answers)(messages(application))
          ).flatten
        )

        val yourDetails = SummaryListViewModel(
          YourContactDetailsSummary.rows(answers)(messages(application)) ++
            SupportingDocumentSummary.row(answers)(messages(application)).toList ++
            DocumentationDescriptionSummary.row(answers)(messages(application))
        )

        val provideContact =
          SummaryListViewModel(ProvideContactDetailsSummary.row(answers)(messages(application)).toSeq)

        val businessDetails = SummaryListViewModel(
          Seq(
            BusinessNameSummary.row(answers, 0)(messages(application)),
            TypeBusinessSummary.row(answers, 0)(messages(application)),
            BusinessAddressSummary.row(answers, 0)(messages(application)),
            BusinessContactDetailsSummary.row(answers, 0)(messages(application)),
            ReferenceNumbersSummary.row(answers, 0)(messages(application)),
            SelectConnectionBusinessSummary.row(answers, 0)(messages(application))
          ).flatten
        )

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          isBusinessJourney,
          activityDetails,
          yourDetails,
          businessDetails,
          1,
          provideContact
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
