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

import scala.util.matching.Regex

object Validation {

  val vatRegistrationPattern: Regex = "(?:[Gg][Bb])?\\d{9}".r.anchored
  val utrPattern: Regex             = "\\d{10}".r.anchored
  val payeReferencePattern: Regex   = """\d{3}/[A-Za-z0-9]{1,10}""".r.anchored
  val validString: Regex            = "(\\?+|\\*+)+".r.unanchored
  val validAddress: Regex           = "^(?!\\?|\\*).*".r.unanchored

  val ukPostCode: Regex =
    "^([A-Za-z][A-Ha-hJ-Yj-y]?[0-9][A-Za-z0-9]? ?[0-9][A-Za-z]{2}|[Gg][Ii][Rr] ?0[Aa]{2})$".r.anchored

}
