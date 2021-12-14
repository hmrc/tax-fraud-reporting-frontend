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

import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json.toJson
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.cache.model.{Cache, Id}
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.mongo.{MongoConnector, MongoSpecSupport}
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{CachedData, SessionCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.models.cache.FraudReportDetails

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class SessionCacheSpec extends IntegrationTestSpec with MockitoSugar with MongoSpecSupport {

  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  private val reactiveMongoComponent = new ReactiveMongoComponent {
    override def mongoConnector: MongoConnector = mongoConnectorForTest
  }

  val sessionCache = new SessionCache(appConfig, reactiveMongoComponent)

  val hc: HeaderCarrier = mock[HeaderCarrier]

  val testCacheData: FraudReportDetails = FraudReportDetails(activityType = Some("activityType"))

  "Session cache" should {

    "store, fetch and update activity type data correctly" in {
      val sessionId: SessionId = setupSession

      await(sessionCache.saveFraudReportDetails(testCacheData)(hc))

      val expectedJson                     = toJson(CachedData(fraudReportDetails = Some(testCacheData)))
      val cache                            = await(sessionCache.findById(Id(sessionId.value)))
      val Some(Cache(_, Some(json), _, _)) = cache
      json mustBe expectedJson

      await(sessionCache.fraudReportDetails(hc)) mustBe testCacheData

      val updatedTest = FraudReportDetails(activityType = Some("updatedActivityType"))

      await(sessionCache.saveFraudReportDetails(updatedTest)(hc))

      val expectedUpdatedJson                     = toJson(CachedData(fraudReportDetails = Some(updatedTest)))
      val updatedCache                            = await(sessionCache.findById(Id(sessionId.value)))
      val Some(Cache(_, Some(updatedJson), _, _)) = updatedCache
      updatedJson mustBe expectedUpdatedJson
    }

    "remove from the cache" in {
      val sessionId: SessionId = setupSession
      await(sessionCache.saveFraudReportDetails(FraudReportDetails(activityType = Some("activityType")))(hc))

      await(sessionCache.remove(hc))

      val cached = await(sessionCache.findById(Id(sessionId.value)))
      cached mustBe None
    }

    "is cache present in DB" in {
      await(sessionCache.saveFraudReportDetails(FraudReportDetails(activityType = Some("activityType")))(hc))

      await(sessionCache.isCachePresent(hc.sessionId.get.value)) mustBe true

      await(sessionCache.remove(hc))

      await(sessionCache.isCachePresent(hc.sessionId.get.value)) mustBe false
    }

    "if cache present is not present in DB create new one" in {
      await(sessionCache.remove(hc))

      await(sessionCache.isCacheNotPresentCreateOne(hc.sessionId.get.value)(hc)) mustBe FraudReportDetails(None)

    }

    "return None when testData requested and not available in cache" in {
      val s = setupSession
      await(sessionCache.insert(Cache(Id(s.value), data = Some(toJson(CachedData())))))
      await(sessionCache.fraudReportDetails(hc)) mustBe FraudReportDetails(None)
    }

    "throw IllegalStateException when session id is not retrieved from hc" in {
      when(hc.sessionId).thenReturn(None)

      val e1 = intercept[IllegalStateException] {
        await(sessionCache.fraudReportDetails(hc))
      }
      e1.getMessage mustBe "Session id is not available"
    }

  }

  private def setupSession: SessionId = {
    val sessionId = SessionId("sessionId-" + UUID.randomUUID())
    when(hc.sessionId).thenReturn(Some(sessionId))
    sessionId
  }

}
