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

package uk.gov.hmrc.taxfraudreportingfrontend.controllers.forms

import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatest.{Matchers, OptionValues, WordSpec}
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualContactProvider
import uk.gov.hmrc.taxfraudreportingfrontend.models.IndividualContact
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

class IndividualContactProviderSpec extends WordSpec with Matchers with BaseSpec with OptionValues {

  val form = new IndividualContactProvider()

  "form" should {

    "return no errors with valid data" in {
      val data =
        Map("landlineNumber" -> "123", "mobileNumber" -> "456", "emailAddress" -> "joe@example.com")
      val result = form().bind(data)
      result.hasErrors mustEqual false
      result.get mustEqual IndividualContact(Some("123"), Some("456"), Some("joe@example.com"))
    }

    "accept input if only landlineNumber is present" in {
      List("abc", "1" * 15).foreach { value =>
        val data   = Map("landlineNumber" -> value)
        val result = form().bind(data)
        result.hasErrors mustEqual false
        result.get mustEqual IndividualContact(Some(value), None, None)
      }
    }

    "accept input if only mobileNumber is present" in {
      List("abc", "1" * 15).foreach { value =>
        val data   = Map("mobileNumber" -> value)
        val result = form().bind(data)
        result.hasErrors mustEqual false
        result.get mustEqual IndividualContact(None, Some(value), None)
      }
    }

    "accept input if only emailAddress is present" in {
      val data   = Map("emailAddress" -> "joe@example.com")
      val result = form().bind(data)
      result.hasErrors mustEqual false
      result.get mustEqual IndividualContact(None, None, Some("joe@example.com"))
    }

    "return errors with invalid data" in {

      val data = Map("landlineNumber" -> "1" * 16, "mobileNumber" -> "2" * 16, "emailAddress" -> "joe.com")

      val boundForm = form().bind(data)
      val errors    = boundForm.errors
      errors.length mustEqual 3

      boundForm("landlineNumber").error.value.message mustEqual "individualContact.error.landline_Number.invalid"
      boundForm("mobileNumber").error.value.message mustEqual "individualContact.error.mobile_Number.invalid"
      boundForm("emailAddress").error.value.message mustEqual "individualContact.error.email_Address.invalid"
    }

    "error if landline number is invalid" in {
      val data      = Map("landlineNumber" -> "1" * 16)
      val boundForm = form().bind(data)
      boundForm.errors.length mustEqual 1
      boundForm("landlineNumber").error.value.message mustEqual "individualContact.error.landline_Number.invalid"
    }

    "error if mobile number is invalid" in {
      val data      = Map("mobileNumber" -> "1" * 16)
      val boundForm = form().bind(data)
      boundForm.errors.length mustEqual 1
      boundForm("mobileNumber").error.value.message mustEqual "individualContact.error.mobile_Number.invalid"
    }

    "error if email address is invalid" in {
      val data      = Map("emailAddress" -> "&*joe.com")
      val boundForm = form().bind(data)
      boundForm.errors.length mustEqual 1
      boundForm("emailAddress").error.value.message mustEqual "individualContact.error.email_Address.invalid"
    }

    "error if there are no fields present" in {
      val boundForm = form().bind(Map.empty[String, String])
      boundForm.errors.length mustEqual 1
      boundForm("").error.value.message mustEqual "individualContact.error.required"
    }
  }
}
