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
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubmissionService
import views.html.SubmitYourReportView

import scala.concurrent.Future

class SubmitYourReportControllerSpec extends SpecBase with MockitoSugar {

  "SubmitYourReport Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.SubmitYourReportController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[SubmitYourReportView]
        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }

    "must submit to the backend and redirect to the submission successful page" in {

      val mockSubmissionService = mock[SubmissionService]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SubmissionService].toInstance(mockSubmissionService))
        .build()

      running(application) {
        when(mockSubmissionService.submit(any())(any())).thenReturn(Future.successful(()))
        val request = FakeRequest(POST, routes.SubmitYourReportController.onSubmit().url)
        val result  = route(application, request).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ReportSubmittedController.onPageLoad().url
        verify(mockSubmissionService, times(1)).submit(any())(any())
      }
    }
  }
}
