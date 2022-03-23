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
import forms.BusinessInformationCheckFormProvider
import models.{BusinessInformationCheck, Index, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import pages.BusinessInformationCheckPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.BusinessInformationCheckView

import scala.concurrent.Future

class BusinessInformationCheckControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val businessInformationCheckRoute =
    routes.BusinessInformationCheckController.onPageLoad(Index(0), NormalMode).url

  private val formProvider = new BusinessInformationCheckFormProvider()
  private val form         = formProvider()

  private val answers           = emptyUserAnswers
  private val isBusinessJourney = answers.isBusinessJourney

  "BusinessInformationCheck Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, businessInformationCheckRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BusinessInformationCheckView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, Index(0), NormalMode, isBusinessJourney)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(
        BusinessInformationCheckPage(Index(0)),
        BusinessInformationCheck.values.toSet
      ).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, businessInformationCheckRoute)

        val view = application.injector.instanceOf[BusinessInformationCheckView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(BusinessInformationCheck.values.toSet),
          Index(0),
          NormalMode,
          isBusinessJourney
        )(request, messages(application)).toString
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
          FakeRequest(POST, businessInformationCheckRoute)
            .withFormUrlEncodedBody(("value[0]", BusinessInformationCheck.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, businessInformationCheckRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[BusinessInformationCheckView]

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
        val request = FakeRequest(GET, businessInformationCheckRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, businessInformationCheckRoute)
            .withFormUrlEncodedBody(("value[0]", BusinessInformationCheck.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
