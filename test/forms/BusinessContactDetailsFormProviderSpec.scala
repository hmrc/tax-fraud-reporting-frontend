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

class BusinessContactDetailsFormProviderSpec extends StringFieldBehaviours {

  private val form = new BusinessContactDetailsFormProvider()()

  ".landlineNumber" - {

    val fieldName = "landlineNumber"
    val lengthKey = "businessContactDetails.error.landlineNumber.length"
    val maxLength = 11

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

  ".mobileNumber" - {

    val fieldName = "mobileNumber"
    val lengthKey = "businessContactDetails.error.mobileNumber.length"
    val maxLength = 11

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

  ".email" - {

    val fieldName = "email"
    val lengthKey = "businessContactDetails.error.email.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }
}
