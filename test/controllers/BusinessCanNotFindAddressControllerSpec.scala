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
import models.{Index, NormalMode, UserAnswers}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.controllers.routes
import views.html.BusinessCanNotFindAddressView

class BusinessCanNotFindAddressControllerSpec extends SpecBase {

  private val userAnswers = UserAnswers(userAnswersId)
  private val answers     = emptyUserAnswers

  "BusinessCanNotFindAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.BusinessCanNotFindAddressController.onPageLoad(Index(0), NormalMode).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BusinessCanNotFindAddressView]
        val isBusinessJourney = userAnswers.isBusinessJourney

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Index(0), NormalMode, isBusinessJourney)(request, messages(application)).toString
      }
    }
  }
}
