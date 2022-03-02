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

import models.{Index, IndividualDateFormat, UserAnswers}
import play.api.libs.json.JsPath

import scala.util.{Success, Try}

final case class IndividualDateFormatPage(index: Index) extends QuestionPage[IndividualDateFormat] {

  override def path: JsPath = JsPath \ "nominals" \ index.position \ toString

  override def toString: String = "dateFormat"

  override def cleanup(value: Option[IndividualDateFormat], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(IndividualDateFormat.Date) => userAnswers.remove(IndividualAgePage(index))
      case Some(IndividualDateFormat.Age)  => userAnswers.remove(IndividualDateOfBirthPage(index))
      case _                               => Success(userAnswers)
    }

}
