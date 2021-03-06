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
import models.IndividualConnection
import play.api.data.Form
import play.api.data.Forms.tuple
import uk.gov.voa.play.form.ConditionalMappings._

import javax.inject.Inject

class IndividualConnectionFormProvider @Inject() extends Mappings {

  def apply(): Form[IndividualConnection] =
    Form(
      tuple(
        "value" -> text("individualConnection.error.required")
          .verifying(
            "individualConnection.error.invalid",
            value => IndividualConnection.enumerableValues.exists(_.toString == value) || value == "other"
          ),
        "otherValue" -> mandatoryIfEqual(
          "value",
          "other",
          text("individualConnection.error.otherValue.required")
            .verifying(maxLength(100, "individualConnection.error.otherValue.maxLength"))
            .verifying(
              regexpRestrict(Validation.validString.toString, "individualConnection.error.otherValue.required")
            )
        )
      )
        .transform[IndividualConnection](
          {
            case ("other", Some(value)) => IndividualConnection.Other(value)
            case (key, _)               => IndividualConnection.enumerableValues.find(_.toString == key).get
          },
          {
            case IndividualConnection.Other(value) => ("other", Some(value))
            case other                             => (other.toString, None)
          }
        )
    )

}
