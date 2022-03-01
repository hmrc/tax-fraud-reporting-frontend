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

import models.{Index, IndividualDateFormat, UserAnswers}
import pages.behaviours.PageBehaviours

class IndividualAgePageSpec extends PageBehaviours {

  "IndividualAgePage" - {

    beRetrievable[Int](IndividualAgePage(Index(0)))

    beSettable[Int](IndividualAgePage(Index(0)))

    beRemovable[Int](IndividualAgePage(Index(0)))
  }

  "must remove individual date Of birth page when the user selects approximate age" in {
    val answers        = UserAnswers("id").set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Date).success.value
    val updatedAnswers = answers.set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Age).success.value
    updatedAnswers.get(IndividualDateOfBirthPage(Index(0))) mustNot be(defined)
  }

}
