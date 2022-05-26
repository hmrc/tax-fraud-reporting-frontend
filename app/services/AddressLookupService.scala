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

package services

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.inject.ImplementedBy
import config.Service
import models.addresslookup.{AddressLookup, AddressRecord, Country, LocalCustodian, ProposedAddress}
import play.api.Configuration
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import java.util.regex.Pattern
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import services.AddressReputationFormats._

import scala.util.Try

@ImplementedBy(classOf[AddressLookupService])
trait AddressService {

  def lookup(postcode: String, filter: Option[String] = None)(implicit
    hc: HeaderCarrier
  ): Future[Seq[ProposedAddress]]

}

@Singleton
class AddressLookupService @Inject() (httpClient: HttpClient, configuration: Configuration)(implicit
  ec: ExecutionContext
) extends AddressService {

  private val baseUrl: String = configuration.get[Service]("microservice.services.address-lookup").baseUrl

  override def lookup(postcode: String, filter: Option[String] = None)(implicit
    hc: HeaderCarrier
  ): Future[Seq[ProposedAddress]] = {

    val pc                 = postcode.replaceAll(" ", "")
    val newHc              = hc.withExtraHeaders("X-Hmrc-Origin" -> "fraud")
    val addressRequestBody = AddressLookup(pc, filter)

    httpClient.POST[AddressLookup, List[AddressRecord]](s"$baseUrl/lookup", addressRequestBody)(
      implicitly,
      implicitly,
      newHc,
      implicitly
    ).map { found =>
      val results = found.map { addr =>
        ProposedAddress(
          addr.id,
          addr.uprn,
          addr.parentUprn,
          addr.usrn,
          addr.organisation,
          Some(addr.address.postcode),
          Some(addr.address.town),
          addr.address.lines,
          if ("UK" == addr.address.country.code) Country("GB", "United Kingdom")
          else addr.address.country,
          addr.poBox
        )
      }

      results.sortWith { (a, b) =>
        def sort(zipped: Seq[(Option[Int], Option[Int])]): Boolean = zipped match {
          case (Some(nA), Some(nB)) :: tail =>
            if (nA == nB) sort(tail) else nA < nB
          case (Some(_), None) :: _ => true
          case (None, Some(_)) :: _ => false
          case _                    => mkString(a) < mkString(b)
        }

        sort(numbersIn(a).zipAll(numbersIn(b), None, None).toList)
      }
    }
  }

  def mkString(p: ProposedAddress) = p.lines.mkString(" ").toLowerCase()

  def numbersIn(p: ProposedAddress): Seq[Option[Int]] =
    "([0-9]+)".r.findAllIn(mkString(p)).map(n => Try(n.toInt).toOption).toSeq.reverse :+ None

}

case class Address(
  lines: List[String],
  town: String,
  postcode: String,
  subdivision: Option[Country],
  country: Country
) {

  import Address._

  @JsonIgnore // needed because the name starts 'is...'
  def isValid: Boolean = lines.nonEmpty && lines.size <= (if (town.isEmpty) 4 else 3)

  def nonEmptyFields: List[String] = lines ::: List(town) ::: List(postcode)

  /** Gets a conjoined representation, excluding the country. */
  def printable(separator: String): String = nonEmptyFields.mkString(separator)

  /** Gets a single-line representation, excluding the country. */
  @JsonIgnore // needed because it's a field
  lazy val printable: String = printable(", ")

  def line1: String = if (lines.nonEmpty) lines.head else ""

  def line2: String = if (lines.size > 1) lines(1) else ""

  def line3: String = if (lines.size > 2) lines(2) else ""

  def line4: String = if (lines.size > 3) lines(3) else ""

  def longestLineLength: Int = nonEmptyFields.map(_.length).max

  def truncatedAddress(maxLen: Int = maxLineLength): Address =
    Address(lines.map(limit(_, maxLen)), limit(town, maxLen), postcode, subdivision, country)

  /*  def asV1 = v1.Address(lines, town, postcode, subdivision.map(_.code), country.asV1)

  def asInternational = International(lines ::: List(town) ::: subdivision.map(_.name).toList, Some(postcode), Some(country))*/
}

object Address {
  val maxLineLength           = 35
  val danglingLetter: Pattern = Pattern.compile(".* [A-Z0-9]$")

  private def limit(str: String, max: Int): String = {
    var s = str
    while (s.length > max && s.indexOf(", ") > 0)
      s = s.replaceFirst(", ", ",")
    if (s.length > max) {
      s = s.substring(0, max).trim
      if (Address.danglingLetter.matcher(s).matches())
        s = s.substring(0, s.length - 2)
      s
    } else s
  }

}

object AddressReputationFormats {
  import play.api.libs.functional.syntax._
  import play.api.libs.json.{JsPath, Reads}

  implicit val format0: Format[Country]        = Json.format[Country]
  implicit val format1: Format[LocalCustodian] = Json.format[LocalCustodian]
  implicit val format2: Format[Address]        = Json.format[Address]

  implicit val addressRecordReads: Reads[AddressRecord] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "uprn").readNullable[Long] and
      (JsPath \ "parentUprn").readNullable[Long] and
      (JsPath \ "usrn").readNullable[Long] and
      (JsPath \ "organisation").readNullable[String] and
      (JsPath \ "address").read[Address] and
      (JsPath \ "language").read[String] and
      (JsPath \ "localCustodian").readNullable[LocalCustodian] and
      (JsPath \ "location").readNullable[Seq[BigDecimal]] and
      (JsPath \ "blpuState").readNullable[String] and
      (JsPath \ "logicalState").readNullable[String] and
      (JsPath \ "streetClassification").readNullable[String] and
      (JsPath \ "administrativeArea").readNullable[String] and
      (JsPath \ "poBox").readNullable[String]
  )(AddressRecord.apply _)

  implicit val format3: Format[AddressRecord] = Format(addressRecordReads, Json.writes[AddressRecord])

  // implicit val format4: Format[International] = Json.format[International]
  // implicit val nonUkAddressRecordReads: Reads[NonUkAddressRecord] = Json.reads[NonUkAddressRecord]
}
