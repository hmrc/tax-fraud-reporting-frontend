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

import models.{ProvideContactDetails, UserAnswers, YourContactDetails}
import pages.behaviours.PageBehaviours

class ProvideContactDetailsPageSpec extends PageBehaviours {

  "ProvideContactDetailsPage" - {

    beRetrievable[ProvideContactDetails](ProvideContactDetailsPage)

    beSettable[ProvideContactDetails](ProvideContactDetailsPage)

    beRemovable[ProvideContactDetails](ProvideContactDetailsPage)
  }

  "must remove contact details when the user selects no" in {
    val answers = UserAnswers("id").set(
      YourContactDetailsPage,
      YourContactDetails("firstname", "lastname", "tel", Some("email"), "test")
    ).success.value
    val updatedAnswers = answers.set(ProvideContactDetailsPage, ProvideContactDetails.No).success.value
    updatedAnswers.get(YourContactDetailsPage) mustNot be(defined)
  }

  "must not remove contact details when the user selects yes" in {
    val answers = UserAnswers("id").set(
      YourContactDetailsPage,
      YourContactDetails("firstname", "lastname", "tel", Some("email"), "test")
    ).success.value
    val updatedAnswers = answers.set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
    updatedAnswers.get(YourContactDetailsPage).value mustBe YourContactDetails(
      "firstname",
      "lastname",
      "tel",
      Some("email"),
      "test"
    )
  }

}
