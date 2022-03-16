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

package viewmodels.govuk

import play.api.i18n.Messages
import play.api.libs.json.{Json, Writes}

case class AutocompleteOption(value: String, text: String, searchTerms: String)

object AutocompleteOption {
  implicit val writes: Writes[AutocompleteOption] = Json.writes

  def get(messages: Messages, superKey: String)(key: String): AutocompleteOption = {
    def translate(subKey: String) = messages(s"$superKey.$key.$subKey")
    apply(key, translate("text"), translate("searchTerms"))
  }

}
