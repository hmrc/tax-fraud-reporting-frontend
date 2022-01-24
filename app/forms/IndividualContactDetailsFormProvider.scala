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

   def apply(): Form[IndividualContactDetails] = Form(
     mapping(
      "landlineNumber" -> optional(text().verifying(maxLength(100, "individualContactDetails.error.landlineNumber.length"))),
      "mobileNumber" -> optional(text().verifying(maxLength(100, "individualContactDetails.error.mobileNumber.length"))),
       "email" -> optional(text().verifying(maxLength(100, "individualContactDetails.error.email.length")))
    )(IndividualContactDetails.apply)(IndividualContactDetails.unapply)
   )
 }
