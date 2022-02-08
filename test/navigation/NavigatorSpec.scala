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

package navigation

import base.SpecBase
import controllers.routes
import pages._
import models._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator                = new Navigator
  val individualInformationGen = Gen.containerOf[Set, IndividualInformation](Gen.oneOf(IndividualInformation.values))

  val businessInformationCheckGen =
    Gen.containerOf[Set, BusinessInformationCheck](Gen.oneOf(BusinessInformationCheck.values))

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad
      }

      "must go from activity type page" - {

        "to the individual or business page when the user chooses an activity which HMRC is responsible for investigating" in {
          ActivityType.list.filterNot(
            activity => ActivityType.otherDepartments.isDefinedAt(activity.activityName)
          ).foreach { activity =>
            val result = navigator.nextPage(
              ActivityTypePage,
              NormalMode,
              UserAnswers("id").set(ActivityTypePage, activity).success.value
            )
            result mustBe routes.IndividualOrBusinessController.onPageLoad(NormalMode)
          }
        }

        "to the do not use this service page when the user chooses an activity which HMRC is not responsible for investigating" in {
          ActivityType.list.filter(
            activity => ActivityType.otherDepartments.isDefinedAt(activity.activityName)
          ).foreach { activity =>
            val result = navigator.nextPage(
              ActivityTypePage,
              NormalMode,
              UserAnswers("id").set(ActivityTypePage, activity).success.value
            )
            result mustBe routes.DoNotUseThisServiceController.onPageLoad()
          }
        }

        "to the journey recovery controller if there is no activity type set" in {
          navigator.nextPage(
            ActivityTypePage,
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from individual or business page" - {

        "to the individual information check page for the first individual" in {
          val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Individual).success.value
          navigator.nextPage(
            IndividualOrBusinessPage,
            NormalMode,
            answers
          ) mustBe routes.IndividualInformationController.onPageLoad(Index(0), NormalMode)
        }

        "to the individual information check page for the first business" in {
          val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Business).success.value
          navigator.nextPage(
            IndividualOrBusinessPage,
            NormalMode,
            answers
          ) mustBe routes.BusinessInformationCheckController.onPageLoad(Index(0), NormalMode)
        }

        "to the business information check page" ignore {}

        "to the journey recovery controller if there is no individual or business set" in {
          navigator.nextPage(
            IndividualOrBusinessPage,
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual information check page" - {

        "to the individual name page if the user has selected  name" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val answer      = individualInformationAnswer + IndividualInformation.Name
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualInformationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNameController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual date format page if the user has selected age and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(IndividualInformation.Name)
            val answer          = individualInformationAnswer -- previousAnswers + IndividualInformation.Age
            val userAnswers     = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualInformationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualDateFormatController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual address page if the user has selected address and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(IndividualInformation.Name, IndividualInformation.Age)
            val answer          = individualInformationAnswer -- previousAnswers + IndividualInformation.Address
            val userAnswers     = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualInformationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualAddressRedirectController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual contact details page if the user has selected contact details and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age, IndividualInformation.Address)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.ContactDetails
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualInformationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualContactDetailsController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual nino page if the user has selected nino and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.NiNumber
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualInformationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNationalInsuranceNumberController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery page if there is no individual information set" in {
          navigator.nextPage(
            IndividualInformationPage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual name page" - {

        "to the individual date format page if the user has selected age" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(IndividualInformation.Name)
            val answer          = individualInformationAnswer -- previousAnswers + IndividualInformation.Age
            val userAnswers     = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualNamePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualDateFormatController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual address page if the user has selected address and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(IndividualInformation.Name, IndividualInformation.Age)
            val answer          = individualInformationAnswer -- previousAnswers + IndividualInformation.Address
            val userAnswers     = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualNamePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualAddressRedirectController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual contact details page if the user has selected contact details and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age, IndividualInformation.Address)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.ContactDetails
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualNamePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualContactDetailsController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual nio page if the user has selected nino and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.NiNumber
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualNamePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNationalInsuranceNumberController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual connection page if there are no following options selected" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val followingAnswers = Set(
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails,
              IndividualInformation.NiNumber
            )
            val answer      = individualInformationAnswer -- followingAnswers
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualNamePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualConnectionController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualNamePage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from individual date format page" - {

        "to the individual approximate age page when they select 'age'" in {
          val answers =
            UserAnswers("id").set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Age).success.value
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            NormalMode,
            answers
          ) mustBe routes.IndividualAgeController.onPageLoad(Index(0), NormalMode)
        }

        "to the individual date of birth page when they select 'date of birth'" in {
          val answers =
            UserAnswers("id").set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Date).success.value
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            NormalMode,
            answers
          ) mustBe routes.IndividualDateOfBirthController.onPageLoad(Index(0), NormalMode)
        }

        "to the journey recovery controller if there is no individual date format set" in {
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual age page" - {

        "to the address page if the user has selected address" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.Address
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAgePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualAddressRedirectController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual contact details page if the user has selected contact details and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age, IndividualInformation.Address)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.ContactDetails
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAgePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualContactDetailsController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual nino page if the user has selected nino and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.NiNumber
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAgePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNationalInsuranceNumberController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual connection page if there are no following options selected" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val followingAnswers =
              Set(IndividualInformation.Address, IndividualInformation.ContactDetails, IndividualInformation.NiNumber)
            val answer      = individualInformationAnswer -- followingAnswers
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAgePage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualConnectionController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualAgePage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual date of birth page" - {

        "to the address page if the user has selected address" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.Address
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualDateOfBirthPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualAddressRedirectController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual contact details page if the user has selected contact details and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age, IndividualInformation.Address)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.ContactDetails
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualDateOfBirthPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualContactDetailsController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual nino page if the user has selected nino and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.NiNumber
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualDateOfBirthPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNationalInsuranceNumberController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual connection page if there are no following options selected" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val followingAnswers =
              Set(IndividualInformation.Address, IndividualInformation.ContactDetails, IndividualInformation.NiNumber)
            val answer      = individualInformationAnswer -- followingAnswers
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualDateOfBirthPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualConnectionController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualDateOfBirthPage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual address flow" - {

        "to the individual contact details page if the user has selected contact details and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.ContactDetails
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAddressConfirmationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualContactDetailsController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the nino page if the user has selected nino and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.NiNumber
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAddressConfirmationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNationalInsuranceNumberController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual connection page if there are no following options selected" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val followingAnswers = Set(IndividualInformation.ContactDetails, IndividualInformation.NiNumber)
            val answer           = individualInformationAnswer -- followingAnswers
            val userAnswers      = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAddressConfirmationPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualConnectionController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualAddressConfirmationPage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual contact details page" - {

        "to the individual nino page if the user has selected nino and has not selected previous answers" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val previousAnswers = Set(
              IndividualInformation.Name,
              IndividualInformation.Age,
              IndividualInformation.Address,
              IndividualInformation.ContactDetails
            )
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.NiNumber
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualContactDetailsPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualNationalInsuranceNumberController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the individual connection page if there are no following options selected" in {
          forAll(individualInformationGen) { individualInformationAnswer =>
            val followingAnswers = Set(IndividualInformation.NiNumber)
            val answer           = individualInformationAnswer -- followingAnswers
            val userAnswers      = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualContactDetailsPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualConnectionController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualContactDetailsPage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from the individual nino page" - {

        "to the individual connection page" in {
          forAll(individualInformationGen) { answer =>
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualNationalInsuranceNumberPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.IndividualConnectionController.onPageLoad(Index(0), NormalMode)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualNationalInsuranceNumberPage(Index(0)),
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }
    }

    "must go from the business information check page" - {

      "to the business name page if the user has selected business name" in {
        forAll(businessInformationCheckGen) { businessInformationCheckCheckAnswer =>
          val answer      = businessInformationCheckCheckAnswer + BusinessInformationCheck.Name
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessNameController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business type page if the user has selected business type and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationCheckCheckAnswer =>
          val previousAnswers = Set(BusinessInformationCheck.Name)
          val answer          = businessInformationCheckCheckAnswer -- previousAnswers + BusinessInformationCheck.Type
          val userAnswers     = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.TypeBusinessController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business address page if the user has selected business address and has not selected previous answers" ignore {
        // TODO when address pages are merged
      }

      "to the business contact details page if the user has selected contact details and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationCheckCheckAnswer =>
          val previousAnswers =
            Set(BusinessInformationCheck.Name, BusinessInformationCheck.Type, BusinessInformationCheck.Address)
          val answer      = businessInformationCheckCheckAnswer -- previousAnswers + BusinessInformationCheck.Contact
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            TypeBusinessPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessContactDetailsController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business reference page if the user has selected business reference and has not selected previous answers" ignore {
        // TODO when reference pages are merged
      }

      "to the journey recovery page if there is no business information set" in {
        navigator.nextPage(
          BusinessInformationCheckPage(Index(0)),
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from the business name page" - {

      "to the business type format page if the user has selected business type" in {
        forAll(businessInformationCheckGen) { businesslInformationAnswer =>
          val previousAnswers = Set(BusinessInformationCheck.Name)
          val answer          = businesslInformationAnswer -- previousAnswers + BusinessInformationCheck.Type
          val userAnswers     = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessNamePage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.TypeBusinessController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business address page if the user has selected address and has not selected previous answers" ignore {
        // TODO when address pages are done
      }

      "to the business contact details page if the user has selected contact details and has not selected previous answers" ignore {
        // TODO when address pages are done
      }

      "to the business reference page if the user has selected reference and has not selected previous answers" ignore {
        // TODO when address pages are done
      }

      "to the connection page if there are no following options selected" in {
        forAll(businessInformationCheckGen) { businesslInformationAnswer =>
          val followingAnswers = Set(
            BusinessInformationCheck.Name,
            BusinessInformationCheck.Type,
            BusinessInformationCheck.Address,
            BusinessInformationCheck.Contact,
            BusinessInformationCheck.BusinessReference
          )
          val answer      = businesslInformationAnswer -- followingAnswers
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessNamePage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.SelectConnectionBusinessController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the journey recovery controller if there is no business check information set" in {
        navigator.nextPage(
          BusinessNamePage(Index(0)),
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from the business address flow" ignore {
      // TODO add when pages are merged
    }

    "to the journey recovery controller if there is no business information set" in {
      navigator.nextPage(
        BusinessNamePage(Index(0)),
        NormalMode,
        UserAnswers("id")
      ) mustBe routes.JourneyRecoveryController.onPageLoad()
    }

    "must go from add another person page" - {

      "to the add another person page page for the yes" in {
        val answers = UserAnswers("id").set(AddAnotherPersonPage(Index(0)), AddAnotherPerson.Yes).success.value
        navigator.nextPage(
          AddAnotherPersonPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.IndividualInformationController.onPageLoad(Index(0), NormalMode)
      }

      "to the add another person page for the no" in {
        val answers = UserAnswers("id").set(AddAnotherPersonPage(Index(0)), AddAnotherPerson.No).success.value
        navigator.nextPage(
          AddAnotherPersonPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.ApproximateValueController.onPageLoad(NormalMode)
      }

      "to the journey recovery controller if there is no individual or business set" in {
        navigator.nextPage(
          IndividualOrBusinessPage,
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from select connection business page" - {

      "to the select connection business page for the first selection" in {
        val answers = UserAnswers("id").set(
          SelectConnectionBusinessPage(Index(0)),
          SelectConnectionBusiness.CurrentEmployer
        ).success.value
        navigator.nextPage(
          SelectConnectionBusinessPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.ApproximateValueController.onPageLoad(NormalMode)
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          CheckMode,
          UserAnswers("id")
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }
  }
}
