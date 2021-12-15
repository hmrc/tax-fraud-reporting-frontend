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
import uk.gov.hmrc.taxfraudreportingfrontend.models.ActivityType
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class UserAnswersCacheSpec extends BaseSpec with BeforeAndAfterEach {

  override implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  private val testCache =
    new UserAnswersCache(mockSessionCache)

  private val mockActivityType =
    ActivityType(
      "22030000",
      "activityType.name.mock_name",
      Seq("CJRS", "Furlough", "COVID", "Corona", "Coronavirus Job Retention Scheme")
    )

  protected override def beforeEach: Unit = {
    reset(mockSessionCache)

    when(mockSessionCache.saveFraudReportDetails(any[FraudReportDetails])(any[HeaderCarrier]))
      .thenReturn(Future.successful(true))

    when(mockSessionCache.fraudReportDetails(any[HeaderCarrier])).thenReturn(
      Future.successful(FraudReportDetails(Some(mockActivityType)))
    )

  }

  "Calling userAnswersCache" should {
    "save activityType Details in frontend cache" in {

      Await.result(testCache.cacheActivityType(mockActivityType), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(hc))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.activityType.get.code shouldBe "22030000"

      val testCacheData: Option[ActivityType] = Await.result(testCache.getActivityType(), Duration.Inf)
      testCacheData.get.code shouldBe "22030000"

    }

    "get empty activityType Details from cache" in {

      when(mockSessionCache.fraudReportDetails(any[HeaderCarrier])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Option[ActivityType] = Await.result(testCache.getActivityType(), Duration.Inf)
      testCacheData shouldBe None
    }
  }
}
