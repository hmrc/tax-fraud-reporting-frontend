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

package services

import models.ActivityType
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.collection.immutable.Iterable

@Singleton
class ActivityTypeService @Inject() (configuration: Configuration) {

  private def getMap(path: String) =
    configuration.get[Map[String, String]](path) map {
      case (key, values) => key -> (values split ",")
    }

  val allActivities: Iterable[ActivityType] =
    getMap("activityTypes") map (ActivityType.apply _).tupled

  private val nonHmrcActivities = getMap("nonHmrcActivities")

  def getDepartmentFor(activity: ActivityType): Option[String] =
    nonHmrcActivities collectFirst {
      case (department, activities) if activities contains activity.nameKey => department
    }

  def getActivityByName(name: String): Option[ActivityType] = allActivities find (_.nameKey == name)
}
