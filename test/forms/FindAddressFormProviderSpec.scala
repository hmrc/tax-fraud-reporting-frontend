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
import forms.mappings.Mappings
import play.api.data.FormError

class FindAddressFormProviderSpec extends StringFieldBehaviours with Mappings {

  val form = new FindAddressFormProvider()()

  ".Postcode" - {
    val fieldName = "Postcode"

    "must not bind an invalid postcode" in {
      val result = form.bind(Map(fieldName -> "postcode"))
      result.errors must contain(FormError("Postcode", "findAddress.error.postcode.invalid"))
    }

    "must not bind an invalid character" in {
      val result = form.bind(Map(fieldName -> "BB#$ 0BB"))
      result.errors must contain(FormError("Postcode", "findAddress.error.postcode.invalidChar"))
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
