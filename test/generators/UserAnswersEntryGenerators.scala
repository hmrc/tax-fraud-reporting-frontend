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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryDateFormatUserAnswersEntry: Arbitrary[(DateFormatPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateFormatPage.type]
        value <- arbitrary[DateFormat].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBusinessDetailsUserAnswersEntry
    : Arbitrary[(IndividualBusinessDetailsPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBusinessDetailsPage]
        value <- arbitrary[IndividualBusinessDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualConnectionUserAnswersEntry: Arbitrary[(IndividualConnectionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualConnectionPage]
        value <- arbitrary[IndividualConnection].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualNationalInsuranceNumberUserAnswersEntry
    : Arbitrary[(IndividualNationalInsuranceNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualNationalInsuranceNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualContactDetailsUserAnswersEntry
    : Arbitrary[(IndividualContactDetailsPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualContactDetailsPage]
        value <- arbitrary[IndividualContactDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualNameUserAnswersEntry: Arbitrary[(IndividualNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualNamePage]
        value <- arbitrary[IndividualName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualInformationUserAnswersEntry: Arbitrary[(IndividualInformationPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualInformationPage]
        value <- arbitrary[IndividualInformation].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualOrBusinessUserAnswersEntry: Arbitrary[(IndividualOrBusinessPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualOrBusinessPage.type]
        value <- arbitrary[IndividualOrBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryActivityTypeUserAnswersEntry: Arbitrary[(ActivityTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ActivityTypePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

}
