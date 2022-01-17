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

package uk.gov.hmrc.taxfraudreportingfrontend.cache

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import org.scalatest.BeforeAndAfterEach
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualInformationCheck.{Address, Age, Contact, NINO, Name}
import uk.gov.hmrc.taxfraudreportingfrontend.models.PersonConnectionType.partner
import uk.gov.hmrc.taxfraudreportingfrontend.models._
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

  private val mockIndividualInformationCheck: Set[IndividualInformationCheck] = Set(Name, Age, Address, Contact, NINO)

  private val mockIndividualName = IndividualName("Joe", "Edd", "Bloggs", "Blog")

  private val mockIndividualContact = IndividualContact(Some("123"), Some("456"), Some("joe@example.com"))

  private val mockNino = IndividualNino("AB 12 34 56 C")

  private val mockPersonConnection = ConnectionType(PersonConnectionType.values.head, Option("otherConnection"))

  protected override def beforeEach: Unit = {
    reset(mockSessionCache)

    when(mockSessionCache.saveFraudReportDetails(any[FraudReportDetails])(any[Request[_]]))
      .thenReturn(Future.successful(FraudReportDetails(Some(mockActivityType))))

    when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
      Future.successful(FraudReportDetails(Some(mockActivityType)))
    )

  }

  "Calling userAnswersCache" should {
    "save activityType Details in frontend cache" in {

      Await.result(testCache.cacheActivityType(mockActivityType)(getRequest), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(getRequest))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.activityType.get.code shouldBe "22030000"

      val testCacheData: Option[ActivityType] = Await.result(testCache.getActivityType()(getRequest), Duration.Inf)
      testCacheData.get.code shouldBe "22030000"

    }

    "get empty activityType Details from cache" in {

      when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Option[ActivityType] = Await.result(testCache.getActivityType()(getRequest), Duration.Inf)
      testCacheData shouldBe None
    }

    "save individual information check Details in frontend cache" in {

      Await.result(testCache.cacheIndividualInformationCheck(mockIndividualInformationCheck)(getRequest), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(getRequest))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.individualInformationCheck shouldBe Set(Name, Age, Address, Contact, NINO)

      val testCacheData: Set[IndividualInformationCheck] =
        Await.result(testCache.getIndividualInformationCheck()(getRequest), Duration.Inf)
      testCacheData shouldBe Set.empty

    }

    "get empty individual information check Details from cache" in {

      when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Set[IndividualInformationCheck] =
        Await.result(testCache.getIndividualInformationCheck()(getRequest), Duration.Inf)
      testCacheData shouldBe Set.empty
    }

    "save individual name details in frontend cache" in {

      Await.result(testCache.cacheIndividualName(Some(mockIndividualName))(getRequest), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(getRequest))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.individualName.get.forename shouldBe "Joe"

      val testCacheData: Option[IndividualName] =
        Await.result(testCache.getIndividualName()(getRequest), Duration.Inf)
      testCacheData shouldBe None

    }

    "get empty individual name details from cache" in {

      when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Option[IndividualName] =
        Await.result(testCache.getIndividualName()(getRequest), Duration.Inf)
      testCacheData shouldBe None
    }

    "save individual contact details in frontend cache" in {

      Await.result(testCache.cacheIndividualContact(Some(mockIndividualContact))(getRequest), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(getRequest))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.individualContact.get.email_Address shouldBe Some("joe@example.com")

      val testCacheData: Option[IndividualContact] =
        Await.result(testCache.getIndividualContact()(getRequest), Duration.Inf)
      testCacheData shouldBe None

    }

    "get empty individual contact details from cache" in {

      when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Option[IndividualContact] =
        Await.result(testCache.getIndividualContact()(getRequest), Duration.Inf)
      testCacheData shouldBe None
    }

    "save nino details in frontend cache" in {

      Await.result(testCache.cacheNino(Some(mockNino))(getRequest), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(getRequest))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.individualNino.get.Nino shouldBe "AB 12 34 56 C"

      val testCacheData: Option[IndividualNino] =
        Await.result(testCache.getNino()(getRequest), Duration.Inf)
      testCacheData shouldBe None

    }

    "get empty nino details from cache" in {

      when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Option[IndividualNino] =
        Await.result(testCache.getNino()(getRequest), Duration.Inf)
      testCacheData shouldBe None
    }

    "save person connect type data in frontend cache" in {

      Await.result(testCache.cacheConnection(Some(mockPersonConnection))(getRequest), Duration.Inf)
      val requestCaptor = ArgumentCaptor.forClass(classOf[FraudReportDetails])

      verify(mockSessionCache).saveFraudReportDetails(requestCaptor.capture())(ArgumentMatchers.eq(getRequest))
      val holder: FraudReportDetails = requestCaptor.getValue
      holder.connectionType.get.personConnectionType shouldBe partner

      val testCacheData: Option[ConnectionType] =
        Await.result(testCache.getConnection()(getRequest), Duration.Inf)
      testCacheData shouldBe None

    }

    "get person connect type data from cache" in {

      when(mockSessionCache.getFraudReportDetails(any[Request[_]])).thenReturn(
        Future.successful(FraudReportDetails(None))
      )

      val testCacheData: Option[ConnectionType] =
        Await.result(testCache.getConnection()(getRequest), Duration.Inf)
      testCacheData shouldBe None
    }

  }
}
