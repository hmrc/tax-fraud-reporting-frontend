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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URLEncoder
import scala.util.Try

@Singleton
class FrontendAppConfig @Inject() (servicesConfig: ServicesConfig, configuration: Configuration) {

  private def loadConfig(key: String): String =
    Try(servicesConfig.getString(key)).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  val host: String    = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")

  private val contactHost                  = configuration.get[String]("contact-frontend.host")
  private val feedbackHost                 = configuration.get[String]("feedback-frontend.host")
  private val contactFormServiceIdentifier = "tax-fraud-reporting-frontend"

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$feedbackHost/feedback/beta-feedback?service=$contactFormServiceIdentifier&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  def contactFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback-unauthenticated??service=$contactFormServiceIdentifier&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  val loginUrl: String         = configuration.get[String]("urls.login")
  val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String       = configuration.get[String]("urls.signOut")

  private val exitSurveyBaseUrl: String = configuration.get[Service]("microservice.services.feedback-frontend").baseUrl
  val exitSurveyUrl: String             = s"$exitSurveyBaseUrl/feedback/tax-fraud-reporting-frontend"

  val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  def languageMap: Map[String, Lang] = Map("en" -> Lang("en"), "cy" -> Lang("cy"))

  val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val accessibilityStatementPath: String = loadConfig("accessibility-statement.service-path")

  lazy val accessibilityStatementFrontendBaseUrl: String = configuration.getOptional[String]("accessibility-statement.host").getOrElse("")
  def accessibilityFooterUrl(referrerUrl: Option[String] = None): String = {
    val referrerString = referrerUrl.map (url => s"""?referrerUrl=${URLEncoder.encode(url, "UTF-8")}""").getOrElse("")
    s"$accessibilityStatementFrontendBaseUrl/accessibility-statement$accessibilityStatementPath$referrerString"
  }
}
