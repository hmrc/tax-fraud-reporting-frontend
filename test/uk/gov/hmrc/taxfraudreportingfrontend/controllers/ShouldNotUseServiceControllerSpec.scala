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

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{defaultAwaitTimeout, status}

class ShouldNotUseServiceControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  new GuiceApplicationBuilder()
    .configure("metrics.jvm" -> false, "metrics.enabled" -> false)
    .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val controller = app.injector.instanceOf[ShouldNotUseServiceController]

  "Should Not Use Service VIew" should {

    "return 200 OK response when given valid activity name" in {

      val result = controller.onPageLoad(activityType = "activity-related-drugs")(fakeRequest)

      status(result) shouldBe Status.OK

    }

    "return activity name cannot be found" in {

      val result = controller.onPageLoad(activityType = "activity-related-drugsggggg")(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND

    }

  }

}
