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
import models.{AddressLookupLabels, CheckMode, Index, LookupPageLabels, NormalMode}
import org.mockito.ArgumentMatchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AddressLookupService

import scala.concurrent.Future

class IndividualAddressRedirectControllerSpec extends SpecBase with MockitoSugar {

  val mockAddressLookupService = mock[AddressLookupService]

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
    .overrides(inject.bind[AddressLookupService].toInstance(mockAddressLookupService))
    .build()

  val labels: AddressLookupLabels = AddressLookupLabels(lookupPageLabels =
    LookupPageLabels(title = "individualAddress.lookup.title", heading = "individualAddress.lookup.heading")
  )

  "IndividualAddressRedirect Controller" - {

    "must redirect to address lookup frontend with a new journey in normal mode" in {
      when(
        mockAddressLookupService.startJourney(
          eqTo(routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, None).url),
          eqTo(labels)
        )(any())
      )
        .thenReturn(Future.successful("foobar"))
      val request = FakeRequest(GET, routes.IndividualAddressRedirectController.onPageLoad(Index(0), NormalMode).url)
      val result  = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "foobar"
    }

    "must redirect to address lookup frontend with a new journey in check mode mode" in {
      when(
        mockAddressLookupService.startJourney(
          eqTo(routes.IndividualAddressConfirmationController.onPageLoad(Index(0), CheckMode, None).url),
          eqTo(labels)
        )(any())
      )
        .thenReturn(Future.successful("foobar"))
      val request = FakeRequest(GET, routes.IndividualAddressRedirectController.onPageLoad(Index(0), CheckMode).url)
      val result  = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "foobar"
    }

    "must fail if the address lookup service fails" in {
      when(
        mockAddressLookupService.startJourney(
          eqTo(routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, None).url),
          eqTo(labels)
        )(any())
      )
        .thenReturn(Future.failed(new Exception()))
      val request = FakeRequest(GET, routes.IndividualAddressRedirectController.onPageLoad(Index(0), NormalMode).url)
      application.injector.instanceOf[IndividualAddressRedirectController].onPageLoad(Index(0), NormalMode)(
        request
      ).failed.futureValue
    }
  }
}
