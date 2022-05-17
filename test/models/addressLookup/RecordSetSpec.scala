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

package models.addressLookup

import generators.PafFixtures
import models.addresslookup.RecordSet
import play.api.libs.json.Json

import scala.io.Source
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class RecordSetSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with PafFixtures {

  "calling fromJsonAddressLookupService" - {
    "filter out all addresses with addressLines missing" in {
      val addressesWithMissingLinesJson: String =
        Source
          .fromInputStream(getClass.getResourceAsStream("/address-lookup/recordSetWithMissingAddressLines.json"))
          .mkString
      val result = RecordSet.fromJsonAddressLookupService(Json.parse(addressesWithMissingLinesJson))

      result mustBe twoOtherPlaceRecordSet
    }

    "return all addresses with where the addressLines are present" in {
      val addressesWhichAllContainLinesJson: String =
        Source.fromInputStream(getClass.getResourceAsStream("/address-lookup/recordSet.json")).mkString
      val result = RecordSet.fromJsonAddressLookupService(Json.parse(addressesWhichAllContainLinesJson))

      result mustBe oneAndTwoOtherPlacePafRecordSet
    }
  }
}