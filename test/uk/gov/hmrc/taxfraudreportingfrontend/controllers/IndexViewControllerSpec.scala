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
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.Helpers.baseApplicationBuilder.injector
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class IndexViewControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure("metrics.jvm" -> false, "metrics.enabled" -> false)
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val controller = app.injector.instanceOf[IndexViewController]

  "GET /" should {
    "return 200" in {
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 with session Id" in {
      val result = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "fakesessionid"))
      status(result) shouldBe Status.OK
    }

    val result: Future[Result] = controller.onPageLoad()(fakeRequest)
    status(result) shouldBe Status.OK
    val content: String = contentAsString(result)
    val doc: Document   = Jsoup.parse(content)

    "load the page content" in {

      doc.getElementsByTag("h1").text() shouldBe messages("index.header")

      doc.getElementById("first-line").text() shouldBe messages("index.p1")

      doc.getElementById("second-line").text() shouldBe messages("index.p2")

      doc.getElementsByClass("govuk-inset-text").text() shouldBe messages("index.inset.text")

      doc.text(messages("index.list.item1"))

      doc.text(messages("index.list.item2"))

      doc.text(messages("index.list.item3"))

    }

    "contain a continue button that redirects to Activity type Page" in {

      doc.getElementsByAttributeValue("href", "/activity-type")

    }

  }
}
