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

import akka.util.Helpers.Requiring
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.data.Form
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.taxfraudreportingfrontend.forms.ReportingTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.models.ReportingType
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class ReportingTypeControllerSpec extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure("metrics.jvm" -> false, "metrics.enabled" -> false)
      .build()

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  val formProvider              = new ReportingTypeProvider()
  val form: Form[ReportingType] = formProvider()

  lazy val reportingTypeRoute: String = routes.ReportingTypeController.onPageLoad().url

  private val controller = app.injector.instanceOf[ReportingTypeController]

  "Reporting Type views" should {

    val result          = controller.onPageLoad()(fakeRequest)
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {
      doc.getElementsByTag("h1").text() shouldBe messages("reportingType.header")
    }

    "return bad request when given invalid reporting type" in {

      val request = FakeRequest(POST, routes.ReportingTypeController.onSubmit().url)

      val resultOption = route(app, request).value

      resultOption foreach { result => status(result) shouldBe BAD_REQUEST }

    }

    "return 200 OK response when given valid reporting type" in {

      val result = controller.onPageLoad()(fakeRequest)

      status(result) shouldBe Status.OK

    }

    "redirect to next page when given valid reporting type" in {

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        EnhancedFakeRequest("POST", "/report-tax-fraud/person-or-business").withFormUrlEncodedBody("value" -> "Person")

      val response: Future[Result] = route(app, request).get

      status(response) shouldBe SEE_OTHER

    }

  }

}
