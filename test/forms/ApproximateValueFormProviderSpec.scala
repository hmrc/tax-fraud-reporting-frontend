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
import play.api.data.FormError

class ApproximateValueFormProviderSpec extends IntFieldBehaviours {

  val form = new ApproximateValueFormProvider()()

  private val currencyRegex =
    "^[',\",\\+,<,>,\\(,\\*,\\-,%]?([£]?\\s*\\d+([\\,,\\.]\\d+)?[£]?\\s*[\\-,\\/,\\,,\\.,\\+]?[\\/]?\\s*)+[',\",\\+,   <,>,\\),\\*,\\-,%]?$"

  ".value" - {

    val fieldName = "value"
    val required  = "approximateValue.error.required"

    "must not bind an empty map" in {
      val result = form.bind(Map.empty[String, String])
      result.errors must contain(FormError(fieldName, required))
    }

    "must not bind an invalid option" in {
      val result = form.bind(Map(fieldName -> "49,000.90ab"))
      result.errors must contain(FormError(fieldName, required, Seq(currencyRegex)))

    }
  }
}
