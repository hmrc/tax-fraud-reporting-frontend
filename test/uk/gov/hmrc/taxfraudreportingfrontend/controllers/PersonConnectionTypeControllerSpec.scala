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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatest.{Matchers, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty, GET, POST}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class PersonConnectionTypeControllerSpec extends BaseSpec with Matchers with MockitoSugar with OptionValues {

  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = application.injector.instanceOf[PersonConnectionTypeController]

  lazy val selectConnectionRoute: String = routes.PersonConnectionTypeController.onPageLoad().url

  "Person connection type page view" should {

    "redirect to the page when there is no session id" in {
      val request = FakeRequest(GET, routes.PersonConnectionTypeController.onPageLoad().url)
      val result  = route(application, request).get
      status(result) mustEqual SEE_OTHER
    }

    "return OK when there is no session" in {
      when(mockSessionCache.get()(any())).thenReturn(Future.successful(None))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK
    }

    "return OK when there is no person connection type data" in {
      when(mockSessionCache.get()(any())).thenReturn(Future.successful(Some(FraudReportDetails())))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK
    }

    "return a Bad Request when invalid data is submitted" in {
      val request =
        FakeRequest(POST, selectConnectionRoute)
          .withFormUrlEncodedBody(("otherConnection" -> "FŔĘĘ"))
      val result = controller.onSubmit()(request)
      status(result) shouldBe BAD_REQUEST
    }

  }

}
