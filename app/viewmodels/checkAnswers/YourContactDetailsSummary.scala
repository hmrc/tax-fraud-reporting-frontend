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

import models.{CheckMode, UserAnswers}
import pages.YourContactDetailsPage
import play.api.i18n.Messages
import controllers.routes
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object YourContactDetailsSummary {

  def rows(answers: UserAnswers)(implicit messages: Messages): List[SummaryListRow] =
    answers.get(YourContactDetailsPage).toList.flatMap {
      answer =>
        val values = List(
          Some("firstName" -> answer.FirstName),
          Some("lastName" -> answer.LastName),
          Some("tel" -> answer.Tel),
          answer.Email map {
            "emailLabel" -> _
          },
          Some("memorableWord" -> answer.MemorableWord)
        ).flatten

        values map { case (key, value) =>
          SummaryListRowViewModel(
            key = "yourContactDetails." + key,
            value = ValueViewModel(HtmlContent(value)),
            actions = Seq(
              ActionItemViewModel("site.change", routes.YourContactDetailsController.onPageLoad(CheckMode).url)
                .withVisuallyHiddenText(messages("yourContactDetails.change.hidden"))
            ))
        }
    }

}
