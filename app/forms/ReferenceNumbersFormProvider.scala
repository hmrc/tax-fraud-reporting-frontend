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
import models.ReferenceNumbers
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class ReferenceNumbersFormProvider @Inject() extends Mappings {

  def trimWhitespace(string: String): String = string.split("\\s+").mkString

  def apply(): Form[ReferenceNumbers] = Form(
    mapping(
      "vatRegistration" -> optional(
        text().transform[String](trimWhitespace(_).toUpperCase, identity)
          .verifying(
            regexp(Validation.vatRegistrationPattern.toString, "referenceNumbers.error.vatRegistration.length")
          )
      ),
      "employeeRefNo" -> optional(
        text().transform[String](trimWhitespace, value => value)
          .verifying(regexp(Validation.payeReferencePattern.toString, "referenceNumbers.error.employeeRefNo.invalid"))
      ),
      "corporationTax" -> optional(
        text().transform[String](trimWhitespace, value => value)
          .verifying(regexp(Validation.utrPattern.toString, "referenceNumbers.error.corporationTax.length"))
      )
    )(ReferenceNumbers.apply)(ReferenceNumbers.unapply)
  )

}
