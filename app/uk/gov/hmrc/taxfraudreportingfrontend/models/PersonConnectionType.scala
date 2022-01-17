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

package uk.gov.hmrc.taxfraudreportingfrontend.models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{ErrorMessage, Label}
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukErrorMessage, GovukHint, GovukInput, GovukLabel}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.input.Input
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed abstract class PersonConnectionType

object PersonConnectionType extends Enumerable.Implicits {

  case object partner            extends PersonConnectionType
  case object exPartner          extends PersonConnectionType
  case object familyMember       extends PersonConnectionType
  case object businessPartner    extends PersonConnectionType
  case object employer           extends PersonConnectionType
  case object exEmployer         extends PersonConnectionType
  case object employee           extends PersonConnectionType
  case object colleague          extends PersonConnectionType
  case object friend             extends PersonConnectionType
  case object neighbour          extends PersonConnectionType
  case object customer           extends PersonConnectionType
  case object businessCompetitor extends PersonConnectionType
  case object other              extends PersonConnectionType

  private val govukErrorMessage: GovukErrorMessage = new GovukErrorMessage()
  private val govukHint: GovukHint                 = new GovukHint()
  private val govukLabel: GovukLabel               = new GovukLabel()

  val values: Seq[PersonConnectionType] = Seq(
    partner,
    exPartner,
    familyMember,
    businessPartner,
    employer,
    exEmployer,
    employee,
    colleague,
    friend,
    neighbour,
    customer,
    businessCompetitor,
    other
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"selectConnection.${value.toString}")),
        checked =
          if (form.value.isEmpty)
            form("value").value.contains(value.toString)
          else
            form.value.head.asInstanceOf[ConnectionType].personConnectionType == value,
        conditionalHtml =
          if (value.toString.equals("other"))
            Some(
              new GovukInput(govukErrorMessage, govukHint, govukLabel)(
                Input(
                  id = "otherConnection",
                  value = form("otherConnection").value,
                  label =
                    Label(content = Text(messages("selectConnection.conditional.text.label")), isPageHeading = false),
                  errorMessage =
                    if (form("otherConnection").hasErrors)
                      Some(ErrorMessage(content = Text(messages(form("otherConnection").errors.head.message))))
                    else None,
                  name = "otherConnection",
                  classes = "govuk-!-width-two-thirds",
                  attributes = Map("autocomplete" -> "off")
                )
              )
            )
          else None
      )
  }

  implicit val enumerable: Enumerable[PersonConnectionType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
