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
import org.mockito.Mockito.when
import org.scalatest.Matchers
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
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
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualNino
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class NinoControllerSpec extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  private val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = app.injector.instanceOf[NinoController]

  private val result          = controller.onPageLoad()(fakeRequest)
  private val content: String = contentAsString(result)
  private val doc: Document   = Jsoup.parse(content)

  lazy val ninoRoute: String = routes.NinoController.onPageLoad().url

  "Nino Controller" should {

    "must load page content and the correct view for a GET" in {

      when(mockUserAnswersCache.getNino()(getRequest)).thenReturn(Future.successful(None))

      running(application) {

        doc.getElementsByTag("h1").text() shouldBe messages("nino.header")

        val request = FakeRequest(GET, ninoRoute)

        val result = route(application, request).get

        status(result) mustEqual SEE_OTHER

        application.stop()
      }
    }

    "must load the page content with cache" in {

      running(application) {
        when(mockUserAnswersCache.getNino()(getRequest)).thenReturn(Future.successful(None))

        val result          = controller.onPageLoad()(fakeRequest)
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("nino.header")

      }

    }

    "must return BadRequest when the submitted form is empty" in {

      running(application) {

        val request =
          FakeRequest(POST, ninoRoute)
            .withFormUrlEncodedBody(("nino", ""))

        val result = route(application, request).get

        status(result) shouldBe BAD_REQUEST

        application.stop()
      }

    }

    "must return BadRequest when the submitted NINO is invalid" in {

      running(application) {

        val request =
          FakeRequest(POST, ninoRoute)
            .withFormUrlEncodedBody(("nino", "1Q 12 34 A6 C"))

        val result = route(application, request).get

        status(result) shouldBe BAD_REQUEST

        application.stop()
      }

    }

    "must return OK when the submitted NINO is valid" in {

      when(mockSessionCache.createCacheIfNotPresent()(getRequest)).thenReturn(Future.successful(true))

      Future.successful(Right(IndividualNino))

      IndividualNino -> Seq(IndividualNino)

      status(result) mustEqual OK

    }

  }

}
