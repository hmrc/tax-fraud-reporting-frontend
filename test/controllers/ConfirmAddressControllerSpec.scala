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
import forms.ConfirmAddressFormProvider
import models.{Index, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import pages.ConfirmAddressPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.mvc.Results.Redirect
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewmodels.{BusinessPart, IndividualPart}
import views.html.ConfirmAddressView

import scala.concurrent.Future

class ConfirmAddressControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  private val answers = emptyUserAnswers
  val userAnswers     = UserAnswers(userAnswersId)

  val formProvider = new ConfirmAddressFormProvider()
  val form         = formProvider()

  private lazy val confirmAddressRoute = routes.ConfirmAddressController.onPageLoad(Index(0), NormalMode).url

  "ConfirmAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, confirmAddressRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ConfirmAddressView]

        val journeyPart = if (userAnswers.isBusinessJourney) BusinessPart else IndividualPart(false)
        UserAnswers(userAnswersId) getAddress (Index(0), forBusiness = false) match {
          case Some(address) =>
            status(result) mustEqual OK
            contentAsString(result) mustEqual view(form, Index(0), NormalMode, address, journeyPart)(
              request,
              messages(application)
            ).toString
          case None =>
            Future.successful(Redirect(routes.IndividualAddressController.onPageLoad(Index(0), NormalMode)))
        }

      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ConfirmAddressPage(Index(0)), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, confirmAddressRoute)

        val view = application.injector.instanceOf[ConfirmAddressView]

        val result      = route(application, request).value
        val journeyPart = if (userAnswers.isBusinessJourney) BusinessPart else IndividualPart(false)
        UserAnswers(userAnswersId) getAddress (Index(0), forBusiness = false) match {
          case Some(address) =>
            status(result) mustEqual OK
            contentAsString(result) mustEqual view(form.fill(true), Index(0), NormalMode, address, journeyPart)(
              request,
              messages(application)
            ).toString
          case None =>
            Future.successful(Redirect(routes.IndividualAddressController.onPageLoad(Index(0), NormalMode)))
        }
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(getFakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, confirmAddressRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return other from individual journey and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {

        val request =
          FakeRequest(GET, confirmAddressRoute)

        val result = route(application, request).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndividualAddressController.onPageLoad(Index(0), NormalMode).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, confirmAddressRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ConfirmAddressView]

        val result      = route(application, request).value
        val journeyPart = if (userAnswers.isBusinessJourney) BusinessPart else IndividualPart(false)
        UserAnswers(userAnswersId) getAddress (Index(0), forBusiness = false) match {
          case Some(address) =>
            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual view(boundForm, Index(0), NormalMode, address, journeyPart)(
              request,
              messages(application)
            ).toString
          case None =>
            Future.successful(Redirect(routes.IndividualAddressController.onPageLoad(Index(0), NormalMode)))
        }
      }
    }

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
