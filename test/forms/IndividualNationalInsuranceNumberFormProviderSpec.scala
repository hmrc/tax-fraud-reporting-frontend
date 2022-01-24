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
import org.scalacheck.Gen
import play.api.data.FormError

class IndividualNationalInsuranceNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "individualNationalInsuranceNumber.error.required"
  val lengthKey   = "individualNationalInsuranceNumber.error.length"
  val maxLength   = 13

  val form = new IndividualNationalInsuranceNumberFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(form, fieldName, Gen.oneOf("AA123456C", "AA 12 45 56 C"))

    // TODO should this be mandatory?
    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must fail to bind an invalid national insurance number" in {
      val result = form.bind(Map(fieldName -> "abc"))(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, "individualNationalInsuranceNumber.error.invalid"))
    }
  }
}
