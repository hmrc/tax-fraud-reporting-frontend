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

sealed trait PersonConnectionType

object PersonConnectionType extends Enumerable.Implicits {

  case object Partner            extends WithName("partner") with PersonConnectionType
  case object ExPartner          extends WithName("exPartner") with PersonConnectionType
  case object FamilyMember       extends WithName("familyMember") with PersonConnectionType
  case object BusinessPartner    extends WithName("businessPartner") with PersonConnectionType
  case object Employer           extends WithName("employer") with PersonConnectionType
  case object ExEmployer         extends WithName("exEmployer") with PersonConnectionType
  case object Employee           extends WithName("employee") with PersonConnectionType
  case object Colleague          extends WithName("colleague") with PersonConnectionType
  case object Friend             extends WithName("friend") with PersonConnectionType
  case object Neighbour          extends WithName("neighbour") with PersonConnectionType
  case object Customer           extends WithName("customer") with PersonConnectionType
  case object BusinessCompetitor extends WithName("businessCompetitor") with PersonConnectionType
  case object Other              extends WithName("other") with PersonConnectionType

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
