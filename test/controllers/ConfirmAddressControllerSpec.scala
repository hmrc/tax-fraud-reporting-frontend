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
import models.{CheckMode, Index, NormalMode, UserAnswers}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.controllers.routes
import viewmodels.{BusinessPart, IndividualPart}
import views.html.ConfirmAddressView

class ConfirmAddressControllerSpec extends SpecBase {

  private val userAnswers = UserAnswers(userAnswersId)
  private val answers     = emptyUserAnswers

  private lazy val confirmAddressRoute = routes.ConfirmAddressController.onPageLoad(Index(0), true, NormalMode).url

  "ConfirmAddress Controller" - {

    "must return other from individual journey and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {

        val isBusinessJourney = userAnswers.isBusinessJourney
        val journeyPart       = if (isBusinessJourney) BusinessPart else IndividualPart(true)

        val request =
          FakeRequest(GET, routes.ConfirmAddressController.onPageLoad(Index(0), isBusinessJourney, NormalMode).url)

        val result = route(application, request).value
        val view   = application.injector.instanceOf[ConfirmAddressView]
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndividualAddressController.onPageLoad(Index(0), NormalMode).url
      }
    }

    /* "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {

        val isBusinessJourney = userAnswers.isBusinessJourney
        val journeyPart       = if (isBusinessJourney) BusinessPart else IndividualPart(true)

        val request =
          FakeRequest(GET, routes.ConfirmAddressController.onPageLoad(Index(0), isBusinessJourney, NormalMode).url)

        val result = route(application, request).value
        val nextPage = if (isBusinessJourney) { routes.BusinessAddressController.onPageLoad(Index(0), NormalMode)}
        val view   = application.injector.instanceOf[ConfirmAddressView]
        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Index(0), Address, isBusinessJourney, journeyPart, nextPage)(request, messages(application)).toString
        //redirectLocation(result).value mustEqual routes.IndividualAddressController.onPageLoad(Index(0), NormalMode).url
      }
    }*/

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, confirmAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

  }
}