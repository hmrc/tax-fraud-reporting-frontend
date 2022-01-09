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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.Matchers
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{
  contentAsString,
  defaultAwaitTimeout,
  route,
  running,
  status,
  writeableOf_AnyContentAsEmpty,
  writeableOf_AnyContentAsFormUrlEncoded,
  GET,
  POST
}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class IndividualContactControllerSpec
    extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  lazy val individualContactRoute: String = routes.IndividualContactController.onPageLoad().url

  private val controller = app.injector.instanceOf[IndividualContactController]

  "Individual's Contact page view" should {

    when(mockUserAnswersCache.getIndividualContact()(getRequest)).thenReturn(Future.successful(None))

    val result          = controller.onPageLoad()(fakeRequest)
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {

      doc.getElementsByTag("h1").text() shouldBe messages("individualContact.header")

      doc.getElementById("value-hint").text() shouldBe messages("individualContact.hint")

    }

    "load the page content from cache individual contact is empty" in {

      running(application) {

        val result =
          controller.onPageLoad()(FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionidNew"))
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("individualContact.header")

        doc.getElementById("value-hint").text() shouldBe messages("individualContact.hint")

      }

    }

    "load the individual contact page content with cache" in {

      running(application) {

        val result          = controller.onPageLoad()(fakeRequest)
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("individualContact.header")

        doc.getElementById("value-hint").text() shouldBe messages("individualContact.hint")

      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      running(application) {

        val request =
          FakeRequest(POST, individualContactRoute)
            .withFormUrlEncodedBody(("email_Address", "&*joe.com"))

        val result = route(application, request).get

        status(result) shouldBe BAD_REQUEST

        application.stop()
      }
    }

    "return to individual Concat view page" in {

      val request = FakeRequest(GET, routes.IndividualContactController.onPageLoad().url)

      val result = route(application, request).get

      status(result) mustEqual SEE_OTHER

    }
  }
}
