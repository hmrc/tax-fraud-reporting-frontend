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
import models.{CheckMode, Index, Mode, UserAnswers}
import pages.IndividualNamePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object IndividualNameSummary {

  def row(answers: UserAnswers, index: Int, mode: Mode = CheckMode)(implicit
                                                                    messages: Messages
  ): Option[SummaryListRow] = {
    val answer = answers.get(IndividualNamePage(Index(index)))

    val value = List(
      messages("individualName.firstName") -> answer.flatMap(_.firstName),
      messages("individualName.middleName")   -> answer.flatMap(_.middleName),
      messages("individualName.lastName")   -> answer.flatMap(_.lastName),
      messages("individualName.nickname")    -> answer.flatMap(_.aliases)
    ) map {
      case (label, valueOpt) =>
        HtmlFormat.escape(label + ": " + valueOpt.getOrElse(messages("site.unknown")))
    } mkString "<br>"


    Some(
      SummaryListRowViewModel(
        key = "individualName.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(value)),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            routes.IndividualNameController.onPageLoad(Index(index), mode).url
          ).withVisuallyHiddenText(messages("IndividualName.change.hidden"))
        )
      )
    )
  }

}
