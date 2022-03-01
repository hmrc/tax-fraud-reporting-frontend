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

package pages

import models.{ActivityTimePeriod, UserAnswers, WhenActivityHappen}
import pages.behaviours.PageBehaviours

class WhenActivityHappenPageSpec extends PageBehaviours {

  "WhenActivityHappenPage" - {

    beRetrievable[WhenActivityHappen](WhenActivityHappenPage)

    beSettable[WhenActivityHappen](WhenActivityHappenPage)

    beRemovable[WhenActivityHappen](WhenActivityHappenPage)
  }

  "must remove activity likely happen when the user selects over 5years" in {
    val answers        = UserAnswers("id").set(ActivityTimePeriodPage, ActivityTimePeriod.DoNotKnow).success.value
    val updatedAnswers = answers.set(WhenActivityHappenPage, WhenActivityHappen.NotHappen).success.value
    updatedAnswers.get(ActivityTimePeriodPage) mustNot be(defined)
  }

}
