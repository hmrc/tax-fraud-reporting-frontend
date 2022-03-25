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

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.FormError

class AddressFormProviderSpec extends AnyFlatSpec with OptionValues {
  private val maxLen   = 255
  private val longLine = "a" * (maxLen + 1)

  private val form = (new AddressFormProvider)()

  "AddressFormProvider" must s"return length errors when fields exceed $maxLen characters" in {
    val lineFields = Seq("line1", "line2", "line3", "townOrCity")
    val addressWithLongLines =
      Map("line1" -> longLine, "line2" -> longLine, "line3" -> longLine, "townOrCity" -> longLine, "country" -> "gb")
    val Left(bindingErrors) = form.mapping bind addressWithLongLines

    lineFields foreach { field =>
      assert(bindingErrors contains FormError(field, "error.length"))
    }
  }

  "AddressFormProvider" must "return an error when country is GB and postcode invalid" in {
    val addressWithBadPostcode =
      Map("line1" -> "221B Baker St", "postCode" -> "aaa aaa", "country" -> "gb")
    val Left(bindingErrors) = form.mapping bind addressWithBadPostcode

    bindingErrors mustBe Seq(FormError("postCode", "error.postcode.invalid"))
  }

  "AddressFormProvider" must s"return a length error when country not GB and postcode exceeds $maxLen chars" in {
    val addressWithLongPostcode =
      Map("line1" -> "221B Baker St", "postCode" -> longLine, "country" -> "aa")
    val Left(bindingErrors) = form.mapping bind addressWithLongPostcode

    bindingErrors mustBe Seq(FormError("postCode", "error.length"))
  }
}
