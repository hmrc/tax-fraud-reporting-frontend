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

import play.api.libs.json._

sealed trait IndividualConnection

object IndividualConnection extends {

  case object Partner                   extends WithName("partner") with IndividualConnection
  case object ExPartner                 extends WithName("exPartner") with IndividualConnection
  case object FamilyMember              extends WithName("familyMember") with IndividualConnection
  case object BusinessPartner           extends WithName("businessPartner") with IndividualConnection
  case object Employer                  extends WithName("employer") with IndividualConnection
  case object ExEmployer                extends WithName("exEmployer") with IndividualConnection
  case object Employee                  extends WithName("employee") with IndividualConnection
  case object Colleague                 extends WithName("colleague") with IndividualConnection
  case object Friend                    extends WithName("friend") with IndividualConnection
  case object Neighbour                 extends WithName("neighbour") with IndividualConnection
  case object Customer                  extends WithName("customer") with IndividualConnection
  case object BusinessCompetitor        extends WithName("businessCompetitor") with IndividualConnection
  final case class Other(value: String) extends WithName("other") with IndividualConnection

  val enumerableValues: List[IndividualConnection] = List(
    Partner,
    ExPartner,
    FamilyMember,
    BusinessPartner,
    Employer,
    ExEmployer,
    Employee,
    Colleague,
    Friend,
    Neighbour,
    Customer,
    BusinessCompetitor
  )

//  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
//    case (value, index) =>
//      RadioItem(
//        content = Text(messages(s"individualConnection.${value.toString}")),
//        value   = Some(value.toString),
//        id      = Some(s"value_$index")
//      )
//  }

  implicit lazy val writes: Writes[IndividualConnection] = Writes {
    case Other(value) => Json.obj("type" -> "other", "value" -> value)
    case other        => Json.obj("type" -> other.toString)
  }

  implicit lazy val reads: Reads[IndividualConnection] =
    (__ \ "type").read[String].flatMap {
      case "partner"            => Reads.pure(Partner)
      case "exPartner"          => Reads.pure(ExPartner)
      case "familyMember"       => Reads.pure(FamilyMember)
      case "businessPartner"    => Reads.pure(BusinessPartner)
      case "employer"           => Reads.pure(Employer)
      case "exEmployer"         => Reads.pure(ExEmployer)
      case "employee"           => Reads.pure(Employee)
      case "colleague"          => Reads.pure(Colleague)
      case "friend"             => Reads.pure(Friend)
      case "neighbour"          => Reads.pure(Neighbour)
      case "customer"           => Reads.pure(Customer)
      case "businessCompetitor" => Reads.pure(BusinessCompetitor)
      case "other"              => (__ \ "value").read[String].map(Other)
    }

}
