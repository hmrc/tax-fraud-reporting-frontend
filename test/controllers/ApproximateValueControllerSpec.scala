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
import forms.ApproximateValueFormProvider
import models.WhenActivityHappen.OverFiveYears
import models.{NormalMode, UserAnswers, WhenActivityHappen}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import pages.{ApproximateValuePage, WhenActivityHappenPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.hmrcfrontend.controllers.routes
import views.html.ApproximateValueView

import scala.concurrent.Future

class ApproximateValueControllerSpec extends SpecBase {

  private val formProvider = new ApproximateValueFormProvider()
  private val form         = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer: BigDecimal = 100.99

  private lazy val approximateValueRoute = routes.ApproximateValueController.onPageLoad(NormalMode).url

  "ApproximateValue Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhenActivityHappenPage, OverFiveYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      running(application) {
        val request = FakeRequest(GET, approximateValueRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ApproximateValueView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, OverFiveYears)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val approxValue  = UserAnswers(userAnswersId).set(ApproximateValuePage, validAnswer).success.value
      val userAnswers = approxValue.set(WhenActivityHappenPage, OverFiveYears).success.value
      val application  = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, approximateValueRoute)

        val view = application.injector.instanceOf[ApproximateValueView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, OverFiveYears)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers           = UserAnswers(userAnswersId).set(WhenActivityHappenPage, OverFiveYears).success.value
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(getFakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, approximateValueRoute)
            .withFormUrlEncodedBody(("value", "100.99"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhenActivityHappenPage, OverFiveYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, approximateValueRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[ApproximateValueView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, OverFiveYears)(
          request,
          messages(application)
        ).toString
      }
    }

    "must return a Bad Request and errors when blank data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhenActivityHappenPage, OverFiveYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, approximateValueRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ApproximateValueView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, OverFiveYears)(
          request,
          messages(application)
        ).toString
      }
    }

    "must return a Bad Request and errors when non numeric data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhenActivityHappenPage, OverFiveYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, approximateValueRoute)
            .withFormUrlEncodedBody(("value", "test"))

        val boundForm = form.bind(Map("value" -> "test"))

        val view = application.injector.instanceOf[ApproximateValueView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, OverFiveYears)(
          request,
          messages(application)
        ).toString
      }
    }

    "must return a Bad Request and errors when not whole number is submitted" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhenActivityHappenPage, OverFiveYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, approximateValueRoute)
            .withFormUrlEncodedBody(("value", "1098.90ab"))

        val boundForm = form.bind(Map("value" -> "1098.90ab"))

        val view = application.injector.instanceOf[ApproximateValueView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, OverFiveYears)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, approximateValueRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, approximateValueRoute)
            .withFormUrlEncodedBody(("value", "100.99"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
