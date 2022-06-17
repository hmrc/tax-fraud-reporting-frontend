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

package controllers

import akka.actor.ActorSystem
import auditing.AuditAndAnalyticsEventDispatcher
import controllers.actions.{DataRequiredActionImpl, DataRetrievalActionImpl, SessionIdentifierAction}
import controllers.helper.EventHelper
import models.backend.Address
import models.requests.DataRequest
import models.{Index, NormalMode, UserAnswers}
import navigation.Navigator
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.MockitoSugar
import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{
  defaultAwaitTimeout,
  status,
  stubMessagesApi,
  stubMessagesControllerComponents,
  stubPlayBodyParsers
}
import play.twirl.api.Html
import repositories.SessionRepository
import services.ActivityTypeService
import uk.gov.hmrc.http.HttpVerbs.POST
import uk.gov.hmrc.http.SessionKeys
import views.html.AddressView

import java.nio.charset.Charset
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class BusinessAddressControllerSpec extends AnyFlatSpec with MockitoSugar with ScalaFutures {

  private val mockAddressView = mock[AddressView]
  private val testHtml        = "testHtml"
  private val mockEventHelper = mock[EventHelper]
  when {
    mockAddressView.apply(any(), any(), any(), any(), any())(any(), any())
  } thenReturn Html(testHtml)

  private val mcc                                   = stubMessagesControllerComponents
  private implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  private implicit val as: ActorSystem              = ActorSystem()

  private val mockId = "mockId"

  private def scenario(userAnswers: JsObject)(test: (JsObject, BusinessAddressController) => Assertion) = {
    val mockSessionRepository = mock[SessionRepository]
    when {
      mockSessionRepository get anyString()
    } thenReturn
      Future.successful(Some(UserAnswers(mockId, Json.obj("nominals" -> Seq(userAnswers)))))

    when {
      mockSessionRepository set any()
    } thenReturn Future.successful(true)

    val businessAddressController = new BusinessAddressController(
      stubMessagesApi(),
      mockSessionRepository,
      new Navigator(mock[ActivityTypeService]),
      new SessionIdentifierAction(new BodyParsers.Default(stubPlayBodyParsers)),
      new DataRetrievalActionImpl(mockSessionRepository),
      new DataRequiredActionImpl(),
      mcc,
      mockAddressView,
      mockEventHelper
    )

    test(userAnswers, businessAddressController)
  }

  private def fakeDataRequest(userAnswers: JsObject = Json.obj(), bodyOpt: Option[Seq[(String, String)]] = None) = {
    val fakeRequest = bodyOpt match {
      case Some(body) => FakeRequest(POST, "").withFormUrlEncodedBody(body: _*)
      case None       => FakeRequest()
    }

    DataRequest(fakeRequest.withSession(SessionKeys.sessionId -> "fakeSessionId"), "", UserAnswers(mockId, userAnswers))
  }

  "BusinessAddressController" should "respond with status 200 given a request without a cached answer" in
    scenario(Json.obj("businessSelectCountry" -> "gb")) {
      (_, controller) =>
        val response = controller.onPageLoad(Index(0), NormalMode)(fakeDataRequest())

        status(response) shouldBe OK
        val responseBody = response.futureValue.body.consumeData.futureValue
        responseBody decodeString Charset.defaultCharset() shouldBe "testHtml"
    }

  it should "respond with status 200 given a request with a cached answer" in
    scenario(
      Json.obj(
        "businessSelectCountry" -> "gb",
        "businessAddress" -> Address(
          addressLine1 = "221b Baker St",
          townCity = "London",
          postcode = Some("NW1 6XE"),
          country = "gb"
        )
      )
    ) {
      (userAnswers, controller) =>
        val requestWithCache =
          fakeDataRequest(Json.obj("nominals" -> Seq(userAnswers)))

        val response = controller.onPageLoad(Index(0), NormalMode)(requestWithCache)

        status(response) shouldBe OK
        val responseBody = response.futureValue.body.consumeData.futureValue
        responseBody decodeString Charset.defaultCharset() shouldBe "testHtml"
    }

  it should "response with status 400 and remain on the same page given invalid data" in
    scenario(Json.obj("businessSelectCountry" -> "gb")) {
      (_, controller) =>
        val badRequest = fakeDataRequest(bodyOpt = Some(Seq("postCode" -> "mockPostCode")))

        val response = controller.onSubmit(Index(0), NormalMode)(badRequest)

        status(response) shouldBe BAD_REQUEST
        val responseBody = response.futureValue.body.consumeData.futureValue
        responseBody decodeString Charset.defaultCharset shouldBe "testHtml"
    }

  it should "response with status 303 given valid data" in
    scenario(Json.obj()) {
      (_, controller) =>
        val goodRequest =
          fakeDataRequest(bodyOpt = Some(Seq("line1" -> "mockLine1", "townOrCity" -> "London", "country" -> "gb")))

        val response = controller.onSubmit(Index(0), NormalMode)(goodRequest)

        status(response) shouldBe SEE_OTHER
    }
}
