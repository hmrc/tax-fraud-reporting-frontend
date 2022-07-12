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
import models.FindAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class BusinessFindAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[FindAddress] = Form(
    mapping(
      "Postcode" -> text("businessFindAddress.error.required").verifying(postcodeConstraint)
        .verifying(maxLength(255, "businessFindAddress.error.length")),
      "Property" -> optional(text().verifying(maxLength(255, "businessFindAddress.error.property.length")))
    )(FindAddress.apply)(FindAddress.unapply)
  )

  /*def apply(): Form[String] =
    Form(
      "value" -> text("businessFindAddress.error.required")
        .verifying(maxLength(100, "businessFindAddress.error.length"))
    )*/

}
