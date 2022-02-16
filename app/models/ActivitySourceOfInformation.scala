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

sealed trait ActivitySourceOfInformation

object ActivitySourceOfInformation extends Enumerable.Implicits {

  case object ReportedIndividuals       extends WithName("reportedIndividuals") with ActivitySourceOfInformation
  case object InformationInLocalArea    extends WithName("informationInLocalArea") with ActivitySourceOfInformation
  case object ObservedTheActivity       extends WithName("observedTheActivity") with ActivitySourceOfInformation
  case object OverheardTheActivity      extends WithName("overheardTheActivity") with ActivitySourceOfInformation
  case object SpeculatedThisActivity    extends WithName("speculatedThisActivity") with ActivitySourceOfInformation
  case object ReportedByIndividual      extends WithName("reportedByIndividual") with ActivitySourceOfInformation
  case object ByThirdPart               extends WithName("byThirdPart") with ActivitySourceOfInformation
  final case class Other(value: String) extends WithName("other") with ActivitySourceOfInformation

  val enumerableValues: List[ActivitySourceOfInformation] = List(
    ReportedIndividuals,
    InformationInLocalArea,
    ObservedTheActivity,
    OverheardTheActivity,
    SpeculatedThisActivity,
    ReportedByIndividual,
    ByThirdPart
  )

  implicit lazy val writes: Writes[ActivitySourceOfInformation] = Writes {
    case Other(value) => Json.obj("type" -> "other", "value" -> value)
    case other        => Json.obj("type" -> other.toString)
  }

  implicit lazy val reads: Reads[ActivitySourceOfInformation] =
    (__ \ "type").read[String].flatMap {
      case "reportedIndividuals"            => Reads.pure(ReportedIndividuals)
      case "informationInLocalArea"         => Reads.pure(InformationInLocalArea)
      case "observedTheActivity-competitor" => Reads.pure(ObservedTheActivity)
      case "overheardTheActivity"           => Reads.pure(OverheardTheActivity)
      case "speculatedThisActivity"         => Reads.pure(SpeculatedThisActivity)
      case "reportedByIndividual"           => Reads.pure(ReportedByIndividual)
      case "byThirdPart"                    => Reads.pure(ByThirdPart)
      case "other"                          => (__ \ "value").read[String].map(Other)
    }

}
