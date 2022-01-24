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
import models.IndividualConnection
import play.api.data.FormError

class IndividualConnectionFormProviderSpec extends OptionFieldBehaviours {

  val form = new IndividualConnectionFormProvider()()

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "individualConnection.error.required"

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "must bind successfully" - {

      "Partner" in {
        form.bind(Map(fieldName -> "partner")).get mustEqual IndividualConnection.Partner
      }

      "ExPartner" in {
        form.bind(Map(fieldName -> "exPartner")).get mustEqual IndividualConnection.ExPartner
      }

      "FamilyMember" in {
        form.bind(Map(fieldName -> "familyMember")).get mustEqual IndividualConnection.FamilyMember
      }

      "BusinessPartner" in {
        form.bind(Map(fieldName -> "businessPartner")).get mustEqual IndividualConnection.BusinessPartner
      }

      "Employer" in {
        form.bind(Map(fieldName -> "employer")).get mustEqual IndividualConnection.Employer
      }

      "ExEmployer" in {
        form.bind(Map(fieldName -> "exEmployer")).get mustEqual IndividualConnection.ExEmployer
      }

      "Employee" in {
        form.bind(Map(fieldName -> "employee")).get mustEqual IndividualConnection.Employee
      }

      "Colleague" in {
        form.bind(Map(fieldName -> "colleague")).get mustEqual IndividualConnection.Colleague
      }

      "Friend" in {
        form.bind(Map(fieldName -> "friend")).get mustEqual IndividualConnection.Friend
      }

      "Neighbour" in {
        form.bind(Map(fieldName -> "neighbour")).get mustEqual IndividualConnection.Neighbour
      }

      "Customer" in {
        form.bind(Map(fieldName -> "customer")).get mustEqual IndividualConnection.Customer
      }

      "BusinessCompetitor" in {
        form.bind(Map(fieldName -> "businessCompetitor")).get mustEqual IndividualConnection.BusinessCompetitor
      }

      "Other" in {
        form.bind(Map(fieldName -> "other", "otherValue" -> "something")).get mustEqual IndividualConnection.Other(
          "something"
        )
      }
    }

    "must fail to bind invalid values" in {
      form.bind(Map(fieldName -> "asdfasdf")).errors mustEqual Seq(
        FormError(fieldName, "individualConnection.error.invalid")
      )
    }

    "must fail to bind Other if there is no extra value" in {
      form.bind(Map(fieldName -> "other")).errors mustEqual Seq(
        FormError("otherValue", "individualConnection.error.otherValue.required")
      )
    }

    "must fail to bind Other if the other value is longer than 100 characters" in {
      form.bind(Map(fieldName -> "other", "otherValue" -> "a" * 101)).errors mustEqual Seq(
        FormError("otherValue", "individualConnection.error.otherValue.maxLength", Seq(100))
      )
    }
  }
}
