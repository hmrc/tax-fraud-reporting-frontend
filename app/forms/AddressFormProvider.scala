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

package forms

import forms.mappings.Mappings
import models.backend.Address
import play.api.data.Forms.{list, mapping}
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}

import scala.language.postfixOps

class AddressFormProvider extends Mappings {
  private val maxLen      = 255
  private val errorPrefix = "address.error"

  def apply(): Form[Address] = Form(
    mapping(
      "lines"    -> Forms.of(addressLinesFormatter),
      "postCode" -> Forms.of(ukPostCodeFormatter),
      "country" -> text("error.country.required")
        .verifying(maxLength(maxLen, "error.length"))
    )(toModel)(fromModel)
  )

  private def toModel(lines: Map[String, String], postCode: Option[String], country: String) =
    Address(lines get "line1", lines get "line2", lines get "line3", lines get "townOrCity", postCode, country)

  private def fromModel(address: Address): Option[(Map[String, String], Option[String], String)] =
    Some {
      import address._
      (
        Seq(
          "line1"      -> addressLine1,
          "line2"      -> addressLine2,
          "line3"      -> addressLine3,
          "townOrCity" -> townCity
        ) flatMap {
          case (key, valueOpt) => valueOpt map { key -> _ }
        } toMap,
        postcode,
        country
      )
    }

  type Lines = Map[String, String]

  private val addressLinesFormatter = new Formatter[Lines] {

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Lines] = {
      val validKeys = List("line1", "line2", "line3", "townOrCity")

      val emptyLines: Either[Seq[FormError], Lines] = Right(Map.empty)

      val errorsOrLines = (validKeys foldLeft emptyLines) {
        (acc, subKey) =>
          data get subKey filter { _.nonEmpty } match {
            case None => acc
            case Some(line) if line.length <= maxLen =>
              acc map { _.updated(subKey, line) }
            case Some(_) =>
              val error = FormError(subKey, s"$errorPrefix.$subKey.length")
              Left(acc.fold(_ :+ error, _ => Seq(error)))
          }
      }

      errorsOrLines.filterOrElse(
        lines => lines.nonEmpty && lines.values.exists(_.matches(Validation.validAddress.toString)),
        validKeys.zipWithIndex map {
          case (subKey, index) =>
            val message = if (index > 0) "" else "error.addressLines.required"
            FormError(subKey, message)
        }
      )

    }

    def unbind(key: String, value: Lines): Map[String, String] = value
  }

  type PostCode = String

  private val ukPostCodeFormatter = new Formatter[Option[PostCode]] {

    /** Pattern obtained from The National Archives:
      * https://webarchive.nationalarchives.gov.uk/ukgwa/20101126040223/http://www.cabinetoffice.gov.uk/media/291370/bs7666-v2-0-xsd-PostCodeType.htm
      * A more thorough but complex one is also available there but
      * went with this for simplicity. */
    private val pattern = "(?i)[A-Z]{1,2}[0-9R][0-9A-Z]?\\s*[0-9][ABD-HJLNP-UW-Z]{2}"

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[PostCode]] = {
      val postCodeOpt = data get key filter (_.nonEmpty)
      data get "country" match {
        case None => Left(Seq(FormError("country", "error.country.required")))
        case Some("gb") =>
          Right(postCodeOpt).filterOrElse(_ forall (_ matches pattern), Seq(FormError(key, s"error.postcode.invalid")))
        case _ =>
          Right(postCodeOpt).filterOrElse(
            _ forall (_.length <= maxLen),
            Seq(FormError(key, s"address.error.postcode.length"))
          )
      }
    }

    def unbind(key: String, value: Option[PostCode]): Map[String, String] =
      value map (key -> _) toMap

  }

}
