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

package models

import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}

import scala.language.postfixOps

// TODO tests

final case class ActivityType(nameKey: String, synonymKeys: Array[String]) {

  def translated(implicit messages: Messages): ActivityTypeTranslated = {
    def msg(key: String) = messages("activityType." + key)

    ActivityTypeTranslated(
      nameKey,
      msg("name." + nameKey),
      synonymKeys map { synonymKey => msg("synonym." + synonymKey) }
    )
  }

}

object ActivityType {
  implicit val format: OFormat[ActivityType] = Json.format
}
