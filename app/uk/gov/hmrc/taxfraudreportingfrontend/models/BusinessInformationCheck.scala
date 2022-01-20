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

import play.api.mvc.Call
import uk.gov.hmrc.taxfraudreportingfrontend.controllers.routes

sealed abstract class BusinessInformationCheck(toString: String, val route: Call) extends WithName(toString)

object BusinessInformationCheck extends Enumerable.Implicits {

  case object BusinessName extends BusinessInformationCheck("name", routes.BusinessNameController.onPageLoad())
  case object BusinessType extends BusinessInformationCheck("type", routes.BusinessTypeController.onPageLoad())

  case object BusinessAddress
      extends BusinessInformationCheck("address", routes.IndividualAddressController.onPageLoad())

  case object BusinessContact extends BusinessInformationCheck("contact", routes.BusinessContactController.onPageLoad())

  case object BusinessReference
      extends BusinessInformationCheck("reference", routes.BusinessReferenceController.onPageLoad())

  val values: Seq[BusinessInformationCheck] =
    Seq(BusinessName, BusinessType, BusinessAddress, BusinessContact, BusinessReference)

  implicit val enumerable: Enumerable[BusinessInformationCheck] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
