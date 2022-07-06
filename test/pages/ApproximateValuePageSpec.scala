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

import models.UserAnswers
import pages.behaviours.PageBehaviours

class ApproximateValuePageSpec extends PageBehaviours {

  "ApproximateValuePage" - {

    beRetrievable[BigDecimal](ApproximateValuePage)

    beSettable[BigDecimal](ApproximateValuePage)

    beRemovable[BigDecimal](ApproximateValuePage)
  }

  "must remove approximate value when the user selects no" in {
    val answers        = UserAnswers("id").set(ApproximateValuePage, BigDecimal(0)).success.value
    val updatedAnswers = answers.set(ZeroValidationPage, false).success.value
    updatedAnswers.get(ZeroValidationPage) mustBe defined
  }
}
