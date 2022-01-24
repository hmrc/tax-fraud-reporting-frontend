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
import models.{CheckMode, Index, UserAnswers}
import pages.IndividualInformationPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object IndividualInformationSummary {

  def row(answers: UserAnswers, index: Int)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(IndividualInformationPage(Index(index))).map {
      answers =>
        val value = ValueViewModel(HtmlContent(answers.map {
          answer => HtmlFormat.escape(messages(s"individualInformation.$answer")).toString
        }
          .mkString(",<br>")))

        SummaryListRowViewModel(
          key = "individualInformation.checkYourAnswersLabel",
          value = value,
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.IndividualInformationController.onPageLoad(Index(index), CheckMode).url
            )
              .withVisuallyHiddenText(messages("individualInformation.change.hidden"))
          )
        )
    }

}
