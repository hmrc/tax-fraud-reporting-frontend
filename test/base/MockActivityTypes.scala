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

package base

import org.mockito.ArgumentMatchers.{any, eq => equal}
import org.mockito.MockitoSugar
import play.api.{ConfigLoader, Configuration}
import services.ActivityTypeService

trait MockActivityTypes extends MockitoSugar {
  type MapLoader = ConfigLoader[Map[String, String]]

  private val fooActivity     = "foo"              -> "bar,baz,qux"
  private val otherActivity1  = "nonHmrcActivity1" -> ""
  private val otherActivity2  = "nonHmrcActivity2" -> ""
  private val otherActivities = Seq(otherActivity1, otherActivity2) map { _._1 } mkString ","

  val mockOtherDept: (String, String) = "nonHmrcDept" -> otherActivities

  val mockConfiguration: Configuration = mock[Configuration]
  when {
    mockConfiguration.get(equal("activityTypes"))(any[MapLoader])
  } thenReturn Map(fooActivity, otherActivity1, otherActivity2)
  when {
    mockConfiguration.get(equal("nonHmrcActivities"))(any[MapLoader])
  } thenReturn Map(mockOtherDept)

  implicit val mockActivityTypeService: ActivityTypeService = new ActivityTypeService(mockConfiguration)

  val firstMockActivityType: String = mockActivityTypeService.allActivities.head._1
}
