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

package uk.gov.hmrc.taxfraudreportingfrontend.controllers

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatest.{Matchers, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualContact
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class IndividualContactControllerSpec
  extends BaseSpec with Matchers with MockitoSugar with OptionValues {

  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  lazy val individualContactRoute: String = routes.IndividualContactController.onPageLoad().url

  private val controller = application.injector.instanceOf[IndividualContactController]

  "Individual's Contact page view" should {

    "redirect to the index page when there is no session id" in {
      val result = controller.onPageLoad()(FakeRequest())
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.IndexViewController.onPageLoad().url
    }

    "return OK when there is no session" in {
      when(mockSessionCache.get()(any())).thenReturn(Future.successful(None))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK
    }

    "return OK when there is no individual contact details" in {
      when(mockSessionCache.get()(any())).thenReturn(Future.successful(Some(FraudReportDetails())))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK
    }

    "return a Bad Request when invalid data is submitted" in {
      val request =
        FakeRequest(POST, individualContactRoute)
          .withFormUrlEncodedBody(("emailAddress" -> "&*joe.com"))
      val result = controller.onSubmit()(request)
      status(result) mustEqual BAD_REQUEST
    }

    // TODO this should be un-ignored when routing is complete
    "redirect to individual contact page when valid data is submitted" ignore {
      val expectedData = IndividualContact(
        landline_Number = None, mobile_Number = None, email_Address = Some("joe@example.com")
      )
      when(mockUserAnswersCache.cacheIndividualContact(eqTo(Some(expectedData)))(any())).thenReturn(Future.successful(FraudReportDetails()))
      val request = FakeRequest(POST, individualContactRoute)
        .withFormUrlEncodedBody("emailAddress" -> "joe@example.com")
      val result = controller.onSubmit()(request)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual individualContactRoute
    }
  }
}
