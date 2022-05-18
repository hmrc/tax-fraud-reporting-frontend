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

import models.backend.Address
import pages._
import play.api.libs.json._
import queries.{Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import scala.util.{Failure, Success, Try}

final case class UserAnswers(id: String, data: JsObject = Json.obj(), lastUpdated: Instant = Instant.now) {

  def getAddress(index: Index, forBusiness: Boolean): Option[Address] = {
    val (countryPage, addressPage) =
      if (forBusiness)
        (BusinessSelectCountryPage(index), BusinessAddressPage(index))
      else
        (IndividualSelectCountryPage(index), IndividualAddressPage(index))

    for {
      countryCode                                                       <- get(countryPage)
      AddressSansCountry(line1, line2Opt, line3Opt, townCity, postCode) <- get(addressPage)
    } yield Address(line1, line2Opt, line3Opt, townCity, postCode, countryCode)
  }

  def isBusinessJourney: Boolean = get(IndividualOrBusinessPage).contains(IndividualOrBusiness.Business)
  def isProvideContact: Boolean  = get(ProvideContactDetailsPage).contains(ProvideContactDetails.Yes)

  def isBusinessDetails(index: Index): Boolean =
    get(IndividualBusinessDetailsPage(index)).contains(IndividualBusinessDetails.Yes)

  def individualHasBusiness: Boolean =
    get(IndividualBusinessDetailsPage(Index(0))).contains(IndividualBusinessDetails.Yes)

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](page: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(page.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(None, updatedAnswers)
    }
  }

}

object UserAnswers {

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(UserAnswers.apply _)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(UserAnswers.unapply))
  }

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}
