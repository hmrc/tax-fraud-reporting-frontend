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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatest.{Matchers, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.OK
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.Future

class AddAnotherPersonControllerSpec extends BaseSpec with Matchers with MockitoSugar with OptionValues {

  val request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  private val controller = application.injector.instanceOf[AddAnotherPersonController]

  "add another person page view" should {

    "return OK when there is no session" in {
      when(mockSessionCache.get()(any())).thenReturn(Future.successful(None))
      val result = controller.onPageLoad()(request)
      status(result) mustEqual OK
    }

  }

}
