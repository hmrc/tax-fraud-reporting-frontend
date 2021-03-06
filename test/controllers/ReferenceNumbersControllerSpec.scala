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
import forms.ReferenceNumbersFormProvider
import models.{Index, NormalMode, ReferenceNumbers, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import pages.ReferenceNumbersPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ReferenceNumbersView

import scala.concurrent.Future

class ReferenceNumbersControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new ReferenceNumbersFormProvider()
  private val form         = formProvider()

  lazy val referenceNumbersRoute = routes.ReferenceNumbersController.onPageLoad(Index(0), NormalMode).url

  private val answers           = emptyUserAnswers
  private val isBusinessJourney = answers.isBusinessJourney

  private val model =
    ReferenceNumbers(
      vatRegistration = Some("vatReference"),
      employeeRefNo = Some("empReference"),
      corporationTax = Some("corporateTax")
    )

  private val userAnswers = UserAnswers(userAnswersId).set(ReferenceNumbersPage(Index(0)), model).success.value

  "ReferenceNumbers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, referenceNumbersRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ReferenceNumbersView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, Index(0), NormalMode, isBusinessJourney)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, referenceNumbersRoute)

        val view = application.injector.instanceOf[ReferenceNumbersView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(model), Index(0), NormalMode, isBusinessJourney)(
          request,
          messages(application)
        ).toString
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
          FakeRequest(POST, referenceNumbersRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, referenceNumbersRoute)
            .withFormUrlEncodedBody(("corporationTax" -> "a" * 11))

        val boundForm = form.bind(Map("corporationTax" -> "a" * 11))

        val view = application.injector.instanceOf[ReferenceNumbersView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, Index(0), NormalMode, isBusinessJourney)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, referenceNumbersRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, referenceNumbersRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
