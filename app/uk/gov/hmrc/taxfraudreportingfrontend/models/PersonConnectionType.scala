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

package uk.gov.hmrc.taxfraudreportingfrontend.models

sealed abstract class PersonConnectionType(toString: String) extends WithName(toString)

object PersonConnectionType extends Enumerable.Implicits {

  case object Partner            extends PersonConnectionType("partner")
  case object ExPartner          extends PersonConnectionType("exPartner")
  case object FamilyMember       extends PersonConnectionType("familyMember")
  case object BusinessPartner    extends PersonConnectionType("businessPartner")
  case object Employer           extends PersonConnectionType("employer")
  case object ExEmployer         extends PersonConnectionType("exEmployer")
  case object Employee           extends PersonConnectionType("employee")
  case object Colleague          extends PersonConnectionType("colleague")
  case object Friend             extends PersonConnectionType("friend")
  case object Neighbour          extends PersonConnectionType("neighbour")
  case object Customer           extends PersonConnectionType("customer")
  case object BusinessCompetitor extends PersonConnectionType("businessCompetitor")
  case object Other              extends PersonConnectionType("other")

  val values: Seq[PersonConnectionType] = Seq(
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
    BusinessCompetitor,
    Other
  )

  implicit val enumerable: Enumerable[PersonConnectionType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
