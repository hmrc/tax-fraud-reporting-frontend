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
import models.addresslookup.{Address, Country}
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class classAddressModelSpec
    extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with PafFixtures {

  "calling hasAddressLines" - {
    "return true where the address has at least one line" in {
      val addressLines = List("some line")
      Address(addressLines, None, None, "Some Postcode", None, Country("UK", "United Kingdom")).isValid mustBe true
    }

    "return false where the address has no lines" in {
      val noAddressLines = List()
      Address(noAddressLines, None, None, "Some Postcode", None, Country("UK", "United Kingdom")).isValid mustBe false
    }

    "return false where the address two lines" in {
      val noAddressLines = List("line 1", "line 2")
      Address(noAddressLines, None, None, "Some Postcode", None, Country("UK", "United Kingdom")).isValid mustBe true
    }
  }
}
