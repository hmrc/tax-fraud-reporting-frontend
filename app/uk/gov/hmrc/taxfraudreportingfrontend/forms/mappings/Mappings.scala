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

package uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings

import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.{FieldMapping, FormError}
import uk.gov.hmrc.taxfraudreportingfrontend.models.Enumerable

import scala.language.postfixOps

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[String] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def int(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def boolean(
    requiredKey: String = "error.required",
    invalidKey: String = "error.boolean",
    args: Seq[String] = Seq.empty
  ): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))

  protected def enumerable[A](
    requiredKey: String = "error.required",
    invalidKey: String = "error.invalid",
    args: Seq[String] = Seq.empty
  )(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey, args))

  protected def atLeastOneOf(fields: Seq[String], msg: String): FieldMapping[String] =
    of(new Formatter[String] {

      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
        val values = fields flatMap { data get _ map (_.trim) } flatten

        if (values.isEmpty)
          Left(Seq(FormError(key, msg, Nil)))
        else
          Right(data.getOrElse(key, "").trim)
      }

      def unbind(key: String, value: String): Map[String, String] = Map(key -> value.trim)
    })

}
