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
import play.api.data.Form
import uk.gov.hmrc.domain.Nino.isValid

import javax.inject.Inject

class IndividualNationalInsuranceNumberFormProvider @Inject() extends Mappings {

  def removeWhitespace(string: String): String = string.split("\\s+").mkString

  def apply(): Form[String] =
    Form(
      "value" -> text("individualNationalInsuranceNumber.error.required")
        .verifying(
          "individualNationalInsuranceNumber.error.invalid",
          value => isValid(removeWhitespace(value.toUpperCase))
        )
    )

}
