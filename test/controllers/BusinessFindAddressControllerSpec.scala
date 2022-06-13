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
import forms.FindAddressFormProvider
import models.{FindAddress, Index, IndividualBusinessDetails, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import pages.{BusinessFindAddressPage, FindAddressPage, IndividualBusinessDetailsPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.{BusinessFindAddressView}

import scala.concurrent.Future

class BusinessFindAddressControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new FindAddressFormProvider()
  val form         = formProvider()

  lazy val businessFindAddressRoute = routes.BusinessFindAddressController.onPageLoad(Index(0), NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(FindAddressPage.toString -> Json.obj("Postcode" -> "value 1", "Property" -> "value 2"))
  )

  "BusinessFindAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(
          IndividualBusinessDetailsPage(Index(0)),
          IndividualBusinessDetails.Yes
        ).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, businessFindAddressRoute)

        val view = application.injector.instanceOf[BusinessFindAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, Index(0), NormalMode, isBusinessDetails = true)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(
          BusinessFindAddressPage(Index(0)),
          FindAddress("EH12 9JE", Option.empty)
        ).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, businessFindAddressRoute)

        val view = application.injector.instanceOf[BusinessFindAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(FindAddress("EH12 9JE", Option.empty)),
          Index(0),
          NormalMode,
          isBusinessDetails = false
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
          FakeRequest(POST, businessFindAddressRoute)
            .withFormUrlEncodedBody(("Postcode", "PE5 7BW"), ("Property", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(
          IndividualBusinessDetailsPage(Index(0)),
          IndividualBusinessDetails.Yes
        ).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, businessFindAddressRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[BusinessFindAddressView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, Index(0), NormalMode, isBusinessDetails = true)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, businessFindAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, businessFindAddressRoute)
            .withFormUrlEncodedBody(("Postcode", "value 1"), ("Property", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }

}
