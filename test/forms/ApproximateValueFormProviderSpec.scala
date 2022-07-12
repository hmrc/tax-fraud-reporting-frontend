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

import forms.behaviours.IntFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class ApproximateValueFormProviderSpec extends IntFieldBehaviours {

  val form = new ApproximateValueFormProvider()()

  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(maxDiscardedFactor = 15)

  ".value" - {

    val fieldName     = "value"
    val required      = "approximateValue.error.required"
    val max12Chars    = "approximateValue.error.length"
    val max2Decimals  = "approximateValue.error.maxTwoDecimals"
    val nonNumericKey = "approximateValue.error.nonNumeric"

    "must not bind an empty map" in {
      val result = form.bind(Map.empty[String, String])
      result.errors must contain(FormError(fieldName, required))
    }

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "12abc.90"))
      result.errors must contain(FormError(fieldName, nonNumericKey))
    }

    "must not report errors for value with" - {

      def assertValidValue(decimalsGen: Gen[String]) =
        forAll(decimalsGen -> "decimal") {
          decimal =>
            val result = form.bind(Map(fieldName -> decimal)).apply(fieldName)
            result.errors mustBe empty
        }

      "no decimals, but integer of length less than 12" in {
        val intGen = intsInRangeWithCommas(0, (Math.pow(10, 12) - 1).intValue())
        assertValidValue(intGen)
      }
      "decimals with 1 digit and total characters(including '.') less than 12" in {
        val decimalsGen: Gen[String] = decimalsWithCommasAndSpaces(maxIntegralDigits = 10, fractions = 1)
        assertValidValue(decimalsGen)
      }
      "decimals with 2 digits and total characters(including '.') less than 12" in {
        val decimalsGen: Gen[String] = decimalsWithCommasAndSpaces(maxIntegralDigits = 9, fractions = 2)
        assertValidValue(decimalsGen)
      }
    }

    "must report errors for value with more than" - {
      def assertInvalidValue(decimalsGen: Gen[String], errorMesg: String) =
        forAll(decimalsGen -> "decimal") {
          decimal =>
            val result = form.bind(Map(fieldName -> decimal)).apply(fieldName)
            result.errors must contain(FormError(fieldName, errorMesg))
        }

      "2 decimals" in {
        val moreThan2decimalsGen = decimalsWithCommasAndSpaces(maxIntegralDigits = 15, fractions = 3)
        assertInvalidValue(moreThan2decimalsGen, max2Decimals)
      }
      "12 characters(including '.')" in {
        val decimalsGen = decimalsWithCommasAndSpaces(minIntegralDigits = 10, maxIntegralDigits = 50, fractions = 2)
        assertInvalidValue(decimalsGen, max12Chars)
      }
    }
  }
}
