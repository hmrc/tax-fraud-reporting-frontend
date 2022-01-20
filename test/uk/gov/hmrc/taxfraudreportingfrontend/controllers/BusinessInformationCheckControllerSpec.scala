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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, defaultAwaitTimeout, redirectLocation, route, status, writeableOf_AnyContentAsEmpty, writeableOf_AnyContentAsFormUrlEncoded}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.models.BusinessInformationCheck
import uk.gov.hmrc.taxfraudreportingfrontend.models.BusinessInformationCheck.BusinessName
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class BusinessInformationCheckControllerSpec
    extends BaseSpec with Matchers with MockitoSugar with OptionValues with GuiceOneAppPerSuite {

  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = application.injector.instanceOf[BusinessDetailsController]

  lazy val businessInfoCheckRoute: String = routes.BusinessInformationCheckController.onPageLoad().url

  "business information check page view" should {

    "return to business information check view page" in {

      val request = FakeRequest(GET, businessInfoCheckRoute)
      val result  = route(application, request).get
      status(result) mustEqual SEE_OTHER

    }

    "return OK when there is no session" in {

      when(mockSessionCache.get()(any())).thenReturn(Future.successful(None))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK

    }

    "return OK when there is no session id" in {

      when(mockSessionCache.createCacheIfNotPresent()(getRequest)).thenReturn(Future.successful(true))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK

    }

    "load the page content from cache business information data is empty" in {

      when(mockUserAnswersCache.getBusinessCheck()(getRequest)).thenReturn(
        Future.successful(Set.empty[BusinessInformationCheck])
      )
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK

    }

    "return OK when there is no business information check data" in {

      when(mockSessionCache.get()(any())).thenReturn(Future.successful(Some(FraudReportDetails())))
      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK

    }

    "return a Bad Request when invalid data is submitted" in {

      val request =
        FakeRequest(POST, businessInfoCheckRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

    }

    "return See Other when valid data is submitted" in {
      val mockBusinessDetails: Set[BusinessInformationCheck] = Set(BusinessName)

      val mockFraudReportCache = FraudReportDetails(businessInformationCheck = mockBusinessDetails)

      implicit val ec: ExecutionContextExecutor = ExecutionContext.global

      implicit val mockRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, businessInfoCheckRoute)
          .withFormUrlEncodedBody("value[]" -> "name")

      when(
        mockUserAnswersCache.cacheBusinessCheck(mockBusinessDetails)
      ).thenReturn(Future.successful(mockFraudReportCache))

      mockUserAnswersCache.cacheBusinessCheck(mockBusinessDetails) foreach {cache => println(s"### $cache ###")}

      val result = route(application, mockRequest).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual businessInfoCheckRoute
    }
  }
}
