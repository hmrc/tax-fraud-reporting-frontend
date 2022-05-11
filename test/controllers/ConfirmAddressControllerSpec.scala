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

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import base.SpecBase
import forms.AddressFormProvider
import models.backend.Address
import models.{Index, NormalMode, UserAnswers}
import pages.BusinessSelectCountryPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.controllers.routes
import viewmodels.{BusinessPart, IndividualPart}
import views.html.ConfirmAddressView

class ConfirmAddressControllerSpec extends SpecBase {

  private val userAnswers = UserAnswers(userAnswersId)
  private val answers     = emptyUserAnswers

  "ConfirmAddress Controller" - {

    "must return other and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {

        val isBusinessJourney = userAnswers.isBusinessJourney
        val journeyPart       = if (isBusinessJourney) BusinessPart else IndividualPart(true)
        val x                 = userAnswers.getAddress(Index(0), true)

        val request = FakeRequest(GET, routes.ConfirmAddressController.onPageLoad(Index(0), isBusinessJourney).url)

        val result = route(application, request).value
        val view   = application.injector.instanceOf[ConfirmAddressView]
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndividualAddressController.onPageLoad(Index(0), NormalMode).url
      }
    }
  }
}
