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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.FormError

class YourContactDetailsFormProviderSpec extends StringFieldBehaviours with ScalaCheckPropertyChecks {

  val form = new YourContactDetailsFormProvider()()

  ".firstName" - {

    val fieldName    = "firstName"
    val requiredKey  = "yourContactDetails.error.firstName.required"
    val lengthKey    = "yourContactDetails.error.firstName.length"
    val maxLength    = 255
    val regexPattern = "\\?|\\*"

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithUnacceptableCharacter(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(regexPattern))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".lastName" - {

    val fieldName    = "lastName"
    val requiredKey  = "yourContactDetails.error.lastName.required"
    val lengthKey    = "yourContactDetails.error.lastName.length"
    val maxLength    = 255
    val regexPattern = "\\?|\\*"

    behave like fieldThatBindsValidData(form, fieldName, stringsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithUnacceptableCharacter(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(regexPattern))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".tel" - {
    val fieldName = "tel"

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "test"))
      result.errors must contain(FormError(fieldName, "yourContactDetails.error.tel.invalid"))
    }

    "must not bind an empty map" in {
      val result = form.bind(Map.empty[String, String])
      result.errors must contain(FormError(fieldName, "yourContactDetails.error.tel.required"))
    }

  }

  ".email" - {
    val fieldName = "email"
    val maxLength = 255

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "yournamegmail.com"))
      result.errors must contain(FormError("email", "yourContactDetails.error.email.invalid"))
    }

    "must not bind when email is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, "yourContactDetails.error.email.length", Seq(maxLength)))
    }

  }

  ".memorableWord" - {

    val fieldName = "memorableWord"
    val lengthKey = "yourContactDetails.error.memorableWord.length"
    val maxLength = 255

    "must not bind when memorable word is longer than 255 characters" in {
      val result = form.bind(Map(fieldName -> "a" * 256))(fieldName)
      result.errors must contain(FormError(fieldName, lengthKey, Seq(maxLength)))
    }
  }

}
