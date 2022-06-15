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

sealed trait SelectConnectionBusiness

object SelectConnectionBusiness {

  case object Accountant                extends WithName("accountant") with SelectConnectionBusiness
  case object Auditor                   extends WithName("auditor") with SelectConnectionBusiness
  case object BusinessCompetitor        extends WithName("business-competitor") with SelectConnectionBusiness
  case object MyClient                  extends WithName("my-client") with SelectConnectionBusiness
  case object CurrentEmployer           extends WithName("current-employer") with SelectConnectionBusiness
  case object Customer                  extends WithName("customer") with SelectConnectionBusiness
  case object ExEmployer                extends WithName("ex-employer") with SelectConnectionBusiness
  case object MySupplier                extends WithName("my-supplier") with SelectConnectionBusiness
  case object Advisor                   extends WithName("advisor") with SelectConnectionBusiness
  case object Treasure                  extends WithName("treasure") with SelectConnectionBusiness
  final case class Other(value: String) extends WithName("other") with SelectConnectionBusiness

  val enumerableValues: List[SelectConnectionBusiness] = List(
    Accountant,
    Auditor,
    BusinessCompetitor,
    MyClient,
    CurrentEmployer,
    Customer,
    ExEmployer,
    MySupplier,
    Advisor,
    Treasure
  )

  implicit lazy val writes: Writes[SelectConnectionBusiness] = Writes {
    case Other(value) => Json.obj("type" -> "other", "value" -> value)
    case other        => Json.obj("type" -> other.toString)
  }

  implicit lazy val reads: Reads[SelectConnectionBusiness] =
    (__ \ "type").read[String].flatMap {
      case "accountant"          => Reads.pure(Accountant)
      case "auditor"             => Reads.pure(Auditor)
      case "business-competitor" => Reads.pure(BusinessCompetitor)
      case "my-client"           => Reads.pure(MyClient)
      case "current-employer"    => Reads.pure(CurrentEmployer)
      case "customer"            => Reads.pure(Customer)
      case "ex-employer"         => Reads.pure(ExEmployer)
      case "my-supplier"         => Reads.pure(MySupplier)
      case "advisor"             => Reads.pure(Advisor)
      case "treasure"            => Reads.pure(Treasure)
      case "other"               => (__ \ "value").read[String].map(Other)
    }

}
