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

import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc.{ControllerComponents, Cookie, Cookies}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig

class LanguageSwitchControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {
  val cc: ControllerComponents = app.injector.instanceOf[ControllerComponents]
  val langUtils: LanguageUtils = app.injector.instanceOf[LanguageUtils]

  private val conf = app.injector.instanceOf[Configuration]
  private val sc   = app.injector.instanceOf[ServicesConfig]

  def testLanguageSelection(language: String, expectedCookieValue: String, welshEnabled: Boolean = true): Unit = {
    object TestConfig extends AppConfig(conf, sc) {
      override val welshLanguageSupportEnabled: Boolean = welshEnabled
    }

    object TestLanguageSwitchController extends LanguageSwitchController(TestConfig, langUtils, cc)

    val request                = FakeRequest()
    val result                 = TestLanguageSwitchController.switchToLanguage(language)(request)
    val resultCookies: Cookies = cookies(result)
    resultCookies.size mustBe 1
    val cookie: Cookie = resultCookies.head
    cookie.name mustBe "PLAY_LANG"
    cookie.value mustBe expectedCookieValue
  }

  "Hitting language selection endpoint" should {

    "redirect to English translated start page if English language is selected" in {
      testLanguageSelection("en", "en")
    }

    "redirect to Welsh translated start page if Welsh language is selected" in {
      testLanguageSelection("cy", "cy")
    }

    "redirect to English translated start page if Welsh language is disabled" in {
      testLanguageSelection("cy", "en", welshEnabled = false)
    }
  }
}
