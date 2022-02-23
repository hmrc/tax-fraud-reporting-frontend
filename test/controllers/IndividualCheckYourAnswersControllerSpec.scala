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
import navigation.{FakeNavigator, Navigator}
import pages._
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers.{
  contentAsString,
  defaultAwaitTimeout,
  redirectLocation,
  route,
  running,
  status,
  writeableOf_AnyContentAsEmpty,
  GET
}
import viewmodels.checkAnswers._
import viewmodels.govuk.SummaryListFluency
import views.html.IndividualCheckYourAnswersView

import java.time.LocalDate

class IndividualCheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Individual Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val onwardRoute = routes.CheckYourAnswersController.onPageLoad

      val answers = emptyUserAnswers
        .set(
          IndividualNamePage(Index(0)),
          IndividualName(Some("firstname"), Some("middlename"), Some("lastname"), Some("aliases"))
        ).success.value
        .set(IndividualAgePage(Index(0)), 45).success.value
        .set(IndividualDateOfBirthPage(Index(0)), LocalDate.now).success.value
        .set(
          IndividualAddressConfirmationPage(Index(0)),
          AddressResponse(List("line"), None, Some("postcode"), Some("country"))
        ).success.value
        .set(
          IndividualContactDetailsPage(Index(0)),
          IndividualContactDetails(Some("landLine"), Some("mobileNumber"), Some("email"))
        ).success.value
        .set(IndividualNationalInsuranceNumberPage(Index(0)), "AB123456A").success.value
        .set(IndividualConnectionPage(Index(0)), IndividualConnection.Partner).success.value
        .set(IndividualBusinessDetailsPage(Index(0)), IndividualBusinessDetails.Yes).success.value
        .set(BusinessNamePage(Index(0)), "name").success.value
        .set(TypeBusinessPage(Index(0)), "businessType").success.value
        .set(
          BusinessAddressConfirmationPage(Index(0)),
          AddressResponse(List("line"), None, Some("postcode"), Some("country"))
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

      val application = applicationBuilder(userAnswers = Some(answers))
        .overrides(inject.bind[Navigator].toInstance(new FakeNavigator(onwardRoute))).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndividualCheckYourAnswersController.onPageLoad(Index(0), NormalMode).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndividualCheckYourAnswersView]

        val individualsDetails = SummaryListViewModel(
          Seq(
            IndividualNameSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualDateFormatSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualDateOfBirthSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualAgeSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualAddressSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualContactDetailsSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualNationalInsuranceNumberSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualConnectionSummary.row(answers, 0, NormalMode)(messages(application)),
            IndividualBusinessDetailsSummary.row(answers, 0, NormalMode)(messages(application))
          ).flatten
        )

        val individualBusinessDetails = SummaryListViewModel(
          Seq(
            BusinessNameSummary.row(answers, 0, NormalMode)(messages(application)),
            TypeBusinessSummary.row(answers, 0, NormalMode)(messages(application)),
            BusinessAddressSummary.row(answers, 0, NormalMode)(messages(application)),
            BusinessContactDetailsSummary.row(answers, 0, NormalMode)(messages(application)),
            ReferenceNumbersSummary.row(answers, 0, NormalMode)(messages(application)),
            SelectConnectionBusinessSummary.row(answers, 0, NormalMode)(messages(application))
          ).flatten
        )

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(individualsDetails, individualBusinessDetails, onwardRoute)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndividualCheckYourAnswersController.onPageLoad(Index(0), NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
