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

package forms.mappings

import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.apache.commons.validator.routines.EmailValidator

import java.time.LocalDate
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.math.BigDecimal.RoundingMode
import scala.util.{Success, Try}

trait Constraints {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>
        import ev._

        if (input >= minimum)
          Valid
        else
          Invalid(errorKey, minimum)
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>
        import ev._

        if (input <= maximum)
          Valid
        else
          Invalid(errorKey, maximum)
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>
        import ev._

        if (input >= minimum && input <= maximum)
          Valid
        else
          Invalid(errorKey, minimum, maximum)
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def validEmailAddress(errorKey: String): Constraint[String] =
    Constraint {
      case str: String if EmailValidator.getInstance().isValid(str) => Valid
      case _                                                        => Invalid(errorKey)
    }

  protected def telephoneNumberValidation(errorMessage: String = "error.invalid"): Constraint[String] =
    Constraint {
      str =>
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        Try(phoneNumberUtil.isPossibleNumber(phoneNumberUtil.parse(str, "GB"))) match {
          case Success(true) => Valid
          case _             => Invalid(errorMessage)
        }
    }

  protected def maxTwoDecimalsWithTwelveChars(): Constraint[BigDecimal] = Constraint { value =>
    val maxChars = 12
    value.scale match {
      case s if s > 2                                                                               => Invalid("approximateValue.error.maxTwoDecimals")
      case 0 if value.setScale(0, RoundingMode.DOWN) < Math.pow(10, maxChars)                       => Valid
      case 1 | 2 if value.setScale(0, RoundingMode.DOWN) < Math.pow(10, maxChars - value.scale - 1) => Valid
      case _                                                                                        => Invalid("approximateValue.error.length")
    }
  }

  protected def regexpRestrict(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Invalid(errorKey, regex)
      case _ =>
        Valid
    }

}
