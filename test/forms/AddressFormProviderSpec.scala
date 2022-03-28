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
import org.scalatest.OptionValues
import play.api.data.FormError

class AddressFormProviderSpec extends StringFieldBehaviours with OptionValues {
  private val maxLen      = 255
  private val longLine    = "a" * (maxLen + 1)
  private val errorPrefix = "address.error"
  private val line1Error  = "address.error.line1.length"

  private val form = (new AddressFormProvider)()

  ".line1" - {
    val fieldName = "line1"

    "must not bind when address line1 is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.line1.length"))
    }

  }

  ".line2" - {
    val fieldName = "line2"

    "must not bind when address line1 is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.line2.length"))
    }

  }
  ".line3" - {
    val fieldName = "line3"

    "must not bind when address line3 is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.line3.length"))
    }

  }
  ".townOrCity" - {
    val fieldName = "townOrCity"

    "must not bind when address townOrCity is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "address.error.townOrCity.length"))
    }

  }

  "return an error when country is GB and postcode invalid" in {
    val addressWithBadPostcode =
      Map("line1" -> "221B Baker St", "postCode" -> "aaa aaa", "country" -> "gb")
    val Left(bindingErrors) = form.mapping bind addressWithBadPostcode

    bindingErrors mustBe Seq(FormError("postCode", "error.postcode.invalid"))
  }

  "return a length error when country not GB and postcode exceeds $maxLen chars" in {
    val addressWithLongPostcode =
      Map("line1" -> "221B Baker St", "postCode" -> longLine, "country" -> "aa")
    val Left(bindingErrors) = form.mapping bind addressWithLongPostcode

    bindingErrors mustBe Seq(FormError("postCode", "error.length"))
  }
}
