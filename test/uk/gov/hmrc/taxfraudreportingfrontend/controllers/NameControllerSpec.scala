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
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

class NameControllerSpec extends BaseSpec with Matchers with Mappings with GuiceOneAppPerSuite with MockitoSugar {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = app.injector.instanceOf[NameController]

  "Individual's name page view" should {
    val result          = controller.onPageLoad()(fakeRequest)
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {

      doc.getElementsByTag("h1").text() shouldBe messages("individualName.header")

    }
  }

}
