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

class ActivitySourceOfInformationSpec
    extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "ActivitySourceOfInformation" - {

    "must deserialise valid values" - {

      "ReportedIndividuals" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "reportedIndividuals")
        ).asOpt.value mustEqual ActivitySourceOfInformation.ReportedIndividuals
      }

      "InformationInLocalArea" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "informationInLocalArea")
        ).asOpt.value mustEqual ActivitySourceOfInformation.InformationInLocalArea
      }

      "ObservedTheActivity" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "observedTheActivity")
        ).asOpt.value mustEqual ActivitySourceOfInformation.ObservedTheActivity
      }

      "OverheardTheActivity" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "overheardTheActivity")
        ).asOpt.value mustEqual ActivitySourceOfInformation.OverheardTheActivity
      }

      "SpeculatedThisActivity" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "speculatedThisActivity")
        ).asOpt.value mustEqual ActivitySourceOfInformation.SpeculatedThisActivity
      }

      "ReportedByIndividual" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "reportedByIndividual")
        ).asOpt.value mustEqual ActivitySourceOfInformation.ReportedByIndividual
      }

      "ByThirdPart" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "byThirdPart")
        ).asOpt.value mustEqual ActivitySourceOfInformation.ByThirdPart
      }

      "Other" in {
        Json.fromJson[ActivitySourceOfInformation](
          Json.obj("type" -> "other", "value" -> "something")
        ).asOpt.value mustEqual ActivitySourceOfInformation.Other("something")
      }

    }

    "must fail to deserialise invalid values" in {

      JsString("foobar").validate[ActivitySourceOfInformation].isError mustBe true
    }

    "must serialise" - {

      "ReportedIndividuals" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.ReportedIndividuals) mustEqual Json.obj(
          "type" -> "reportedIndividuals"
        )
      }

      "InformationInLocalArea" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.InformationInLocalArea) mustEqual Json.obj(
          "type" -> "informationInLocalArea"
        )
      }

      "ObservedTheActivity" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.ObservedTheActivity) mustEqual Json.obj(
          "type" -> "observedTheActivity"
        )
      }

      "OverheardTheActivity" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.OverheardTheActivity) mustEqual Json.obj(
          "type" -> "overheardTheActivity"
        )
      }

      "SpeculatedThisActivity" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.SpeculatedThisActivity) mustEqual Json.obj(
          "type" -> "speculatedThisActivity"
        )
      }

      "ReportedByIndividual" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.ReportedByIndividual) mustEqual Json.obj(
          "type" -> "reportedByIndividual"
        )
      }

      "ByThirdPart" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.ByThirdPart) mustEqual Json.obj(
          "type" -> "byThirdPart"
        )
      }

      "Other" in {
        Json.toJson[ActivitySourceOfInformation](ActivitySourceOfInformation.Other("something")) mustEqual Json.obj(
          "type"  -> "other",
          "value" -> "something"
        )
      }
    }
  }
}
