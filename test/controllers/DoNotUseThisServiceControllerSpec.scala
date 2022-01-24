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
import models.ActivityType
import org.scalatest.TryValues
import pages.ActivityTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DoNotUseThisServiceView

class DoNotUseThisServiceControllerSpec extends SpecBase with TryValues {

  "DoNotUseThisService Controller" - {

    "must return OK and the correct view for a GET when the user has an activity which is the responsibility of another department" in {

      val userAnswers =
        emptyUserAnswers.set(ActivityTypePage, ActivityType.list.find(_.code == "22030036").value).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.DoNotUseThisServiceController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DoNotUseThisServiceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view("activity-related-to-drugs")(request, messages(application)).toString
      }
    }

    "must redirect to the journey recovery page for a GET when the user has no session" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.DoNotUseThisServiceController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to the journey recovery page for a GET when the user has no activity" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.DoNotUseThisServiceController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to the journey recovery page for a GET when the user has an activity which belongs to HMRC" in {

      val userAnswers = emptyUserAnswers.set(ActivityTypePage, ActivityType.list.head).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.DoNotUseThisServiceController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
