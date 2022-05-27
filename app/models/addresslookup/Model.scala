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

import play.api.libs.json._

case class CountryPicker(countryCode: String)

case class Lookup(filter: Option[String], postcode: String)

case class NonAbpLookup(filter: String)

case class Timeout(timeoutAmount: Int, timeoutUrl: String, timeoutKeepAliveUrl: Option[String])

case class Select(addressId: String)

case class ProposedAddress(
  addressId: String,
  uprn: Option[Long],
  parentUprn: Option[Long],
  usrn: Option[Long],
  organisation: Option[String],
  postcode: Option[String],
  town: Option[String],
  lines: List[String] = List.empty,
  country: Country = Country("GB", "United Kingdom"),
  poBox: Option[String] = None
) {

  def toDescription: String = {
    val addressDescription = (lines.take(3).map(Some(_)) :+ town :+ postcode).flatten.mkString(", ")
    organisation.fold(addressDescription)(org => s"$org, $addressDescription")
  }

}

object CountryFormat {
  implicit val countryFormat: Format[Country] = Json.format[Country]
}

object ProposedAddress {
  import CountryFormat._
  implicit val proposedAddressFormat = Json.format[ProposedAddress]

  def apply(
    addressId: String,
    uprn: Option[Long],
    parentUprn: Option[Long],
    usrn: Option[Long],
    organisation: Option[String],
    postcode: String,
    town: String
  ): ProposedAddress =
    ProposedAddress(
      addressId = addressId,
      uprn = uprn,
      parentUprn = parentUprn,
      usrn = usrn,
      organisation = organisation,
      postcode = Some(postcode),
      town = Some(town)
    )

  def apply(
    addressId: String,
    uprn: Option[Long],
    parentUprn: Option[Long],
    usrn: Option[Long],
    organisation: Option[String],
    postcode: String,
    town: String,
    lines: List[String]
  ): ProposedAddress =
    ProposedAddress(
      addressId = addressId,
      uprn = uprn,
      parentUprn = parentUprn,
      usrn = usrn,
      organisation = organisation,
      postcode = Some(postcode),
      town = Some(town),
      lines = lines
    )

  def apply(
    addressId: String,
    uprn: Option[Long],
    parentUprn: Option[Long],
    usrn: Option[Long],
    organisation: Option[String],
    postcode: String,
    town: String,
    lines: List[String],
    country: Country
  ): ProposedAddress =
    ProposedAddress(
      addressId = addressId,
      uprn = uprn,
      parentUprn = parentUprn,
      usrn = usrn,
      organisation = organisation,
      postcode = Some(postcode),
      town = Some(town),
      lines = lines,
      country = country
    )

}
