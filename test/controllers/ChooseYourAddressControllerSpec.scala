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
import forms.{BusinessChooseYourAddressFormProvider, ChooseYourAddressFormProvider}
import models.addresslookup.ProposedAddress
import models.{ChooseYourAddress, FindAddress, Index, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{BusinessFindAddressPage, ChooseYourAddressPage, FindAddressPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import services.{AddressLookupService, AddressService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ChooseYourAddressView

import scala.concurrent.Future

class ChooseYourAddressControllerSpec extends SpecBase {

  def onwardRoute  = Call("GET", "/report-tax-fraud/1/individual/confirm-address")
  val fromPostcode = "AA1 1AA"

  lazy val chooseYourAddressRoute =
    routes.ChooseYourAddressController.onPageLoad(Index(0), NormalMode).url

  implicit val hc = HeaderCarrier()

  val formProvider = new ChooseYourAddressFormProvider()
  val form         = formProvider()

  val proposal: Seq[ProposedAddress] = Seq(
    ProposedAddress(
      "GB1234567890",
      uprn = None,
      parentUprn = None,
      usrn = None,
      organisation = None,
      "ZZ11 1ZZ",
      lines = List("line1", "line2"),
      town = "town"
    )
  )

  "ChooseYourAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val nameAnswers =
        emptyUserAnswers.set(FindAddressPage(Index(0)), FindAddress(fromPostcode, Some("test"))).success.value

      val mockAddressService = mock[AddressService]
      when(mockAddressService.lookup(fromPostcode, Some("test"))) thenReturn Future.successful(proposal)

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val addressServiceTest = new AddressService {
        override def lookup(postcode: String, filter: Option[String])(implicit
          hc: HeaderCarrier
        ): Future[Seq[ProposedAddress]] =
          Future.successful(proposal)
      }

      val application =
        applicationBuilder(Some(nameAnswers))
          .overrides(bind[AddressService].toInstance(addressServiceTest))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

      running(application) {
        val request = FakeRequest(GET, chooseYourAddressRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ChooseYourAddressView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, Index(0), NormalMode, Proposals(Some(proposal)))(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val choseAddressAnswers = emptyUserAnswers.set(ChooseYourAddressPage(Index(0)), proposal).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(choseAddressAnswers))
          .overrides(
            bind[Navigator].toInstance(getFakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.ChooseYourAddressController.onSubmit(Index(0), NormalMode).url)
            .withFormUrlEncodedBody(("value", "GB1234567890"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val choseAddressAnswers = emptyUserAnswers.set(ChooseYourAddressPage(Index(0)), proposal).success.value

      val application =
        applicationBuilder(userAnswers = Some(choseAddressAnswers))
          .overrides(bind[Navigator].toInstance(getFakeNavigator(onwardRoute)))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.ChooseYourAddressController.onSubmit(Index(0), NormalMode).url)

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[ChooseYourAddressView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, chooseYourAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, chooseYourAddressRoute)
            .withFormUrlEncodedBody(("value[0]", "AddressId"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
