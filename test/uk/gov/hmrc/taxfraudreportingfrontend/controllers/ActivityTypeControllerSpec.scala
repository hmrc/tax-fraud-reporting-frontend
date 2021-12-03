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
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{
  contentAsString,
  defaultAwaitTimeout,
  route,
  status,
  writeableOf_AnyContentAsFormUrlEncoded
}
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class ActivityTypeControllerSpec
    extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure("metrics.jvm" -> false, "metrics.enabled" -> false)
      .build()

  val fakeRequest = FakeRequest("GET", "/")

  private val controller = app.injector.instanceOf[ActivityTypeController]

  "Activity Type views" should {
    val result          = controller.onPageLoad()(fakeRequest)
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {

      doc.getElementsByTag("h1").text() shouldBe messages("activityType.header")

      doc.getElementById("first-para").text() shouldBe messages("activityType.p1")

      doc.getElementById("hint-text").text() shouldBe messages("activityType.p2")

    }

    "return bad request when given invalid activity type" in {

      implicit val request =
        EnhancedFakeRequest("POST", "/report-tax-fraud/type-activity").withFormUrlEncodedBody(
          ("activityType" -> "234567")
        )

      val response: Future[Result] = route(app, request).get

      status(response) shouldBe BAD_REQUEST

    }

    "return 200 OK response when given valid activity type" in {

      implicit val request =
        EnhancedFakeRequest("POST", "/report-tax-fraud/type-activity").withFormUrlEncodedBody(("activityType" -> ""))

      val response: Future[Result] = route(app, request).get

      status(response) shouldBe OK

    }

  }
}