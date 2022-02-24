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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ReferenceNumbersFormProviderSpec extends StringFieldBehaviours {

  val form = new ReferenceNumbersFormProvider()()

  ".vatRegistration" - {

    val fieldName = "vatRegistration"
    val lengthKey = "referenceNumbers.error.vatRegistration.length"

    val result = form.bind(Map(fieldName -> "ab123456789"))(fieldName)
    result.errors mustEqual Seq(FormError(fieldName, lengthKey, Seq(Validation.vatRegistration)))

  }

  ".employeeRefNo" - {

    val fieldName = "employeeRefNo"
    val lengthKey = "referenceNumbers.error.employeeRefNo.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

  ".corporationTax" - {

    val fieldName = "corporationTax"
    val lengthKey = "referenceNumbers.error.corporationTax.length"

    val result = form.bind(Map(fieldName -> "a123456789"))(fieldName)
    result.errors mustEqual Seq(FormError(fieldName, lengthKey, Seq(Validation.ctutrValidation)))
  }

}
