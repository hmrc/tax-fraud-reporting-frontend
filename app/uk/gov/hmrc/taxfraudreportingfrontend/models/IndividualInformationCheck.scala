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
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.taxfraudreportingfrontend.controllers.routes

sealed abstract class IndividualInformationCheck(toString: String, val route: Call) extends WithName(toString)

object IndividualInformationCheck extends Enumerable.Implicits {

  case object Name    extends IndividualInformationCheck("name", routes.IndividualNameController.onPageLoad())
  case object Age     extends IndividualInformationCheck("age", routes.IndividualAgeController.onPageLoad())
  case object Address extends IndividualInformationCheck("address", routes.IndividualAddressController.onPageLoad())
  case object Contact extends IndividualInformationCheck("contact", routes.IndividualContactController.onPageLoad())
  case object NINO    extends IndividualInformationCheck("nino", routes.NinoController.onPageLoad())

  val values: Seq[IndividualInformationCheck] = Seq(Name, Age, Address, Contact, NINO)

  def options(form: Form[_])(implicit messages: Messages): Seq[CheckboxItem] = values.map {
    value =>
      CheckboxItem(
        name = Some("value[]"),
        id = Some(value.toString),
        value = value.toString,
        content = Text(messages(s"individual.informationCheck.${value.toString}")),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[IndividualInformationCheck] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
