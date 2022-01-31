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

class SelectConnectionBusinessSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "SelectConnectionBusiness" - {

    "must deserialise" - {

      "CurrentEmployer" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "current-employer")
        ).asOpt.value mustEqual SelectConnectionBusiness.CurrentEmployer
      }

      "ExEmployer" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "ex-employer")
        ).asOpt.value mustEqual SelectConnectionBusiness.ExEmployer
      }

      "BusinessCompetitor" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "business-competitor")
        ).asOpt.value mustEqual SelectConnectionBusiness.BusinessCompetitor
      }

      "MyClient" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "my-client")
        ).asOpt.value mustEqual SelectConnectionBusiness.MyClient
      }

      "MySupplier" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "my-supplier")
        ).asOpt.value mustEqual SelectConnectionBusiness.MySupplier
      }

      "Customer" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "customer")
        ).asOpt.value mustEqual SelectConnectionBusiness.Customer
      }

      "Accountant" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "accountant")
        ).asOpt.value mustEqual SelectConnectionBusiness.Accountant
      }

      "Advisor" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "advisor")
        ).asOpt.value mustEqual SelectConnectionBusiness.Advisor
      }

      "Auditor" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "auditor")
        ).asOpt.value mustEqual SelectConnectionBusiness.Auditor
      }

      "Treasure" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "treasure")
        ).asOpt.value mustEqual SelectConnectionBusiness.Treasure
      }

      "Other" in {
        Json.fromJson[SelectConnectionBusiness](
          Json.obj("type" -> "other", "value" -> "something")
        ).asOpt.value mustEqual SelectConnectionBusiness.Other("something")
      }
    }

    "must fail to deserialise invalid values" in {
      JsString("foobar").validate[SelectConnectionBusiness].isError mustBe true
    }

    "must serialise" - {

      "CurrentEmployer" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.CurrentEmployer) mustEqual Json.obj(
          "type" -> "current-employer"
        )
      }

      "ExEmployer" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.ExEmployer) mustEqual Json.obj(
          "type" -> "ex-employer"
        )
      }

      "BusinessCompetitor" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.BusinessCompetitor) mustEqual Json.obj(
          "type" -> "business-competitor"
        )
      }

      "MyClient" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.MyClient) mustEqual Json.obj(
          "type" -> "my-client"
        )
      }

      "MySupplier" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.MySupplier) mustEqual Json.obj(
          "type" -> "my-supplier"
        )
      }

      "Customer" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.Customer) mustEqual Json.obj(
          "type" -> "customer"
        )
      }

      "Accountant" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.Accountant) mustEqual Json.obj(
          "type" -> "accountant"
        )
      }

      "Advisor" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.Advisor) mustEqual Json.obj("type" -> "advisor")
      }

      "Auditor" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.Auditor) mustEqual Json.obj("type" -> "auditor")
      }

      "Treasure" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.Treasure) mustEqual Json.obj(
          "type" -> "treasure"
        )
      }

      "Other" in {
        Json.toJson[SelectConnectionBusiness](SelectConnectionBusiness.Other("something")) mustEqual Json.obj(
          "type"  -> "other",
          "value" -> "something"
        )
      }
    }
  }
}
