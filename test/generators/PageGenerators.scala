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

import models.Index
import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryReferenceNumbersPage: Arbitrary[ReferenceNumbersPage] =
    Arbitrary(ReferenceNumbersPage(Index(0)))

  implicit lazy val arbitrarySelectConnectionBusinessPage: Arbitrary[SelectConnectionBusinessPage] =
    Arbitrary(SelectConnectionBusinessPage(Index(0)))

  implicit lazy val arbitraryApproximateValuePage: Arbitrary[ApproximateValuePage.type] =
    Arbitrary(ApproximateValuePage)

  implicit lazy val arbitraryDescriptionActivityPage: Arbitrary[DescriptionActivityPage.type] =
    Arbitrary(DescriptionActivityPage)

  implicit lazy val arbitraryTypeBusinessPage: Arbitrary[TypeBusinessPage] =
    Arbitrary(TypeBusinessPage(Index(0)))

  implicit lazy val arbitraryBusinessInformationCheckPage: Arbitrary[BusinessInformationCheckPage] =
    Arbitrary(BusinessInformationCheckPage(Index(0)))

  implicit lazy val arbitraryIndividualAgePage: Arbitrary[IndividualAgePage] =
    Arbitrary(IndividualAgePage(Index(0)))

  implicit lazy val arbitraryIndividualDateOfBirthPage: Arbitrary[IndividualDateOfBirthPage] =
    Arbitrary(IndividualDateOfBirthPage(Index(0)))

  implicit lazy val arbitraryDateFormatPage: Arbitrary[IndividualDateFormatPage] =
    Arbitrary(IndividualDateFormatPage(Index(0)))

  implicit lazy val arbitraryBusinessNamePage: Arbitrary[BusinessNamePage] =
    Arbitrary(BusinessNamePage(Index(0)))

  implicit lazy val arbitraryIndividualBusinessDetailsPage: Arbitrary[IndividualBusinessDetailsPage] =
    Arbitrary(IndividualBusinessDetailsPage(Index(0)))

  implicit lazy val arbitraryIndividualConnectionPage: Arbitrary[IndividualConnectionPage] =
    Arbitrary(IndividualConnectionPage(Index(0)))

  implicit lazy val arbitraryIndividualNationalInsuranceNumberPage: Arbitrary[IndividualNationalInsuranceNumberPage] =
    Arbitrary(IndividualNationalInsuranceNumberPage(Index(0)))

  implicit lazy val arbitraryIndividualContactDetailsPage: Arbitrary[IndividualContactDetailsPage] =
    Arbitrary(IndividualContactDetailsPage(Index(0)))

  implicit lazy val arbitraryIndividualNamePage: Arbitrary[IndividualNamePage] =
    Arbitrary(IndividualNamePage(Index(0)))

  implicit lazy val arbitraryIndividualInformationPage: Arbitrary[IndividualInformationPage] =
    Arbitrary(IndividualInformationPage(Index(0)))

  implicit lazy val arbitraryIndividualOrBusinessPage: Arbitrary[IndividualOrBusinessPage.type] =
    Arbitrary(IndividualOrBusinessPage)

  implicit lazy val arbitraryActivityTypePage: Arbitrary[ActivityTypePage.type] =
    Arbitrary(ActivityTypePage)

}
