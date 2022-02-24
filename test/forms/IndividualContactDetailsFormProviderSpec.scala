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

class IndividualContactDetailsFormProviderSpec extends StringFieldBehaviours {

  private val form = new IndividualContactDetailsFormProvider()()

  ".landlineNumber" - {

    val fieldName  = "landlineNumber"
    val invalidKey = "individualContactDetails.error.landlineNumber.invalid"

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "test"))
      result.errors must contain(FormError(fieldName, invalidKey))
    }

  }

  ".mobileNumber" - {

    val fieldName  = "mobileNumber"
    val invalidKey = "individualContactDetails.error.mobileNumber.invalid"

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "test"))
      result.errors must contain(FormError(fieldName, invalidKey))
    }

  }

  ".email" - {

    val fieldName  = "email"
    val lengthKey  = "individualContactDetails.error.email.length"
    val invalidKey = "individualContactDetails.error.email.invalid"
    val maxLength  = 255

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "name?example.com"))
      result.errors must contain(FormError(fieldName, invalidKey))
    }

    "must not bind when email address is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, lengthKey, Seq(maxLength)))
    }
  }
}
