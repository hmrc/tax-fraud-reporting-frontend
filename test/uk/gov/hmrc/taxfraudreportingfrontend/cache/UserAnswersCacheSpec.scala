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

package uk.gov.hmrc.taxfraudreportingfrontend.cache

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class UserAnswersCacheSpec extends BaseSpec with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  private val testCache =
    new UserAnswersCache(mockSessionCache)

  protected override def beforeEach: Unit = {
    reset(mockSessionCache)

    when(mockSessionCache.testCache(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(true))

    when(mockSessionCache.getTestCache(any[HeaderCarrier])).thenReturn(Future.successful(Some("existingData")))

  }

  /*TODO this should be the reference for all user answer cache test cases*/
  "Calling userAnswersCache" should {
    "save test Details in frontend cache" in {

      Await.result(testCache.testCache("testString"), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[String])

      verify(mockSessionCache).testCache(requestCaptor.capture())(ArgumentMatchers.eq(hc))
      val holder: String = requestCaptor.getValue
      holder shouldBe "testString"

      val testCacheData: String = Await.result(testCache.getTestCache(), Duration.Inf)
      testCacheData shouldBe "existingData"

    }
  }
}
