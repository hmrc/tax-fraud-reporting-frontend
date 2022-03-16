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
import pages.BusinessAddressPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object BusinessAddressSummary {

  def row(answers: UserAnswers, index: Int, mode: Mode = CheckMode)(implicit
    messages: Messages
  ): Option[SummaryListRow] = {
    val answer = answers.get(BusinessAddressPage(Index(index))).map {
      answer =>
        List(
          answer.addressLine1,
          answer.addressLine2,
          answer.addressLine3,
          answer.townCity,
          answer.postcode,
          Some(answer.country)
        ).flatten map HtmlFormat.escape mkString "<br/>"
    } getOrElse messages("site.unknown")

    Some(
      SummaryListRowViewModel(
        key = "businessAddress.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(answer)),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            routes.BusinessAddressController.onPageLoad(Index(index), mode).url
          ).withVisuallyHiddenText(messages("businessAddress.change.hidden"))
        )
      )
    )
  }

}
