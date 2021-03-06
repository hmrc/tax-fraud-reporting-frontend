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

  implicit lazy val arbitraryBusinessConfirmAddressPage: Arbitrary[BusinessConfirmAddressPage] =
    Arbitrary(BusinessConfirmAddressPage(Index(0)))

  implicit lazy val arbitraryConfirmAddressPage: Arbitrary[ConfirmAddressPage] =
    Arbitrary(ConfirmAddressPage(Index(0)))

  implicit lazy val arbitraryZeroValidationPage: Arbitrary[ZeroValidationPage.type] =
    Arbitrary(ZeroValidationPage)

  implicit lazy val arbitraryBusinessSelectCountryPage: Arbitrary[BusinessSelectCountryPage] =
    Arbitrary(BusinessSelectCountryPage(Index(0)))

  implicit lazy val arbitraryIndividualSelectCountryPage: Arbitrary[IndividualSelectCountryPage] =
    Arbitrary(IndividualSelectCountryPage(Index(0)))

  implicit lazy val arbitraryIndividualConfirmRemovePage: Arbitrary[IndividualConfirmRemovePage] =
    Arbitrary(IndividualConfirmRemovePage(Index(0)))

  implicit lazy val arbitraryActivitySourceOfInformationPage: Arbitrary[ActivitySourceOfInformationPage.type] =
    Arbitrary(ActivitySourceOfInformationPage)

  implicit lazy val arbitraryDocumentationDescriptionPage: Arbitrary[DocumentationDescriptionPage.type] =
    Arbitrary(DocumentationDescriptionPage)

  implicit lazy val arbitraryYourContactDetailsPage: Arbitrary[YourContactDetailsPage.type] =
    Arbitrary(YourContactDetailsPage)

  implicit lazy val arbitraryProvideContactDetailsPage: Arbitrary[ProvideContactDetailsPage.type] =
    Arbitrary(ProvideContactDetailsPage)

  implicit lazy val arbitrarySupportingDocumentPage: Arbitrary[SupportingDocumentPage.type] =
    Arbitrary(SupportingDocumentPage)

  implicit lazy val arbitraryAddAnotherPersonPage: Arbitrary[AddAnotherPersonPage.type] =
    Arbitrary(AddAnotherPersonPage)

  implicit lazy val arbitraryHowManyPeopleKnowPage: Arbitrary[HowManyPeopleKnowPage.type] =
    Arbitrary(HowManyPeopleKnowPage)

  implicit lazy val arbitraryActivityTimePeriodPage: Arbitrary[ActivityTimePeriodPage.type] =
    Arbitrary(ActivityTimePeriodPage)

  implicit lazy val arbitraryWhenActivityHappenPage: Arbitrary[WhenActivityHappenPage.type] =
    Arbitrary(WhenActivityHappenPage)

  implicit lazy val arbitraryReferenceNumbersPage: Arbitrary[ReferenceNumbersPage] =
    Arbitrary(ReferenceNumbersPage(Index(0)))

  implicit lazy val arbitrarySelectConnectionBusinessPage: Arbitrary[SelectConnectionBusinessPage] =
    Arbitrary(SelectConnectionBusinessPage(Index(0)))

  implicit lazy val arbitraryBusinessContactDetailsPage: Arbitrary[BusinessContactDetailsPage] =
    Arbitrary(BusinessContactDetailsPage(Index(0)))

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

  implicit lazy val arbitraryChooseYourAddressPage: Arbitrary[ChooseYourAddressPage] =
    Arbitrary(ChooseYourAddressPage(Index(0)))

  implicit lazy val arbitraryBusinessChooseYourAddressPage: Arbitrary[BusinessChooseYourAddressPage] =
    Arbitrary(BusinessChooseYourAddressPage(Index(0)))

  implicit lazy val arbitraryIndividualInformationPage: Arbitrary[IndividualInformationPage] =
    Arbitrary(IndividualInformationPage(Index(0)))

  implicit lazy val arbitraryIndividualOrBusinessPage: Arbitrary[IndividualOrBusinessPage.type] =
    Arbitrary(IndividualOrBusinessPage)

  implicit lazy val arbitraryActivityTypePage: Arbitrary[ActivityTypePage.type] =
    Arbitrary(ActivityTypePage)

}
