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

class ReferenceNumbersFormProviderSpec extends StringFieldBehaviours {

  val form = new ReferenceNumbersFormProvider()()

  ".vatRegistration" - {

    val fieldName = "vatRegistration"

    val validData = for {
      prefix <- Gen.oneOf("GB", "gb", "")
      digits <- Gen.listOfN(9, Gen.numChar).map(_.mkString)
    } yield prefix + digits

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validData
    )

    "bind values with spaces" in {

      val result = form.bind(Map(fieldName -> "GB 123 456 789")).apply(fieldName)
      result.value.get mustBe "GB 123 456 789"
      result.errors mustBe empty
    }
  }

  ".employeeRefNo" - {

    val fieldName = "employeeRefNo"

    val validData = for {
      firstDigits <- Gen.listOfN(3, Gen.alphaChar).map(_.mkString)
      numDigits   <- Gen.choose(1, 10)
      lastChars   <- Gen.listOfN(numDigits, Gen.alphaNumChar)
    } yield s"$firstDigits/$lastChars"

    "bind values with spaces" in {

      val result = form.bind(Map(fieldName -> "123 / ABC 123 45")).apply(fieldName)
      result.value.get mustBe "123 / ABC 123 45"
      result.errors mustBe empty
    }
  }

  ".corporationTax" - {

    val fieldName = "corporationTax"

    val validData = for {
      digits <- Gen.listOfN(10, Gen.numChar)
    } yield digits.mkString

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validData
    )

    "bind values with spaces" in {

      val result = form.bind(Map(fieldName -> "12 34 56 78 90")).apply(fieldName)
      result.value.get mustBe "12 34 56 78 90"
      result.errors mustBe empty
    }

  }

}
