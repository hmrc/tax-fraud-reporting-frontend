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

  implicit lazy val arbitraryBusinessConfirmAddressUserAnswersEntry: Arbitrary[(BusinessConfirmAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessConfirmAddressPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConfirmAddressUserAnswersEntry: Arbitrary[(ConfirmAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmAddressPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryZeroValidationUserAnswersEntry: Arbitrary[(ZeroValidationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ZeroValidationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessSelectCountryUserAnswersEntry: Arbitrary[(BusinessSelectCountryPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessSelectCountryPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualSelectCountryUserAnswersEntry
    : Arbitrary[(IndividualSelectCountryPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualSelectCountryPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualConfirmRemoveUserAnswersEntry
    : Arbitrary[(IndividualConfirmRemovePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualConfirmRemovePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryActivitySourceOfInformationUserAnswersEntry
    : Arbitrary[(ActivitySourceOfInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ActivitySourceOfInformationPage.type]
        value <- arbitrary[ActivitySourceOfInformation].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDocumentationDescriptionUserAnswersEntry
    : Arbitrary[(DocumentationDescriptionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DocumentationDescriptionPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYourContactDetailsUserAnswersEntry: Arbitrary[(YourContactDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourContactDetailsPage.type]
        value <- arbitrary[YourContactDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProvideContactDetailsUserAnswersEntry
    : Arbitrary[(ProvideContactDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProvideContactDetailsPage.type]
        value <- arbitrary[ProvideContactDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySupportingDocumentUserAnswersEntry: Arbitrary[(SupportingDocumentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SupportingDocumentPage.type]
        value <- arbitrary[SupportingDocument].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnotherPersonUserAnswersEntry: Arbitrary[(AddAnotherPersonPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnotherPersonPage.type]
        value <- arbitrary[AddAnotherPerson].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowManyPeopleKnowUserAnswersEntry: Arbitrary[(HowManyPeopleKnowPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowManyPeopleKnowPage.type]
        value <- arbitrary[HowManyPeopleKnow].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryActivityTimePeriodUserAnswersEntry: Arbitrary[(ActivityTimePeriodPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ActivityTimePeriodPage.type]
        value <- arbitrary[ActivityTimePeriod].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenActivityHappenUserAnswersEntry: Arbitrary[(WhenActivityHappenPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenActivityHappenPage.type]
        value <- arbitrary[WhenActivityHappen].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReferenceNumbersUserAnswersEntry: Arbitrary[(ReferenceNumbersPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReferenceNumbersPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySelectConnectionBusinessUserAnswersEntry
    : Arbitrary[(SelectConnectionBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SelectConnectionBusinessPage]
        value <- arbitrary[SelectConnectionBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessContactDetailsUserAnswersEntry: Arbitrary[(BusinessContactDetailsPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessContactDetailsPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryApproximateValueUserAnswersEntry: Arbitrary[(ApproximateValuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ApproximateValuePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDescriptionActivityUserAnswersEntry: Arbitrary[(DescriptionActivityPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DescriptionActivityPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTypeBusinessUserAnswersEntry: Arbitrary[(TypeBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeBusinessPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessInformationCheckUserAnswersEntry
    : Arbitrary[(BusinessInformationCheckPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessInformationCheckPage]
        value <- arbitrary[BusinessInformationCheck].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualAgeUserAnswersEntry: Arbitrary[(IndividualAgePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualAgePage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualDateOfBirthUserAnswersEntry: Arbitrary[(IndividualDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateFormatUserAnswersEntry: Arbitrary[(IndividualDateFormatPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualDateFormatPage]
        value <- arbitrary[IndividualDateFormat].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessNameUserAnswersEntry: Arbitrary[(BusinessNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
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

  implicit lazy val arbitraryChooseYourAddressPageAnswersEntry: Arbitrary[(ChooseYourAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChooseYourAddressPage]
        value <- arbitrary[ChooseYourAddressPage].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessChooseYourAddressPageAnswersEntry
    : Arbitrary[(BusinessChooseYourAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessChooseYourAddressPage]
        value <- arbitrary[BusinessChooseYourAddressPage].map(Json.toJson(_))
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
