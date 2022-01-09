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
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualContactProvider
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

class IndividualContactProviderSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with BaseSpec {

  val form = new IndividualContactProvider()

  "form" should {

    "return no errors with valid data" in {
      val data =
        Map("landlineNumber" -> "01632960001", "mobileNumber" -> "07700900982", "emailAddress" -> "joe@gmail.com")

      form().bind(data).hasErrors mustBe false
    }

    "accept input if only landlineNumber is present" in {
      val data = Map("landlineNumber" -> "01632960001", "mobileNumber" -> "", "emailAddress" -> "")

      form().bind(data).hasErrors mustBe false
    }

    "return errors with invalid data" in {
      val data =
        Map("landlineNumber" -> "01632 960 001Abc", "mobileNumber" -> "07700 900 982", "emailAddress" -> "joe.com")

      form().bind(data).hasErrors mustBe true
    }

    "error if telephone number invalid" in {

      val data =
        Map("landlineNumber" -> "&$01632960001")

      form().bind(data).hasErrors mustBe true
    }

    "error if email address invalid" in {

      val data =
        Map("email_Address" -> "&*joe.com")

      form().bind(data).hasErrors mustBe true
    }

  }

}
