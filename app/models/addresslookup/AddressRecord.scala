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

package models.addresslookup

import com.fasterxml.jackson.annotation.JsonIgnore
import play.api.libs.json.{Format, Json}
import services.Address

/**
  * Represents one address record. Arrays of these are returned from the address-lookup microservice.
  */
case class AddressRecord(
  id: String,
  uprn: Option[Long],
  parentUprn: Option[Long],
  usrn: Option[Long],
  organisation: Option[String],
  address: Address,
  language: String,
  location: Option[Seq[BigDecimal]],
  blpuState: Option[String],
  logicalState: Option[String],
  streetClassification: Option[String],
  administrativeArea: Option[String] = None,
  poBox: Option[String] = None
) {

  require(location.isEmpty || location.get.size == 2, location.get)

  @JsonIgnore // needed because the name starts 'is...'
  def isValid: Boolean = address.isValid && language.length == 2

  def truncatedAddress(maxLen: Int = Address.maxLineLength): AddressRecord =
    if (address.longestLineLength <= maxLen) this
    else copy(address = address.truncatedAddress(maxLen))

  def withoutMetadata: AddressRecord = copy(blpuState = None, logicalState = None, streetClassification = None)

}

object AddressRecord {
  import play.api.libs.functional.syntax._
  import play.api.libs.json.{JsPath, Reads}

  implicit val format0: Format[Country] = Json.format[Country]
  implicit val format2: Format[Address] = Json.format[Address]

  implicit val addressRecordReads: Reads[AddressRecord] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "uprn").readNullable[Long] and
      (JsPath \ "parentUprn").readNullable[Long] and
      (JsPath \ "usrn").readNullable[Long] and
      (JsPath \ "organisation").readNullable[String] and
      (JsPath \ "address").read[Address] and
      (JsPath \ "language").read[String] and
      (JsPath \ "location").readNullable[Seq[BigDecimal]] and
      (JsPath \ "blpuState").readNullable[String] and
      (JsPath \ "logicalState").readNullable[String] and
      (JsPath \ "streetClassification").readNullable[String] and
      (JsPath \ "administrativeArea").readNullable[String] and
      (JsPath \ "poBox").readNullable[String]
  )(AddressRecord.apply _)

  implicit val format3: Format[AddressRecord] = Format(addressRecordReads, Json.writes[AddressRecord])
}
