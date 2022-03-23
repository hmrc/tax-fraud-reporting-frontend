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

import base.MockActivityTypes
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

// TODO tests
class ActivityTypeFormProviderSpec extends StringFieldBehaviours with MockActivityTypes {

  ".value" in {

    val requiredKey = "error.required"

    val form = new ActivityTypeFormProvider(mockActivityTypeService)()

    val fieldName = "value"

      val result = form.bind(emptyForm).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, requiredKey))

  }
}
