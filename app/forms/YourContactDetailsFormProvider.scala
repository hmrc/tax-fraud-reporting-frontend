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

import com.google.i18n.phonenumbers.PhoneNumberUtil
import forms.mappings.Mappings
import models.YourContactDetails
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject
import scala.util.{Success, Try}

class YourContactDetailsFormProvider @Inject() extends Mappings {

  private def telephoneNumberValidation(errorMessage: String = "error.invalid"): Constraint[String] =
    Constraint {
      str =>
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        Try(phoneNumberUtil.isPossibleNumber(phoneNumberUtil.parse(str, "GB"))) match {
          case Success(true) => Valid
          case _             => Invalid(errorMessage)
        }
    }

  private val errorPrefix = "yourContactDetails.error"

  private def field(key: String, isOptional: Boolean = false) = {
    def errorMsg(problem: String) = s"$errorPrefix.$key.$problem"

    val textMapping = if (isOptional) text() else text(errorMsg("required"))

    textMapping verifying maxLength(255, errorMsg("length"))
  }

  def apply(): Form[YourContactDetails] = Form(
    mapping(
      "firstName" -> field("firstName"),
      "lastName"  -> field("lastName"),
      "tel" -> (
        field("tel") verifying telephoneNumberValidation(errorPrefix + ".tel.invalid")
      ),
      "email" -> optional(
        field("email", isOptional = true) verifying validEmailAddress(errorPrefix + ".email.invalid")
      ),
      "memorableWord" -> field("memorableWord")
    )(YourContactDetails.apply)(YourContactDetails.unapply)
  )

}
