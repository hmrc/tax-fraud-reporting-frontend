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
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}
import Forms.{of, optional}

import scala.language.postfixOps

class AddressFormProvider extends Mappings {
  private val maxLen = 255

  def apply(): Form[Address] = Form(
    mapping(
      "line1" -> text("error.address.line1").verifying(maxLength(maxLen, "address.error.line1.length"))
        .verifying(regexpRestrict(Validation.validString.toString, "error.address.line1")),
      "line2" -> optional(text().verifying(maxLength(maxLen, "address.error.line2.length"))),
      "line3" -> optional(text().verifying(maxLength(maxLen, "address.error.line3.length"))),
      "townOrCity" -> text("error.address.townOrCity").verifying(maxLength(maxLen, "address.error.townOrCity.length"))
        .verifying(regexpRestrict(Validation.validString.toString, "error.address.townOrCity")),
      "postCode" -> of(ukPostCodeFormatter),
      "country"  -> text("error.country.required")
    )(Address.apply)(Address.unapply)
  )

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
