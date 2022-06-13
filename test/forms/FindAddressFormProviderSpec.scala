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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class FindAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new FindAddressFormProvider()()

  ".Postcode" - {
    val fieldName  = "Postcode"
    val ukPostCode = "^([A-Za-z][A-Ha-hJ-Yj-y]?[0-9][A-Za-z0-9]? ?[0-9][A-Za-z]{2}|[Gg][Ii][Rr] ?0[Aa]{2})$"

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "invalid-postcode"))
      result.errors must contain(FormError("Postcode", "findAddress.error.postcode.invalid", Seq(ukPostCode)))
    }
    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "findAddress.error.postcode.required")
    )
  }

  ".Property" - {

    val fieldName = "Property"
    val lengthKey = "findAddress.error.property.length"
    val maxLength = 255

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

  }
}
