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
import forms.IndividualSelectCountryFormProvider
import models.{Index, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import pages.BusinessSelectCountryPage
import play.api
import play.api.{inject, Configuration}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{
  contentAsString,
  defaultAwaitTimeout,
  redirectLocation,
  route,
  running,
  status,
  writeableOf_AnyContentAsEmpty,
  writeableOf_AnyContentAsFormUrlEncoded,
  GET
}
import repositories.SessionRepository
import uk.gov.hmrc.http.HttpVerbs.POST
import viewmodels.{Business, Individual}
import views.html.IndividualSelectCountryView

import scala.concurrent.Future

class BusinessSelectCountryControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new IndividualSelectCountryFormProvider(mock[Configuration])
  val form         = formProvider()

  private val answers           = emptyUserAnswers
  private val isBusinessJourney = answers.isBusinessJourney

  lazy val businessSelectCountryRoute = routes.BusinessSelectCountryController.onPageLoad(Index(0), NormalMode).url

  "BusinessSelectCountry Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, businessSelectCountryRoute)

        val result         = route(application, request).value
        val countryJourney = if (isBusinessJourney) Business else Individual(true)
        val view           = application.injector.instanceOf[IndividualSelectCountryView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, Index(0), NormalMode, countryJourney)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(BusinessSelectCountryPage(Index(0)), "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {

        val request        = FakeRequest(GET, businessSelectCountryRoute)
        val countryJourney = if (isBusinessJourney) Business else Individual(true)
        val view           = application.injector.instanceOf[IndividualSelectCountryView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), Index(0), NormalMode, countryJourney)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when invalid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            inject.bind[Navigator].toInstance(getFakeNavigator(onwardRoute)),
            api.inject.bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, businessSelectCountryRoute)
            .withFormUrlEncodedBody(("value" -> "answer"))

        val boundForm = form.bind(Map("value" -> ""))

        val view           = application.injector.instanceOf[IndividualSelectCountryView]
        val countryJourney = if (isBusinessJourney) Business else Individual(true)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, Index(0), NormalMode, countryJourney)(
          request,
          messages(application)
        ).toString
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, businessSelectCountryRoute)
            .withFormUrlEncodedBody(("value", ""))
        val countryJourney = if (isBusinessJourney) Business else Individual(true)
        val boundForm      = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[IndividualSelectCountryView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, Index(0), NormalMode, countryJourney)(
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
          FakeRequest(POST, businessSelectCountryRoute)
            .withFormUrlEncodedBody(("country", "gb"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, businessSelectCountryRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, businessSelectCountryRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }

}
