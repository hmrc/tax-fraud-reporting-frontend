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
import models.SelectConnectionBusiness
import play.api.data.Forms.tuple
import uk.gov.voa.play.form.ConditionalMappings._

class SelectConnectionBusinessFormProvider @Inject() extends Mappings {

  def apply(): Form[SelectConnectionBusiness] =
    Form(
      tuple(
        "value" -> text("selectConnectionBusiness.error.required")
          .verifying(
            "selectConnectionBusiness.error.invalid",
            value => SelectConnectionBusiness.enumerableValues.exists(_.toString == value) || value == "other"
          ),
        "otherValue" -> mandatoryIfEqual(
          "value",
          "other",
          text("selectConnectionBusiness.error.otherValue.required")
            .verifying(maxLength(100, "selectConnectionBusiness.error.otherValue.maxLength"))
        )
      )
        .transform[SelectConnectionBusiness](
          {
            case ("other", Some(value)) => SelectConnectionBusiness.Other(value)
            case (key, _)               => SelectConnectionBusiness.enumerableValues.find(_.toString == key).get
          },
          {
            case SelectConnectionBusiness.Other(value) => ("other", Some(value))
            case other                                 => (other.toString, None)
          }
        )
    )

}
