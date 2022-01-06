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
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualInformationCheckProvider
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualInformationCheck

class IndividualInformationCheckProviderSpec extends CheckboxFieldBehaviours {

  val form = new IndividualInformationCheckProvider()()

  "individual informationCheck value" must {

    val fieldName   = "value"
    val requiredKey = "individual.informationCheck.error.required"

    behave like checkboxField[IndividualInformationCheck](
      form,
      fieldName,
      validValues = IndividualInformationCheck.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(form, fieldName, requiredKey)
  }

}
