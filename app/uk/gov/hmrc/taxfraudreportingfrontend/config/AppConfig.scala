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

package uk.gov.hmrc.taxfraudreportingfrontend.config

import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration

@Singleton
class AppConfig @Inject() (config: Configuration, servicesConfig: ServicesConfig) {

  val appName: String     = config.get[String]("appName")
  private val contactHost = config.get[String]("contact-frontend.host")

  val welshLanguageSupportEnabled: Boolean =
    config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  lazy val feedbackUrl: String = config.get[String]("feedback.url")

  val en: String            = "en"
  val cy: String            = "cy"
  val defaultLanguage: Lang = Lang(en)

  lazy val assetsPrefix = config.get[String](s"assets.url") + config
    .get[String](s"assets.version") + '/'

  val cacheTtl: Duration = Duration.create(config.get[String]("tax-fraud-reporting-cache.ttl"))

}
