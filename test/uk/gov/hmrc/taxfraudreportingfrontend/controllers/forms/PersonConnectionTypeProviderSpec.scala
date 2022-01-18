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
import uk.gov.hmrc.taxfraudreportingfrontend.controllers.forms.behaviours.OptionFieldBehaviours
import uk.gov.hmrc.taxfraudreportingfrontend.forms.PersonConnectionTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.models.PersonConnectionType

class PersonConnectionTypeProviderSpec extends OptionFieldBehaviours {

  val form = new PersonConnectionTypeProvider()()

  val radioFieldName = "value"

  "form value" must {

    val fieldName   = "value"
    val requiredKey = "selectConnection.error.required"

    behave like optionsField[PersonConnectionType](
      form,
      fieldName,
      validValues = PersonConnectionType.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

}