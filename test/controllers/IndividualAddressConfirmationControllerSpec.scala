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
import models.{AddressResponse, Index, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import services.AddressLookupService

import scala.concurrent.Future

class IndividualAddressConfirmationControllerSpec extends SpecBase with MockitoSugar with TryValues with OptionValues {

  val mockSessionRepository    = mock[SessionRepository]
  val mockAddressLookupService = mock[AddressLookupService]
  val onwardRoute              = Call(GET, "")

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
    .overrides(
      bind[AddressLookupService].toInstance(mockAddressLookupService),
      bind[SessionRepository].toInstance(mockSessionRepository),
      bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
    )
    .build()

  val controller = application.injector.instanceOf[IndividualAddressConfirmationController]

  "IndividualAddressConfirmation Controller" - {

    "must update user answers and redirect to the next page when a confirmed address is available" in {

      val addressResponse =
        AddressResponse(lines = List("foo", "bar"), postcode = Some("postcode"), country = Some("country"))

      running(application) {
        when(mockAddressLookupService.retrieveAddress(eqTo("foo"))(any())).thenReturn(
          Future.successful(Some(addressResponse))
        )
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        val request = FakeRequest(
          GET,
          routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, Some("foo")).url
        )
        val result = route(application, request).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to journey recovery controller if there is no confirmed address available" in {
      when(mockAddressLookupService.retrieveAddress(eqTo("foo"))(any())).thenReturn(Future.successful(None))
      val request = FakeRequest(
        GET,
        routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, Some("foo")).url
      )
      val result = controller.onPageLoad(Index(0), NormalMode, Some("foo"))(request)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must fail if the id is missing from the request" in {
      val request =
        FakeRequest(GET, routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, None).url)
      controller.onPageLoad(Index(0), NormalMode, None)(request).failed.futureValue
    }

    "must fail if the address lookup service fails" in {
      when(mockAddressLookupService.retrieveAddress(eqTo("foo"))(any())).thenReturn(Future.failed(new Exception()))
      val request = FakeRequest(
        GET,
        routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, Some("foo")).url
      )
      controller.onPageLoad(Index(0), NormalMode, Some("foo"))(request).failed.futureValue
    }

    "must fail if the session repository fails" in {
      val addressResponse =
        AddressResponse(lines = List("foo", "bar"), postcode = Some("postcode"), country = Some("country"))
      when(mockAddressLookupService.retrieveAddress(eqTo("foo"))(any())).thenReturn(
        Future.successful(Some(addressResponse))
      )
      when(mockSessionRepository.set(any())).thenReturn(Future.failed(new Exception()))
      val request = FakeRequest(
        GET,
        routes.IndividualAddressConfirmationController.onPageLoad(Index(0), NormalMode, Some("foo")).url
      )
      controller.onPageLoad(Index(0), NormalMode, Some("foo"))(request).failed.futureValue
    }
  }
}
