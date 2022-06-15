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

package views.errors

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.FakeRequest
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.twirl.api.Html
import views.html.ErrorTemplate

class ErrorTemplateSpec extends SpecBase {

  def messagesApi = injector.instanceOf[MessagesApi]

  trait Setup {
    val errorTemplate = applicationBuilder().injector.instanceOf[ErrorTemplate]
  }

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  "ErrorTemplate" - {

    "render error template correctly" in new Setup {
      val page: Html = errorTemplate.render(
        "This page can’t be found",
        "This page can’t be found",
        "Please check that you have entered the correct web address.",
        FakeRequest(),
        messages.messages
      )
      val document: Document = Jsoup.parse(page.body)
      document.getElementsByClass("govuk-heading-xl").text() shouldBe "This page can’t be found"
      document.getElementsByClass(
        "govuk-body"
      ).text() shouldBe "Please check that you have entered the correct web address."
    }
  }

}
