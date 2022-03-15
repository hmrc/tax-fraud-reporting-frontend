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
import forms.ActivityTypeFormProvider
import models.{ActivityType, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.scalatest.Assertion
import pages.ActivityTypePage
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import services.ActivityTypeService
import views.html.ActivityTypeView

import scala.concurrent.Future

class ActivityTypeControllerSpec extends SpecBase {
  "ActivityType Controller" - {
    def onwardRoute = Call("GET", "/foo")

    def withForm(application: Application)(test: Form[ActivityType] => Assertion) = {
      val formProvider = new ActivityTypeFormProvider(mockActivityTypeService)

      running(application) {
        test(formProvider())
      }
    }

    lazy val activityTypeRoute = routes.ActivityTypeController.onPageLoad(NormalMode).url

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      withForm(application) { form =>
        val request = FakeRequest(GET, activityTypeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ActivityTypeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when the user has no session data" in {

      val application = applicationBuilder(userAnswers = None).build()

      withForm(application) { form =>
        val request = FakeRequest(GET, activityTypeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ActivityTypeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(ActivityTypePage, mockActivityTypeService.allActivities.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      withForm(application) { form =>
        val request = FakeRequest(GET, activityTypeRoute)

        val view = application.injector.instanceOf[ActivityTypeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form fill mockActivityTypeService.allActivities.head, NormalMode)(
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
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[ActivityTypeService].toInstance(mockActivityTypeService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, activityTypeRoute)
            .withFormUrlEncodedBody(("value", mockActivityTypeService.allActivities.head.nameKey))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the next page when valid data is submitted when the user has no session" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[Navigator].toInstance(getFakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[ActivityTypeService].toInstance(mockActivityTypeService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, activityTypeRoute)
            .withFormUrlEncodedBody(("value", mockActivityTypeService.allActivities.head.nameKey))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      withForm(application) { form =>
        val request =
          FakeRequest(POST, activityTypeRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ActivityTypeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }
  }
}
