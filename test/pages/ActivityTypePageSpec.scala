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

package pages

import base.MockActivityTypes
import org.scalacheck.{Arbitrary, Gen}
import pages.behaviours.PageBehaviours
import play.api.libs.json.{Format, JsString}
import services.ActivityTypeService

class ActivityTypePageSpec extends PageBehaviours with MockActivityTypes {

  private implicit def arbActivityType(implicit activityTypeService: ActivityTypeService): Arbitrary[String] =
    Arbitrary(Gen oneOf activityTypeService.allActivities.keys)

  private implicit def formatActivityType(implicit activityTypeService: ActivityTypeService): Format[String] =
    Format(_.validate[String] filter activityTypeService.allActivities.contains, JsString.apply)

  "ActivityTypePage" - {

    beRetrievable[String](ActivityTypePage)

    beSettable[String](ActivityTypePage)

    beRemovable[String](ActivityTypePage)
  }
}
