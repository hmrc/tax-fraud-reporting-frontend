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

class AddressFormProviderSpec extends StringFieldBehaviours {
  private val maxLen       = 255
  private val longLine     = "a" * (maxLen + 1)
  private val regexPattern = "(\\?+|\\*+)+"
  private val requiredKey  = "error.address.line1"
  private val lengthKey    = "address.error.line1.length"

  val form = AddressFormProvider.get(isCountryUK = true)
  //private val form = (new AddressFormProvider)()

  ".line1" - {
    val fieldName = "line1"

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLen))

    "must not bind when address line1 is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, lengthKey, Seq(maxLen)))
    }

    behave like fieldWithUnacceptableCharacter(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(regexPattern))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

  }

  ".line2" - {
    val fieldName = "line2"

    "must not bind when address line2 is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.line2.length", Seq(maxLen)))
    }

  }
  ".line3" - {
    val fieldName = "line3"

    "must not bind when address line3 is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.line3.length", Seq(maxLen)))
    }

  }
  ".townOrCity" - {
    val fieldName = "townOrCity"

    "must not bind when address townOrCity is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.townOrCity.length", Seq(maxLen)))
    }

    behave like fieldWithUnacceptableCharacter(
      form,
      fieldName,
      requiredError = FormError(fieldName, "error.address.townOrCity", Seq(regexPattern))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, "error.address.townOrCity"))

  }

  "return an error when country is GB and postcode invalid" in {
    val addressWithBadPostcode =
      Map("line1" -> "221B Baker St", "postCode" -> "aaa aaa", "townOrCity" -> "London", "country" -> "gb")
    val Left(bindingErrors) = form.mapping bind addressWithBadPostcode

    bindingErrors mustBe Seq(FormError("postCode", "error.postcode.invalid"))
  }

  "return a length error when country not GB and postcode exceeds maxLen chars" in {
    val addressWithLongPostcode =
      Map("line1" -> "221B Baker St", "townOrCity" -> "London", "postCode" -> longLine, "country" -> "aa")
    val Left(bindingErrors) = form.mapping bind addressWithLongPostcode

    bindingErrors mustBe Seq(FormError("postCode", "error.postcode.invalid"))
  }
}
