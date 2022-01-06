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
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualNameProvider
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class IndividualNameControllerSpec
    extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  lazy val individualNameRoute = routes.IndividualNameController.onPageLoad().url

  val formProvider = new IndividualNameProvider()
  val form         = formProvider()

  private val controller = app.injector.instanceOf[IndividualNameController]

  "Individual's name page view" should {
    val result          = controller.onPageLoad()(fakeRequest)
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {

      when(mockSessionCache.isCachePresent(any[String])).thenReturn(Future.successful(false))
      when(mockUserAnswersCache.getIndividualName()(hc)).thenReturn(Future.successful(None))

      doc.getElementsByTag("h1").text() shouldBe messages("individualName.header")

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      running(application) {

        val request =
          FakeRequest(POST, individualNameRoute)
            .withFormUrlEncodedBody(("firstName", "F*R*E*E"))

        val result = route(application, request).get

        status(result) shouldBe BAD_REQUEST

        application.stop()
      }
    }

    "return to individual name view page" in {

      val request = FakeRequest(GET, routes.IndividualNameController.onPageLoad().url)

      val result = route(application, request).get

      status(result) mustEqual SEE_OTHER

    }

  }

}
