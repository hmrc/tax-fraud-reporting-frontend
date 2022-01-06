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

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualNameProvider
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

class IndividualNameProviderSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with BaseSpec {

  val form = new IndividualNameProvider()

  "form" should {
    "return no errors with valid data" in {
      val data = Map("firstName" -> "David", "middleName" -> "harvey", "lastName" -> "Write", "nickName" -> "Dave")

      form().bind(data).hasErrors mustBe false
    }

    "return errors with invalid data" in {
      val data = Map("firstName" -> "%%%Dave", "middleName" -> "harvey", "lastName" -> "Write", "nickName" -> "Dave")

      form().bind(data).hasErrors mustBe true

      val errors = form().bind(data).errors
      errors.head.message mustBe "individualName.error.forename.invalid"
    }

    "return errors with blank field" in {
      val data = Map("firstName" -> "", "middleName" -> "", "lastName" -> "", "nickName" -> "")

      form().bind(data).hasErrors mustBe true

      val errors = form().bind(data).errors
      errors.head.message mustBe "Enter either the individual’s first name, middle name, last name or nick name."
    }

  }

}
