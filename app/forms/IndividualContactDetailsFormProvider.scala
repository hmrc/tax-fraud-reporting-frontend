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

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.IndividualContactDetails

class IndividualContactDetailsFormProvider @Inject() extends Mappings {

  private val errorPrefix = "individualContactDetails.error"

  private def field(key: String) = {
    def errorMsg(problem: String) = s"$errorPrefix.$key.$problem"

    text() verifying maxLength(255, errorMsg("length"))
  }

  def apply(): Form[IndividualContactDetails] = Form(
    mapping(
      "landlineNumber" -> optional(
        field("landlineNumber")
          .verifying(firstError(telephoneNumberValidation(errorPrefix + ".landlineNumber.invalid")))
      ),
      "mobileNumber" -> optional(
        field("mobileNumber")
          .verifying(firstError(telephoneNumberValidation(errorPrefix + ".mobileNumber.invalid")))
      ),
      "email" -> optional(field("email") verifying validEmailAddress(errorPrefix + ".email.invalid"))
    )(IndividualContactDetails.apply)(IndividualContactDetails.unapply)
  )

}
