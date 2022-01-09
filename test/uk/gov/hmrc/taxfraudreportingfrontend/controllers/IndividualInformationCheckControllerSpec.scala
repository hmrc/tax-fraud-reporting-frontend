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
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualInformationCheckProvider
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualInformationCheck
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class IndividualInformationCheckControllerSpec
    extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = app.injector.instanceOf[IndividualInformationCheckController]

  lazy val individualInformationCheckControllerRoute: String =
    routes.IndividualInformationCheckController.onPageLoad().url

  val formProvider                                = new IndividualInformationCheckProvider()
  val form: Form[Set[IndividualInformationCheck]] = formProvider()

  "Information Checker VIew" should {
    val result          = controller.onPageLoad()(fakeRequest)
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {

      when(mockUserAnswersCache.getIndividualInformationCheck()(getRequest)).thenReturn(
        Future.successful(Set.empty[IndividualInformationCheck])
      )

      doc.getElementsByTag("h1").text() shouldBe messages("individual.informationCheck.header")

    }

    "load the page content from cache when individual information check is empty" in {

      running(application) {

        when(mockSessionCache.createCacheIfNotPresent()(getRequest)).thenReturn(Future.successful(true))

        val result =
          controller.onPageLoad()(FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionidNew"))
        val content: String = contentAsString(result)
        val doc: Document   = Jsoup.parse(content)

        doc.getElementsByTag("h1").text() shouldBe messages("individual.informationCheck.header")

        application.stop()

      }

    }

    "return to information check view page" in {

      val request = FakeRequest(GET, routes.IndividualInformationCheckController.onPageLoad().url)

      val result = route(application, request).get

      status(result) mustEqual SEE_OTHER

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      running(application) {

        val request =
          FakeRequest(POST, individualInformationCheckControllerRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val result = route(application, request).get

        status(result) shouldBe BAD_REQUEST

        application.stop()
      }
    }
  }
}
