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

package uk.gov.hmrc.taxfraudreportingfrontend.controllers.forms

import play.api.data.FormError
import uk.gov.hmrc.taxfraudreportingfrontend.controllers.forms.behaviours.CheckboxFieldBehaviours
import uk.gov.hmrc.taxfraudreportingfrontend.forms.BusinessInformationCheckProvider
import uk.gov.hmrc.taxfraudreportingfrontend.models.BusinessInformationCheck

class BusinessInformationCheckProviderSpec extends CheckboxFieldBehaviours {

  val form = new BusinessInformationCheckProvider()()

  "business information check value" must {

    val fieldName   = "value"
    val requiredKey = "businessInformationCheck.error.required"

    behave like checkboxField[BusinessInformationCheck](
      form,
      fieldName,
      validValues = BusinessInformationCheck.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(form, fieldName, requiredKey)
  }

}
