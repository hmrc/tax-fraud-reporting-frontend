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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

sealed abstract class BusinessInformationCheck(val order: Byte, name: String) extends WithName(name)

object BusinessInformationCheck extends Enumerable.Implicits {
  case object Name              extends BusinessInformationCheck(0, "name")
  case object Type              extends BusinessInformationCheck(1, "type")
  case object Address           extends BusinessInformationCheck(2, "address")
  case object Contact           extends BusinessInformationCheck(3, "contact")
  case object BusinessReference extends BusinessInformationCheck(4, "businessReference")

  val values: Seq[BusinessInformationCheck] = Seq(Name, Type, Address, Contact, BusinessReference)

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"businessInformationCheck.${value.toString}")),
          fieldId = "value",
          index = index,
          value = value.toString
        )
    }

  implicit val enumerable: Enumerable[BusinessInformationCheck] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
