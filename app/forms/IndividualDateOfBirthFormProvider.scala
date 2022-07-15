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

package forms

import forms.mappings.Mappings
import play.api.data.Form
import play.api.i18n.Messages

import java.time.{LocalDate, ZoneOffset}
import javax.inject.Inject

class IndividualDateOfBirthFormProvider @Inject() extends Mappings {

  val minDateLimit = LocalDate.parse("1900-01-01")

  def apply()(implicit messages: Messages): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "individualDateOfBirth.error.invalid",
        allRequiredKey = "individualDateOfBirth.error.required.all",
        twoRequiredKey = "individualDateOfBirth.error.required.two",
        requiredKey = "individualDateOfBirth.error.required"
      ).verifying(maxDate(LocalDate.now(ZoneOffset.UTC), "individualDateOfBirth.error.futureDate"))
        .verifying(minDate(minDateLimit, "individualDateOfBirth.error.pastDate", minDateLimit))
    )

}
