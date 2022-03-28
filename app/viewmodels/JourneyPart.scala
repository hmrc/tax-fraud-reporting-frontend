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

package viewmodels

import models.{Index, Mode}
import play.api.mvc.Call

abstract class JourneyPart(val heading: String) {
  val addressSubmit: (Index, Mode) => Call
}

case class IndividualPart(withBusiness: Boolean) extends JourneyPart(if (withBusiness) "business" else "individual") {

  val addressSubmit: (Index, Mode) => Call =
    if (withBusiness) controllers.routes.BusinessAddressController.onSubmit
    else controllers.routes.IndividualAddressController.onSubmit

}

case object BusinessPart extends JourneyPart("business") {

  val addressSubmit: (Index, Mode) => Call =
    controllers.routes.BusinessAddressController.onSubmit

}
