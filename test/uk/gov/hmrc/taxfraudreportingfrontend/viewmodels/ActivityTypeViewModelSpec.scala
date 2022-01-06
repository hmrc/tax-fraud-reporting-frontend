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

package uk.gov.hmrc.taxfraudreportingfrontend.viewmodels

import org.scalatest.MustMatchers.convertToAnyMustWrapper
import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.taxfraudreportingfrontend.models.ActivityType

class ActivityTypeViewModelSpec extends WordSpec with Matchers {

  val activityType: ActivityType = ActivityType(
    "22030000",
    "activityType.name.furlough",
    List("CJRS", "Furlough", "COVID", "Corona", "Coronavirus Job Retention Scheme")
  )

  val activityTypeViewModel: ActivityTypeViewModel = ActivityTypeViewModel(
    "22030000",
    "activityType.name.furlough",
    List("CJRS", "Furlough", "COVID", "Corona", "Coronavirus Job Retention Scheme")
  )

  ".toModel" should {
    "convert an ActivityTypeViewModel to an ActivityType model" in {
      activityTypeViewModel.toModel mustBe activityType
    }
  }

  ".from" should {
    "convert an ActivityType to an ActivityTypeViewModel model" in {
      ActivityTypeViewModel.from(activityType) mustBe activityTypeViewModel
    }
  }

}
