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

package uk.gov.hmrc.taxfraudreportingfrontend.util

import akka.stream.Materializer
import akka.stream.testkit.NoMaterializer
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.{DefaultFileMimeTypes, FileMimeTypesConfiguration}
import play.api.i18n.Lang._
import play.api.i18n.{Messages, MessagesApi, MessagesImpl}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{inject, Application, Configuration, Environment}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{SessionCache, UserAnswersCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig

import java.util.UUID
import scala.concurrent.ExecutionContext.global
import scala.util.Random

trait BaseSpec extends WordSpec with MockitoSugar with Matchers with Injector {

  implicit val messagesApi: MessagesApi = instanceOf[MessagesApi]

  implicit def materializer: Materializer = NoMaterializer

  implicit val messages: Messages = MessagesImpl(defaultLang, messagesApi)

  implicit val mcc: MessagesControllerComponents = DefaultMessagesControllerComponents(
    new DefaultMessagesActionBuilderImpl(stubBodyParser(AnyContentAsEmpty), messagesApi)(global),
    DefaultActionBuilder(stubBodyParser(AnyContentAsEmpty))(global),
    stubPlayBodyParsers(NoMaterializer),
    messagesApi, // Need to be a real messages api, because our tests checks the content, not keys
    stubLangs(),
    new DefaultFileMimeTypes(FileMimeTypesConfiguration()),
    global
  )

  protected val previousPageUrl = "javascript:history.back()"

  val env: Environment                 = Environment.simple()
  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val config: Configuration            = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(config)

  val appConfig: AppConfig = new AppConfig(config, serviceConfig)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "")

  def postRequest(data: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest("POST", "").withFormUrlEncodedBody(data: _*)

  val defaultUserId: String = s"user-${UUID.randomUUID}"

  val mockSessionCache: SessionCache         = mock[SessionCache]
  val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]
  val hc: HeaderCarrier                      = mock[HeaderCarrier]

  def oversizedString(maxLength: Int): String = Random.alphanumeric.take(maxLength + 1).mkString

  def undersizedString(minLength: Int): String = Random.alphanumeric.take(minLength - 1).mkString

  def application: Application = new GuiceApplicationBuilder().overrides(
    inject.bind[SessionCache].to(mockSessionCache),
    inject.bind[UserAnswersCache].to(mockUserAnswersCache)
  ).configure("auditing.enabled" -> "false", "metrics.jvm" -> false, "metrics.enabled" -> false).build()

  private def addToken[T](fakeRequest: FakeRequest[T])(implicit app: Application) =
    fakeRequest.withSession(SessionKeys.sessionId -> "fakesessionid")

  def EnhancedFakeRequest(method: String, uri: String)(implicit app: Application): FakeRequest[AnyContentAsEmpty.type] =
    addToken(FakeRequest(method, uri))

}
