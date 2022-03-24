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

package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.YourContactDetailsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.Util._

object YourContactDetailsSummary {

  def rows(answers: UserAnswers)(implicit messages: Messages): List[SummaryListRow] =
    answers.get(YourContactDetailsPage).toList.flatMap {
      answer =>
        def msg(key: String) = messages("yourContactDetails." + key)

        val values = Map(
          "firstName"        -> answer.FirstName,
          "lastName"         -> answer.LastName,
          "tel"              -> answer.Tel,
          "emailLabel"       -> answer.Email.getOrElse(messages("site.unknown")),
          "uniqueIdentifier" -> sanitizeString(answer.MemorableWord).getOrElse(messages("site.unknown"))
        )

        values map {
          case (key, value) =>
            SummaryListRowViewModel(
              key = msg(key),
              value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
              actions = Seq(
                ActionItemViewModel("site.change", routes.YourContactDetailsController.onPageLoad(CheckMode).url)
                  .withVisuallyHiddenText(msg(key))
              )
            )
        }
    }

}
