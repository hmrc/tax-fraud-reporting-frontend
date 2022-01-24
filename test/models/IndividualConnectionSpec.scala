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

package models

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsString, Json}

class IndividualConnectionSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "IndividualConnection" - {

    "must deserialise" - {

      "Partner" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "partner")).asOpt.value mustEqual IndividualConnection.Partner
      }

      "ExPartner" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "exPartner")).asOpt.value mustEqual IndividualConnection.ExPartner
      }

      "FamilyMember" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "familyMember")).asOpt.value mustEqual IndividualConnection.FamilyMember
      }

      "BusinessPartner" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "businessPartner")).asOpt.value mustEqual IndividualConnection.BusinessPartner
      }

      "Employer" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "employer")).asOpt.value mustEqual IndividualConnection.Employer
      }

      "ExEmployer" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "exEmployer")).asOpt.value mustEqual IndividualConnection.ExEmployer
      }

      "Employee" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "employee")).asOpt.value mustEqual IndividualConnection.Employee
      }

      "Colleague" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "colleague")).asOpt.value mustEqual IndividualConnection.Colleague
      }

      "Friend" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "friend")).asOpt.value mustEqual IndividualConnection.Friend
      }

      "Neighbour" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "neighbour")).asOpt.value mustEqual IndividualConnection.Neighbour
      }

      "Customer" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "customer")).asOpt.value mustEqual IndividualConnection.Customer
      }

      "BusinessCompetitor" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "businessCompetitor")).asOpt.value mustEqual IndividualConnection.BusinessCompetitor
      }

      "Other" in {
        Json.fromJson[IndividualConnection](Json.obj("type" -> "other", "value" -> "something")).asOpt.value mustEqual IndividualConnection.Other("something")
      }
    }

    "must fail to deserialise invalid values" in {
      JsString("foobar").validate[IndividualConnection].isError mustBe true
    }

    "must serialise" - {

      "Partner" in {
        Json.toJson[IndividualConnection](IndividualConnection.Partner) mustEqual Json.obj("type" -> "partner")
      }

      "ExPartner" in {
        Json.toJson[IndividualConnection](IndividualConnection.ExPartner) mustEqual Json.obj("type" -> "exPartner")
      }

      "FamilyMember" in {
        Json.toJson[IndividualConnection](IndividualConnection.FamilyMember) mustEqual Json.obj("type" -> "familyMember")
      }

      "BusinessPartner" in {
        Json.toJson[IndividualConnection](IndividualConnection.BusinessPartner) mustEqual Json.obj("type" -> "businessPartner")
      }

      "Employer" in {
        Json.toJson[IndividualConnection](IndividualConnection.Employer) mustEqual Json.obj("type" -> "employer")
      }

      "ExEmployer" in {
        Json.toJson[IndividualConnection](IndividualConnection.ExEmployer) mustEqual Json.obj("type" -> "exEmployer")
      }

      "Employee" in {
        Json.toJson[IndividualConnection](IndividualConnection.Employee) mustEqual Json.obj("type" -> "employee")
      }

      "Colleague" in {
        Json.toJson[IndividualConnection](IndividualConnection.Colleague) mustEqual Json.obj("type" -> "colleague")
      }

      "Friend" in {
        Json.toJson[IndividualConnection](IndividualConnection.Friend) mustEqual Json.obj("type" -> "friend")
      }

      "Neighbour" in {
        Json.toJson[IndividualConnection](IndividualConnection.Neighbour) mustEqual Json.obj("type" -> "neighbour")
      }

      "Customer" in {
        Json.toJson[IndividualConnection](IndividualConnection.Customer) mustEqual Json.obj("type" -> "customer")
      }

      "BusinessCompetitor" in {
        Json.toJson[IndividualConnection](IndividualConnection.BusinessCompetitor) mustEqual Json.obj("type" -> "businessCompetitor")
      }

      "Other" in {
        Json.toJson[IndividualConnection](IndividualConnection.Other("something")) mustEqual Json.obj("type" -> "other", "value" -> "something")
      }
    }
  }
}
