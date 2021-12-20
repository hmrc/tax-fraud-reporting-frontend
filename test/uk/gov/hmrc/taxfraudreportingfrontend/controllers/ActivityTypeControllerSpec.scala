/*
 * Copyright 2021 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{
  contentAsString,
  defaultAwaitTimeout,
  route,
  running,
  status,
  writeableOf_AnyContentAsFormUrlEncoded
}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class ActivityTypeControllerSpec
    extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = app.injector.instanceOf[ActivityTypeController]

  "Activity Type views" should {

    "load the page content" in {

      running(application) {
        when(mockSessionCache.isCachePresent(any[String])).thenReturn(Future.successful(false))
        when(mockUserAnswersCache.getActivityType()(hc)).thenReturn(Future.successful(None))

        val result          = controller.onPageLoad()(fakeRequest)
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("activityType.header")

        doc.getElementById("first-para").text() shouldBe messages("activityType.p1")

        doc.getElementById("hint-text").text() shouldBe messages("activityType.p2")

      }

    }

    "load the page content from cache activity type is empty" in {

      running(application) {

        when(mockSessionCache.isCacheNotPresentCreateOne("fakesessionidNew")(hc)).thenReturn(
          Future.successful(FraudReportDetails(activityType = None))
        )

        val result =
          controller.onPageLoad()(FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionidNew"))
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("activityType.header")

        doc.getElementById("first-para").text() shouldBe messages("activityType.p1")

        doc.getElementById("hint-text").text() shouldBe messages("activityType.p2")

      }

    }

    "load the page content with cache" in {

      running(application) {
        when(mockSessionCache.isCachePresent(any[String])).thenReturn(Future.successful(true))

        val result          = controller.onPageLoad()(fakeRequest)
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("activityType.header")

        doc.getElementById("first-para").text() shouldBe messages("activityType.p1")

        doc.getElementById("hint-text").text() shouldBe messages("activityType.p2")

      }

    }

    "return bad request when given invalid activity type" in {

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        EnhancedFakeRequest("POST", "/report-tax-fraud/type-activity").withFormUrlEncodedBody(
          "activityType" -> "234567"
        )

      val response: Future[Result] = route(app, request).get

      status(response) shouldBe BAD_REQUEST

    }

    "redirect to next page when given valid activity type" in {

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        EnhancedFakeRequest("POST", "/report-tax-fraud/type-activity").withFormUrlEncodedBody(
          "activityType" -> "22030000"
        )

      val response: Future[Result] = route(app, request).get

      status(response) shouldBe SEE_OTHER

    }

    "redirect to should not use service page with dynamic page content" in {

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        EnhancedFakeRequest("POST", "/report-tax-fraud/type-activity").withFormUrlEncodedBody(
          "activityType" -> "22030037"
        )

      val response: Future[Result] = route(app, request).get

      status(response) shouldBe SEE_OTHER

    }

  }
}
