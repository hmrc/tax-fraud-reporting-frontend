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

package uk.gov.hmrc.taxfraudreportingfrontend.forms

import play.api.data.Form
import play.api.data.Forms.{mapping, optional}
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualContact

import javax.inject.Inject

class IndividualContactProvider @Inject() extends Mappings {

  def apply(): Form[IndividualContact] =
    Form(
      mapping(
        "landlineNumber" -> optional(
          text().verifying("individualContact.error.landline_Number.invalid", _.trim.length <= 15)
        ),
        "mobileNumber" -> optional(
          text().verifying("individualContact.error.mobile_Number.invalid", _.trim.length <= 15)
        ),
        "emailAddress" -> optional(
          text().verifying(regexp(Validation.emailPattern, "individualContact.error.email_Address.invalid"))
        )
      )(IndividualContact.apply)(IndividualContact.unapply)
        .verifying(
          "individualContact.error.required",
          individualContact =>
            List(
              individualContact.landline_Number,
              individualContact.mobile_Number,
              individualContact.email_Address
            ).flatten.nonEmpty
        )
    )

}
