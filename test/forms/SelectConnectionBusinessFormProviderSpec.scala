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

import forms.behaviours.{OptionFieldBehaviours, StringFieldBehaviours}
import models.SelectConnectionBusiness
import play.api.data.FormError

class SelectConnectionBusinessFormProviderSpec extends OptionFieldBehaviours with StringFieldBehaviours {

  private val form = new SelectConnectionBusinessFormProvider()()

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "selectConnectionBusiness.error.required"
    val maxLength   = 255

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must bind successfully" - {

      "CurrentEmployer" in {
        form.bind(Map(fieldName -> "current-employer")).get mustEqual SelectConnectionBusiness.CurrentEmployer
      }

      "ExEmployer" in {
        form.bind(Map(fieldName -> "ex-employer")).get mustEqual SelectConnectionBusiness.ExEmployer
      }

      "BusinessCompetitor" in {
        form.bind(Map(fieldName -> "business-competitor")).get mustEqual SelectConnectionBusiness.BusinessCompetitor
      }

      "MyClient" in {
        form.bind(Map(fieldName -> "my-client")).get mustEqual SelectConnectionBusiness.MyClient
      }

      "MySupplier" in {
        form.bind(Map(fieldName -> "my-supplier")).get mustEqual SelectConnectionBusiness.MySupplier
      }

      "Customer" in {
        form.bind(Map(fieldName -> "customer")).get mustEqual SelectConnectionBusiness.Customer
      }

      "Accountant" in {
        form.bind(Map(fieldName -> "accountant")).get mustEqual SelectConnectionBusiness.Accountant
      }

      "Advisor" in {
        form.bind(Map(fieldName -> "advisor")).get mustEqual SelectConnectionBusiness.Advisor
      }

      "Auditor" in {
        form.bind(Map(fieldName -> "auditor")).get mustEqual SelectConnectionBusiness.Auditor
      }

      "Treasure" in {
        form.bind(Map(fieldName -> "treasure")).get mustEqual SelectConnectionBusiness.Treasure
      }

      "Other" in {
        form.bind(Map(fieldName -> "other", "otherValue" -> "something")).get mustEqual SelectConnectionBusiness.Other(
          "something"
        )
      }
    }

    "must fail to bind invalid values" in {
      form.bind(Map(fieldName -> "asdfasdf")).errors mustEqual Seq(
        FormError(fieldName, "selectConnectionBusiness.error.invalid")
      )
    }

    "must fail to bind Other if there is no extra value" in {
      form.bind(Map(fieldName -> "other")).errors mustEqual Seq(
        FormError("otherValue", "selectConnectionBusiness.error.otherValue.required")
      )
    }

    "must fail to bind Other if the other value is longer than 255 characters" in {
      form.bind(Map(fieldName -> "other", "otherValue" -> "test" * 256)).errors mustEqual Seq(
        FormError("otherValue", "selectConnectionBusiness.error.otherValue.maxLength", Seq(maxLength))
      )
    }

  }
}
