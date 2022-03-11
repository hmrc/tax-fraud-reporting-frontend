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

package forms

import forms.mappings.Mappings
import models.ActivityType
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}
import services.ActivityTypeService

import javax.inject.{Inject, Singleton}

@Singleton
class ActivityTypeFormProvider @Inject() (activityTypeService: ActivityTypeService) extends Mappings {
  private val formatter = new Formatter[ActivityType] {

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ActivityType] =
      data get key flatMap { activityStr =>
        activityTypeService.allActivities find (_.nameKey == activityStr)
      } toRight
        Seq(FormError(key, "activityType.error.invalid"))

    def unbind(key: String, value: ActivityType): Map[String, String] =
      Map(key -> value.nameKey)

  }

  def apply(): Form[ActivityType] = Form("value" -> Forms.of(formatter))

}
