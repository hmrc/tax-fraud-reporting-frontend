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
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualName

import javax.inject.Inject
import scala.language.postfixOps

class IndividualNameProvider @Inject() extends Mappings {
  private val safeInputPattern = "^(|[a-zA-Z][a-zA-Z0-9 / '-]+)$?"

  private def nameConstraint(mapping: FieldMapping[String], field: String) =
    mapping.verifying(regexp(safeInputPattern, s"individualName.error.$field.invalid"))

  private def atLeastOneName(message: String = "") =
    atLeastOneOf("firstName,lastName,middleName,nickName" split ',', message)

  def apply()(implicit messages: Messages): Form[IndividualName] =
    Form(
      mapping(
        "firstName"  -> nameConstraint(atLeastOneName(messages(s"individualName.error.required")), "forename"),
        "middleName" -> nameConstraint(atLeastOneName(), "middle_Name"),
        "lastName"   -> nameConstraint(atLeastOneName(), "surname"),
        "nickName"   -> nameConstraint(atLeastOneName(), "alias")
      )(IndividualName.apply)(IndividualName.unapply).verifying(
        messages(s"individualName.error.required"),
        individualName =>
          Seq(
            individualName.forename,
            individualName.surname,
            individualName.middle_Name,
            individualName.alias
          ) flatMap { _.trim } nonEmpty
      )
    )

}
