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

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class AddressResponse(lines: List[String], town: Option[String], postcode: Option[String], country: Option[String])

object AddressResponse {

  implicit lazy val reads: Reads[AddressResponse] = (
    (__ \ "address" \ "lines").read[List[String]] ~
      (__ \ "address" \ "town").readNullable[String] ~
      (__ \ "address" \ "postcode").readNullable[String] ~
      (__ \ "address" \ "country" \ "code").readNullable[String]
  )(AddressResponse(_, _, _, _))

  implicit lazy val writes: Writes[AddressResponse] = (
    (__ \ "address" \ "lines").write[List[String]] ~
      (__ \ "address" \ "town").writeNullable[String] ~
      (__ \ "address" \ "postcode").writeNullable[String] ~
      (__ \ "address" \ "country" \ "code").writeNullable[String]
  )(unlift(AddressResponse.unapply))

}
