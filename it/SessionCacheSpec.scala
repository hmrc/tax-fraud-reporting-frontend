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

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.running
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.mongo.cache.DataKey
import uk.gov.hmrc.taxfraudreportingfrontend.cache.SessionCache
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.models.ActivityType
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails

class SessionCacheSpec extends IntegrationTestSpec with MockitoSugar with BeforeAndAfterEach {

  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/").withSession(SessionKeys.sessionId -> "fakesessionid")

  override def afterEach(): Unit = {
    val app: Application         = new GuiceApplicationBuilder().build()
    val repository: SessionCache = app.injector.instanceOf[SessionCache]
    running(app) {
      await(repository.deleteFromSession(DataKey("fakesessionid"))(fakeRequest))
    }
  }

  val testCacheData: FraudReportDetails = FraudReportDetails(
    Some(
      ActivityType(
        "22030000",
        "activityType.name.furlough",
        Seq("CJRS", "Furlough", "COVID", "Corona", "Coronavirus Job Retention Scheme")
      )
    )
  )

  "Session cache" should {

    "store, fetch and update activity type data correctly" in new Setup {
      running(app) {
        await(repository.store(testCacheData)(fakeRequest))
        val reportFromCache = await(repository.get()(fakeRequest))
        reportFromCache.get mustBe testCacheData
      }
    }

    "if cache present is not present in DB create new one" in new Setup {
      running(app) {
        val response = await(repository.createCacheIfNotPresent()(fakeRequest))
        response mustBe true
        val reportFromCache = await(repository.get()(fakeRequest))
        reportFromCache.get.activityType mustBe Option.empty
      }
    }

    "return None when testData requested and not available in cache" in new Setup {
      await(repository.createCacheIfNotPresent()(fakeRequest))
      val reportFromCache: Option[FraudReportDetails] = await(repository.get()(fakeRequest))
      reportFromCache.get mustBe FraudReportDetails(None)
    }

    "throw IllegalStateException when session id is not retrieved from hc" in new Setup {
      intercept[uk.gov.hmrc.taxfraudreportingfrontend.cache.SessionCacheException] {
        await(repository.get()(FakeRequest("GET", "/")))
      }
    }

  }

  trait Setup {
    val app: Application         = new GuiceApplicationBuilder().build()
    val repository: SessionCache = app.injector.instanceOf[SessionCache]
  }

}
