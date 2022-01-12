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

import play.api.data.Forms.mapping
import play.api.data.{FieldMapping, Form}
import play.api.i18n.Messages
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualContact

import javax.inject.Inject
import scala.language.postfixOps

class IndividualContactProvider @Inject() extends Mappings {

  private def phoneConstraint(mapping: FieldMapping[String], field: String) =
    mapping.verifying(regexp(Validation.phoneNumberRegexPattern, s"individualContact.error.$field.invalid"))

  private def emailConstraint(mapping: FieldMapping[String], field: String) =
    mapping.verifying(regexp(Validation.emailPattern, s"individualContact.error.$field.invalid"))

  private def atLeastOneContact(message: String = "") =
    atLeastOneOf("landlineNumber,mobileNumber,emailAddress" split ',', message)

  def apply()(implicit messages: Messages): Form[IndividualContact] =
    Form(
      mapping(
        "landlineNumber" -> phoneConstraint(
          atLeastOneContact(messages(s"individualContact.error.required")),
          "landline_Number"
        ),
        "mobileNumber" -> phoneConstraint(atLeastOneContact(), "mobile_Number"),
        "emailAddress" -> emailConstraint(atLeastOneContact(), "email_Address")
      )(IndividualContact.apply)(IndividualContact.unapply).verifying(
        messages(s"individualContact.error.required"),
        individualContact =>
          Seq(
            individualContact.landline_Number,
            individualContact.mobile_Number,
            individualContact.email_Address
          ) flatMap { _.trim } nonEmpty
      )
    )

}
