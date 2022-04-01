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

package pages

import models.IndividualBusinessDetails.Yes
import models.backend.Address
import models.{BusinessContactDetails, BusinessInformationCheck, Index, IndividualBusinessDetails, IndividualConnection, IndividualContactDetails, IndividualDateFormat, IndividualInformation, IndividualName, ReferenceNumbers, SelectConnectionBusiness, UserAnswers}
import play.api.libs.json.JsPath

import java.time.LocalDate
import scala.util.{Success, Try}

abstract class NominalsQuestionPage[A](override val toString: String)
    extends QuestionPage[A] with IndexedConfirmationPage {
  val index: Index
  def path: JsPath = JsPath \ "nominals" \ index.position \ toString
}

/** START Individual journey */
final case class IndividualInformationPage(index: Index)
    extends NominalsQuestionPage[Set[IndividualInformation]]("individualInformation")

/** START Checked Info Pages */
final case class IndividualNamePage(index: Index) extends NominalsQuestionPage[IndividualName]("individualName")

/** START Age Pages */
final case class IndividualDateFormatPage(index: Index)
    extends NominalsQuestionPage[IndividualDateFormat]("dateFormat") {

  override def cleanup(value: Option[IndividualDateFormat], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(IndividualDateFormat.Date) => userAnswers remove IndividualAgePage(index)
      case Some(IndividualDateFormat.Age)  => userAnswers remove IndividualDateOfBirthPage(index)
      case _                               => Success(userAnswers)
    }

}

final case class IndividualDateOfBirthPage(index: Index)
    extends NominalsQuestionPage[LocalDate]("individualDateOfBirth")

final case class IndividualAgePage(index: Index) extends NominalsQuestionPage[Int]("individualAge")

/** END Age Pages */

final case class IndividualAddressPage(index: Index) extends NominalsQuestionPage[Address]("individualAddress")

final case class IndividualContactDetailsPage(index: Index)
    extends NominalsQuestionPage[IndividualContactDetails]("individualContactDetails")

final case class IndividualNationalInsuranceNumberPage(index: Index)
    extends NominalsQuestionPage[String]("individualNationalInsuranceNumber")

/** END Checked Info Pages */

final case class IndividualConnectionPage(index: Index)
    extends NominalsQuestionPage[IndividualConnection]("individualConnection")

final case class IndividualBusinessDetailsPage(index: Index)
    extends NominalsQuestionPage[IndividualBusinessDetails]("individualBusinessDetails") {

  override def cleanup(value: Option[IndividualBusinessDetails], userAnswers: UserAnswers): Try[UserAnswers] =
    if (value exists { _ != Yes })
      userAnswers.remove(BusinessInformationCheckPage(index)).flatMap(_.remove(BusinessNamePage(index))).flatMap(
          _.remove(TypeBusinessPage(index))).flatMap(
          _.remove(BusinessAddressPage(index))).flatMap(
          _.remove(BusinessContactDetailsPage(index))).flatMap(
          _.remove(ReferenceNumbersPage(index))).flatMap(
        _.remove(SelectConnectionBusinessPage(index)))
    else super.cleanup(value, userAnswers)
}

final case class IndividualConfirmRemovePage(index: Index)
    extends NominalsQuestionPage[Boolean]("individualConfirmRemove")

/** END   Individual journey
  * START Business journey */
final case class BusinessInformationCheckPage(index: Index)
    extends NominalsQuestionPage[Set[BusinessInformationCheck]]("businessInformationCheck")

/** START Checked Info */
final case class BusinessNamePage(index: Index) extends NominalsQuestionPage[String]("businessName")

final case class TypeBusinessPage(index: Index) extends NominalsQuestionPage[String]("typeBusiness")

final case class BusinessAddressPage(index: Index) extends NominalsQuestionPage[Address]("businessAddress")

final case class BusinessContactDetailsPage(index: Index)
    extends NominalsQuestionPage[BusinessContactDetails]("businessContactDetails")

final case class ReferenceNumbersPage(index: Index) extends NominalsQuestionPage[ReferenceNumbers]("referenceNumbers")

/** END Checked Info */

final case class SelectConnectionBusinessPage(index: Index)
    extends NominalsQuestionPage[SelectConnectionBusiness]("selectConnectionBusiness")

/** END Business journey */
