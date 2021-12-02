/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalatest.Matchers
import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.taxfraudreportingfrontend.forms.ActivityTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.util.BaseSpec

class ActivityTypeProviderSpec extends BaseSpec with Matchers with GuiceOneAppPerSuite {

  val provider = new ActivityTypeProvider

  "ActivityTypeProvider" should {

    "validate with no issues" when {

      "activityType provided with space" in {
        val data = Map("activityType" -> "Fraud related to furlough")

        provider().bind(data).hasErrors mustBe false
      }

      "activityType provided with hyphens" in {
        val data = Map("activityType" -> "Fraud-related-to-furlough")

        provider().bind(data).hasErrors mustBe false
      }

      "validate with issues" when {

        "activity type has invalid entry" in {
          val safeInputPattern = "^(|[a-zA-Z][a-zA-Z0-9 / '-]+)$?"
          val data             = Map("activityType" -> safeInputPattern)

          val errors = provider().bind(data).errors
          errors.head.message mustBe "activityType.error.invalid"
        }

        "activity type has invalid entry as numbers" in {
          val data = Map("activityType" -> "123456")

          val errors = provider().bind(data).errors
          provider().bind(data).hasErrors mustBe true
        }

      }
    }
  }
}
