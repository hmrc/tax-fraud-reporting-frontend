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

import java.time.{LocalDate, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class IndividualDateOfBirthFormProviderSpec(implicit messages: Messages) extends DateBehaviours {

  val form = new IndividualDateOfBirthFormProvider()()

  ".value" - {

    behave like dateFieldWithMax(
      form,
      "value",
      LocalDate.now(ZoneOffset.UTC),
      FormError("value", "individualDateOfBirth.error.futureDate")
    )

    behave like mandatoryDateField(form, "value", "individualDateOfBirth.error.required.all")
  }
}
