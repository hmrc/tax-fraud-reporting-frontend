/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.taxfraudreportingfrontend.controllers.forms.behaviours

import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.{Form, FormError}
import uk.gov.hmrc.taxfraudreportingfrontend.controllers.forms.FormSpec

trait FieldBehaviours extends FormSpec with ScalaCheckPropertyChecks {

  def fieldThatBindsValidData(form: Form[_], fieldName: String, validDataGenerator: Gen[String]): Unit =
    "bind valid data" in {

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value shouldBe dataItem
      }
    }

  def mandatoryField(form: Form[_], fieldName: String, requiredError: FormError): Unit = {

    "not bind when key is not present at all" in {

      val result = form.bind(emptyForm).apply(fieldName)
      result.errors shouldEqual Seq(requiredError)
    }

    "not bind blank values" in {

      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors shouldEqual Seq(requiredError)
    }
  }

  def optionalField(form: Form[_], fieldName: String): Unit = {

    "bind when key is not present" in {

      val result = form.bind(emptyForm).apply(fieldName)
      result.errors shouldBe empty
    }

    "bind blank values" in {

      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors shouldBe empty
    }
  }

  def fieldThatPreventsUnsafeInput(
    form: Form[_],
    fieldName: String,
    unsafeInputs: Gen[String],
    invalidError: FormError
  ): Unit =
    "prevent unsafe inputs" in {

      forAll(unsafeInputs) {
        input: String =>
          val result = form.bind(Map(fieldName -> input)).apply(fieldName)
          result.errors shouldEqual Seq(invalidError)
      }
    }

  def declarantReferencePreventsUnsafeInput(
    form: Form[_],
    fieldName: String,
    unsafeInputs: Gen[String],
    invalidError: FormError
  ): Unit =
    "prevent unsafe inputs" in {

      forAll(unsafeInputs) {
        input: String =>
          val result = form.bind(Map("value" -> "01", fieldName -> input)).apply(fieldName)
          result.errors shouldEqual Seq(invalidError)
      }
    }

}