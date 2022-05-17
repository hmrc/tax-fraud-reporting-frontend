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
import models.AddressSansCountry
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import scala.language.postfixOps

object AddressFormProvider extends Mappings {
  private val maxLen = 255

  def get(isCountryUK: Boolean): Form[AddressSansCountry] = {
    val pattern = "(?i)[A-Z]{1,2}[0-9R][0-9A-Z]?\\s*[0-9][ABD-HJLNP-UW-Z]{2}"

    val (postcodeError, postcodeConstraint) =
      if (isCountryUK) "error.postcode.invalid" -> { (_: String) matches pattern }
      else "address.error.postcode.length"      -> { (_: String).length <= maxLen }

    Form(
      mapping(
        "line1" -> text("error.address.line1").verifying(maxLength(maxLen, "address.error.line1.length"))
          .verifying(regexpRestrict(Validation.validString.toString, "error.address.line1")),
        "line2" -> optional(text().verifying(maxLength(maxLen, "address.error.line2.length"))),
        "line3" -> optional(text().verifying(maxLength(maxLen, "address.error.line3.length"))),
        "townOrCity" -> text("error.address.townOrCity").verifying(maxLength(maxLen, "address.error.townOrCity.length"))
          .verifying(regexpRestrict(Validation.validString.toString, "error.address.townOrCity")),
        "postCode" -> optional(text().verifying(postcodeError, postcodeConstraint))
      )(AddressSansCountry.apply)(AddressSansCountry.unapply)
    )

  }

}
