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

import forms.behaviours.OptionFieldBehaviours
import play.api.data.FormError
import models.ActivitySourceOfInformation

class ActivitySourceOfInformationFormProviderSpec extends OptionFieldBehaviours {

  val form = new ActivitySourceOfInformationFormProvider()()

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "activitySourceOfInformation.error.required"
    val maxLength   = 100

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must bind successfully" - {

      "ReportedIndividuals" in {
        form.bind(Map(fieldName -> "reportedIndividuals")).get mustEqual ActivitySourceOfInformation.ReportedIndividuals
      }

      "InformationInLocalArea" in {
        form.bind(
          Map(fieldName -> "informationInLocalArea")
        ).get mustEqual ActivitySourceOfInformation.InformationInLocalArea
      }

      "ObservedTheActivity" in {
        form.bind(Map(fieldName -> "observedTheActivity")).get mustEqual ActivitySourceOfInformation.ObservedTheActivity
      }

      "OverheardTheActivity" in {
        form.bind(
          Map(fieldName -> "overheardTheActivity")
        ).get mustEqual ActivitySourceOfInformation.OverheardTheActivity
      }

      "SpeculatedThisActivity" in {
        form.bind(
          Map(fieldName -> "speculatedThisActivity")
        ).get mustEqual ActivitySourceOfInformation.SpeculatedThisActivity
      }

      "ReportedByIndividual" in {
        form.bind(
          Map(fieldName -> "reportedByIndividual")
        ).get mustEqual ActivitySourceOfInformation.ReportedByIndividual
      }

      "Other" in {
        form.bind(
          Map(fieldName -> "other", "otherValue" -> "something")
        ).get mustEqual ActivitySourceOfInformation.Other("something")
      }
    }

    "must fail to bind invalid values" in {
      form.bind(Map(fieldName -> "testabcdef")).errors mustEqual Seq(
        FormError(fieldName, "activitySourceOfInformation.error.invalid")
      )
    }

    "must fail to bind Other if there is no extra value" in {
      form.bind(Map(fieldName -> "other")).errors mustEqual Seq(
        FormError("otherValue", "activitySourceOfInformation.error.otherValue.required")
      )
    }

    "must fail to bind Other if the other value is longer than 100 characters" in {
      form.bind(Map(fieldName -> "other", "otherValue" -> "test" * 101)).errors mustEqual Seq(
        FormError("otherValue", "activitySourceOfInformation.error.otherValue.maxLength", Seq(maxLength))
      )
    }

  }
}
