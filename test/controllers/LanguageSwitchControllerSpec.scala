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

import base.SpecBase
import models.UserAnswers
import play.api.i18n.Lang
import play.api.test.Helpers.running

class LanguageSwitchControllerSpec extends SpecBase {

  private val userAnswers = UserAnswers(userAnswersId)

  "LanguageSwitch Controller" - {

    "fallbackURL and languageMap must be correct" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val controller = application.injector.instanceOf[LanguageSwitchController]
        controller.fallbackURL mustBe routes.IndexController.onPageLoad.url
        controller.languageMap mustBe Map("en" -> Lang("en"), "cy" -> Lang("cy"))
      }
    }
  }

}
