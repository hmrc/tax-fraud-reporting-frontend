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
import models.YourContactDetails
import play.api.data.Form
import play.api.data.Forms._

import javax.inject.Inject

class YourContactDetailsFormProvider @Inject() extends Mappings {

  val emailPattern                     = "^(.+@.+\\..+)?$"
  val phoneValidation ="\\s*((?:[48+][- ]?)?(?:\\(?\\d{3}\\)?[- ]?)?[\\d -]{7,10})"

  def apply(): Form[YourContactDetails] = Form(
    mapping(
      "firstName" -> text("yourContactDetails.error.firstName.required")
        .verifying(maxLength(255, "yourContactDetails.error.firstName.length")),
      "lastName" -> text("yourContactDetails.error.lastName.required")
        .verifying(maxLength(255, "yourContactDetails.error.lastName.length")),
      "tel" -> text("yourContactDetails.error.tel.required")
        .verifying(maxLength(255, "yourContactDetails.error.tel.length"))
      .verifying(regexp(phoneValidation, "yourContactDetails.error.tel.invalid")),
      "email" -> optional(text().verifying(maxLength(255, "yourContactDetails.error.email.length"))
        .verifying(regexp(emailPattern, "yourContactDetails.error.email.invalid"))),
      "memorableWord" -> text("yourContactDetails.error.memorableWord.required")
        .verifying(maxLength(255, "yourContactDetails.error.memorableWord.length"))
    )(YourContactDetails.apply)(YourContactDetails.unapply)
  )

}
