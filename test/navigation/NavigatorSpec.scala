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
import services.ActivityTypeService

import scala.language.postfixOps

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {
  private val app       = applicationBuilder().build()
  private val navigator = app.injector.instanceOf[Navigator]

  private val individualInformationGen =
    Gen.containerOf[Set, IndividualInformation](Gen.oneOf(IndividualInformation.values))

  private val businessInformationCheckGen: Gen[Set[BusinessInformationCheck]] =
    Gen.containerOf[Set, BusinessInformationCheck](Gen.oneOf(BusinessInformationCheck.values))

  "Navigator" - {

    val activityTypeService = app.injector.instanceOf[ActivityTypeService]

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad
      }

      "must go from activity type page" - {

        "to the individual or business page when the user chooses an activity which HMRC is responsible for investigating" in {
          activityTypeService.allActivities collect {
            case activity if activityTypeService getDepartmentFor activity._1 isEmpty =>
              val result = navigator.nextPage(
                ActivityTypePage,
                NormalMode,
                UserAnswers("id").set(ActivityTypePage, activity._1).success.value
              )
              result mustBe routes.IndividualOrBusinessController.onPageLoad(NormalMode)
          }
        }

        "to the do not use this service page when the user chooses an activity which HMRC is not responsible for investigating" in {
          activityTypeService.allActivities collect {
            case activity if activityTypeService getDepartmentFor activity._1 nonEmpty =>
              val result = navigator.nextPage(
                ActivityTypePage,
                NormalMode,
                UserAnswers("id").set(ActivityTypePage, activity._1).success.value
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

        "to the business information check page for the first business" in {
          val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Business).success.value
          navigator.nextPage(
            IndividualOrBusinessPage,
            NormalMode,
            answers
          ) mustBe routes.BusinessInformationCheckController.onPageLoad(Index(0), NormalMode)
        }

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
            ) mustBe routes.IndividualSelectCountryController.onPageLoad(Index(0), NormalMode)
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
            ) mustBe routes.IndividualSelectCountryController.onPageLoad(Index(0), NormalMode)
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
            ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), false)
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
            ) mustBe routes.IndividualSelectCountryController.onPageLoad(Index(0), NormalMode)
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
            ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), false)
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
            ) mustBe routes.IndividualSelectCountryController.onPageLoad(Index(0), NormalMode)
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
            ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), false)
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
            val previousAnswers =
              Set(IndividualInformation.Name, IndividualInformation.Age, IndividualInformation.Address)
            val answer      = individualInformationAnswer -- previousAnswers + IndividualInformation.ContactDetails
            val userAnswers = UserAnswers("id").set(IndividualInformationPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualAddressPage(Index(0)),
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
              IndividualAddressPage(Index(0)),
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
              IndividualAddressPage(Index(0)),
              NormalMode,
              userAnswers
            ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), false)
          }
        }

        "to the journey recovery controller if there is no individual information set" in {
          navigator.nextPage(
            IndividualAddressPage(Index(0)),
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
            ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), false)
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
            ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), false)
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

      "to the business address page if the user has selected business address and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers = Set(BusinessInformationCheck.Name, BusinessInformationCheck.Type)
          val answer          = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.Address
          val userAnswers     = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessSelectCountryController.onPageLoad(Index(0), NormalMode)
        }
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

      "to the business reference details page if the user has selected contact details and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationCheckCheckAnswer =>
          val previousAnswers =
            Set(
              BusinessInformationCheck.Name,
              BusinessInformationCheck.Type,
              BusinessInformationCheck.Address,
              BusinessInformationCheck.Contact
            )
          val answer =
            businessInformationCheckCheckAnswer -- previousAnswers + BusinessInformationCheck.BusinessReference
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            ReferenceNumbersPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), true)
        }
      }

      "to the business reference page if the user has selected business reference and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers = Set(
            BusinessInformationCheck.Name,
            BusinessInformationCheck.Type,
            BusinessInformationCheck.Address,
            BusinessInformationCheck.Contact
          )
          val answer      = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.BusinessReference
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.ReferenceNumbersController.onPageLoad(Index(0), NormalMode)
        }
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

      "to the business address page if the user has selected address and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers = Set(BusinessInformationCheck.Name, BusinessInformationCheck.Type)
          val answer          = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.Address
          val userAnswers     = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessSelectCountryController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business contact details page if the user has selected contact details and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers =
            Set(BusinessInformationCheck.Name, BusinessInformationCheck.Type, BusinessInformationCheck.Address)
          val answer      = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.Contact
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessContactDetailsController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business reference page if the user has selected reference and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers = Set(
            BusinessInformationCheck.Name,
            BusinessInformationCheck.Type,
            BusinessInformationCheck.Address,
            BusinessInformationCheck.Contact
          )
          val answer      = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.BusinessReference
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.ReferenceNumbersController.onPageLoad(Index(0), NormalMode)
        }
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
          ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), true)
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

    "must go from the business address flow" - {

      "to the business address page if the user has selected address and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers = Set(BusinessInformationCheck.Name, BusinessInformationCheck.Type)
          val answer          = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.Address
          val userAnswers     = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessSelectCountryController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business contact details page if the user has selected contact details and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers =
            Set(BusinessInformationCheck.Name, BusinessInformationCheck.Type, BusinessInformationCheck.Address)
          val answer      = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.Contact
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.BusinessContactDetailsController.onPageLoad(Index(0), NormalMode)
        }
      }

      "to the business reference page if the user has selected reference and has not selected previous answers" in {
        forAll(businessInformationCheckGen) { businessInformationAnswer =>
          val previousAnswers = Set(
            BusinessInformationCheck.Name,
            BusinessInformationCheck.Type,
            BusinessInformationCheck.Address,
            BusinessInformationCheck.Contact
          )
          val answer      = businessInformationAnswer -- previousAnswers + BusinessInformationCheck.BusinessReference
          val userAnswers = UserAnswers("id").set(BusinessInformationCheckPage(Index(0)), answer).success.value
          navigator.nextPage(
            BusinessInformationCheckPage(Index(0)),
            NormalMode,
            userAnswers
          ) mustBe routes.ReferenceNumbersController.onPageLoad(Index(0), NormalMode)
        }
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
          ) mustBe routes.ConfirmAddressController.onPageLoad(Index(0), true)
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

    "to the journey recovery controller if there is no business information set" in {
      navigator.nextPage(
        BusinessNamePage(Index(0)),
        NormalMode,
        UserAnswers("id")
      ) mustBe routes.JourneyRecoveryController.onPageLoad()
    }

    "must go from add another person page" - {

      "to the individual information page page when the user answers yes" in {
        val answers = UserAnswers("id")
          .set(AddAnotherPersonPage, AddAnotherPerson.Yes).success.value
          .set(IndividualInformationPage(Index(0)), IndividualInformation.values.toSet).success.value
        navigator.nextPage(
          AddAnotherPersonPage,
          NormalMode,
          answers
        ) mustBe routes.IndividualInformationController.onPageLoad(Index(1), NormalMode)
      }

      "to the total value of the activity page when the user answers no" in {
        val answers = UserAnswers("id").set(AddAnotherPersonPage, AddAnotherPerson.No).success.value
        navigator.nextPage(
          AddAnotherPersonPage,
          NormalMode,
          answers
        ) mustBe routes.ActivitySourceOfInformationController.onPageLoad(NormalMode)
      }

      "to the journey recovery controller if there is no individual or business set" in {
        navigator.nextPage(
          IndividualOrBusinessPage,
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from the are you sure you want to remove this individual page" - {

      "to the add another individual page if there is at least 1 individual" in {
        val answers = emptyUserAnswers
          .set(IndividualInformationPage(Index(0)), IndividualInformation.values.toSet).success.value
        navigator.nextPage(
          IndividualConfirmRemovePage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.AddAnotherPersonController.onPageLoad(NormalMode)
      }

      "to the individual or business page if there are no individuals" in {
        navigator.nextPage(
          IndividualConfirmRemovePage(Index(0)),
          NormalMode,
          emptyUserAnswers
        ) mustBe routes.IndividualOrBusinessController.onPageLoad(NormalMode)
      }
    }

    "must go from the individual check your answer page to the add another individual page" in {
      navigator.nextPage(
        IndividualCheckYourAnswersPage(Index(0)),
        NormalMode,
        emptyUserAnswers
      ) mustBe routes.AddAnotherPersonController.onPageLoad(NormalMode)
    }

    "must go from individual have business details page" - {

      "to the business information page for the answer yes" in {
        val answers =
          UserAnswers("id").set(IndividualBusinessDetailsPage(Index(0)), IndividualBusinessDetails.Yes).success.value
        navigator.nextPage(
          IndividualBusinessDetailsPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.BusinessInformationCheckController.onPageLoad(Index(0), NormalMode)
      }

      "to the add another person page for the answer no" in {
        val answers =
          UserAnswers("id").set(IndividualBusinessDetailsPage(Index(0)), IndividualBusinessDetails.No).success.value
        navigator.nextPage(
          IndividualBusinessDetailsPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.AddAnotherPersonController.onPageLoad(NormalMode)
      }

      "to the add another page for the answer don't know" in {
        val answers = UserAnswers("id").set(
          IndividualBusinessDetailsPage(Index(0)),
          IndividualBusinessDetails.DontKnow
        ).success.value
        navigator.nextPage(
          IndividualBusinessDetailsPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.AddAnotherPersonController.onPageLoad(NormalMode)
      }

      "to the journey recovery controller if there is no individual have business details set" in {
        navigator.nextPage(
          IndividualBusinessDetailsPage(Index(0)),
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from select connection business page" - {

      "to the add activity source information page for the business journey selection" in {
        val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Business).success.value
        navigator.nextPage(
          SelectConnectionBusinessPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.ActivitySourceOfInformationController.onPageLoad(NormalMode)
      }
      "to the add another person page for the individual journey selection" in {
        val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Individual).success.value
        navigator.nextPage(
          SelectConnectionBusinessPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.AddAnotherPersonController.onPageLoad(NormalMode)
      }

    }

    "must go from when activity start page" - {

      "to the what is the total value of the activity page for the first selection" in {
        val answers = UserAnswers("id").set(WhenActivityHappenPage, WhenActivityHappen.OverFiveYears).success.value
        navigator.nextPage(
          WhenActivityHappenPage,
          NormalMode,
          answers
        ) mustBe routes.ApproximateValueController.onPageLoad(NormalMode)
      }

      "to the When will the activity likely happen page for the it's going to happen in the future selection" in {
        val answers = UserAnswers("id").set(WhenActivityHappenPage, WhenActivityHappen.NotHappen).success.value
        navigator.nextPage(
          WhenActivityHappenPage,
          NormalMode,
          answers
        ) mustBe routes.ActivityTimePeriodController.onPageLoad(NormalMode)
      }

      "to the journey recovery controller if there is no period time set" in {
        navigator.nextPage(
          WhenActivityHappenPage,
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }

    }

    "must go from approximate total value page" - {

      "to the how many people know about the activity page for the next selection" in {
        val answers = UserAnswers("id").set(ApproximateValuePage, BigDecimal(45.99)).success.value
        navigator.nextPage(
          ApproximateValuePage,
          NormalMode,
          answers
        ) mustBe routes.HowManyPeopleKnowController.onPageLoad(NormalMode)
      }
    }

    "must go from when activity likely happen page" - {

      "to the what is the total value of the activity page for the first selection" in {
        val answers = UserAnswers("id").set(ActivityTimePeriodPage, ActivityTimePeriod.NextWeek).success.value
        navigator.nextPage(
          ActivityTimePeriodPage,
          NormalMode,
          answers
        ) mustBe routes.ApproximateValueController.onPageLoad(NormalMode)
      }
    }

    "must go from how do you know the individual page" - {

      "to the does the individual have business details page for the first selection" in {
        val answers =
          UserAnswers("id").set(IndividualConnectionPage(Index(0)), IndividualConnection.Partner).success.value
        navigator.nextPage(
          IndividualConnectionPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.IndividualBusinessDetailsController.onPageLoad(Index(0), NormalMode)
      }
    }

    "must go from provide description of the activity you are reporting page" - {

      "to the how many other people know about the activity page for the next selection" in {
        val answers = UserAnswers("id").set(DescriptionActivityPage, "test").success.value
        navigator.nextPage(
          DescriptionActivityPage,
          NormalMode,
          answers
        ) mustBe routes.ProvideContactDetailsController.onPageLoad(NormalMode)
      }
    }

    "must go from how many other people know about the activity page" - {

      "to the do you wish to provide your contact details page for the first selection" in {
        navigator.nextPage(
          HowManyPeopleKnowPage,
          NormalMode,
          emptyUserAnswers
        ) mustBe routes.DescriptionActivityController.onPageLoad(NormalMode)
      }

    }

    "must go from do you have any supporting information page" - {

      "to the describe any supporting information page for the answer yes" in {
        val answers = UserAnswers("id").set(SupportingDocumentPage, SupportingDocument.Yes).success.value
        navigator.nextPage(
          SupportingDocumentPage,
          NormalMode,
          answers
        ) mustBe routes.DocumentationDescriptionController.onPageLoad(NormalMode)
      }

      "to the check your answers page for the answer no" in {
        val answers = UserAnswers("id").set(SupportingDocumentPage, SupportingDocument.No).success.value
        navigator.nextPage(
          SupportingDocumentPage,
          NormalMode,
          answers
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "to the journey recovery controller if there is no do you have any supporting information set" in {
        navigator.nextPage(
          SupportingDocumentPage,
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from do you wish to provide your contact details page" - {

      "to the what are your details page for the answer yes" in {
        val answers = UserAnswers("id").set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
        navigator.nextPage(
          ProvideContactDetailsPage,
          NormalMode,
          answers
        ) mustBe routes.YourContactDetailsController.onPageLoad(NormalMode)
      }

      "to the check your answers page for the answer no" in {
        val answers = UserAnswers("id").set(ProvideContactDetailsPage, ProvideContactDetails.No).success.value
        navigator.nextPage(
          ProvideContactDetailsPage,
          NormalMode,
          answers
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "to the journey recovery controller if there is no do you wish to provide your contact details set" in {
        navigator.nextPage(
          ProvideContactDetailsPage,
          NormalMode,
          UserAnswers("id")
        ) mustBe routes.JourneyRecoveryController.onPageLoad()
      }
    }

    "must go from how do you know this information page" - {

      "to the when activity happened page for the first selection" in {
        navigator.nextPage(
          ActivitySourceOfInformationPage,
          NormalMode,
          emptyUserAnswers
        ) mustBe routes.WhenActivityHappenController.onPageLoad(NormalMode)
      }

    }

    "must go from your details page" - {

      "to the Do you have any supporting information page for the first selection" in {
        navigator.nextPage(
          YourContactDetailsPage,
          NormalMode,
          emptyUserAnswers
        ) mustBe routes.SupportingDocumentController.onPageLoad(NormalMode)
      }

    }

    "must go from supporting information you currently have page" - {

      "to the check your answers page for the first selection" in {
        navigator.nextPage(
          DocumentationDescriptionPage,
          NormalMode,
          emptyUserAnswers
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

    }

    "must go from the individual select country page" - {

      "to the individual select country page if the user has selected address" in {
        val answers = UserAnswers("id").set(IndividualSelectCountryPage(Index(0)), "country").success.value
        navigator.nextPage(
          IndividualSelectCountryPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.IndividualAddressController.onPageLoad(Index(0), NormalMode)
      }
    }

    "must go from the business select country page" - {

      "to the business select country page if the user has selected address" in {
        val answers = UserAnswers("id").set(BusinessSelectCountryPage(Index(0)), "country").success.value
        navigator.nextPage(
          BusinessSelectCountryPage(Index(0)),
          NormalMode,
          answers
        ) mustBe routes.BusinessAddressController.onPageLoad(Index(0), NormalMode)
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

      "must go from the supporting document page" - {

        "to the document description page when the user selects yes and there is no answer for the document description" in {
          val answers = emptyUserAnswers.set(SupportingDocumentPage, SupportingDocument.Yes).success.value
          navigator.nextPage(
            SupportingDocumentPage,
            CheckMode,
            answers
          ) mustBe routes.DocumentationDescriptionController.onPageLoad(CheckMode)
        }

        "to the check your answers page when the user selects yes and there is an answer for the document description" in {
          val answers = emptyUserAnswers
            .set(SupportingDocumentPage, SupportingDocument.Yes).success.value
            .set(DocumentationDescriptionPage, "foobar").success.value
          navigator.nextPage(
            SupportingDocumentPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when the user selects no" in {
          val answers = emptyUserAnswers
            .set(SupportingDocumentPage, SupportingDocument.No).success.value
          navigator.nextPage(
            SupportingDocumentPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when there is no user selection" in {
          navigator.nextPage(
            SupportingDocumentPage,
            CheckMode,
            emptyUserAnswers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from the provide your contact details page" - {

        "to the your details page when the user selects yes and there is no answer for the your details" in {
          val answers = emptyUserAnswers.set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
          navigator.nextPage(
            YourContactDetailsPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when the user selects yes and there is an answer for the your details" in {
          val answers = emptyUserAnswers
            .set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
            .set(
              YourContactDetailsPage,
              YourContactDetails("firstname", "lastname", "tel", Some("email"), Some("test"))
            ).success.value
          navigator.nextPage(
            YourContactDetailsPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when the user selects no" in {
          val answers = emptyUserAnswers
            .set(ProvideContactDetailsPage, ProvideContactDetails.No).success.value
          navigator.nextPage(
            ProvideContactDetailsPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when there is no user selection" in {
          navigator.nextPage(
            YourContactDetailsPage,
            CheckMode,
            emptyUserAnswers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from when activity start page" - {

        "to the description of activity page for the first selection" in {
          val answers = emptyUserAnswers.set(WhenActivityHappenPage, WhenActivityHappen.OverFiveYears).success.value
          navigator.nextPage(
            HowManyPeopleKnowPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the When will the activity likely happen page for the it's going to happen in the future selection" in {
          val answers = emptyUserAnswers.set(WhenActivityHappenPage, WhenActivityHappen.NotHappen).success.value
          navigator.nextPage(
            ActivityTimePeriodPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when there is if there is no period time set" in {
          navigator.nextPage(
            ActivityTimePeriodPage,
            CheckMode,
            emptyUserAnswers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from the individual age format page" - {

        "to the date of birth page when the user selects Date of Birth and there is no answer for the approximate age" in {
          val answers =
            emptyUserAnswers.set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Date).success.value
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.IndividualDateOfBirthController.onPageLoad(Index(0), CheckMode)
        }

        "to the check your answers page when the user selects age" in {
          val answers = emptyUserAnswers
            .set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Age).success.value
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.IndividualAgeController.onPageLoad(Index(0), CheckMode)
        }

        "to the check your answers page when there is no user selection" in {
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            CheckMode,
            emptyUserAnswers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from the when activity start page" - {

        "to the It's going to happen in the future and there is no answer for the activity likely happen" in {
          val answers =
            emptyUserAnswers.set(WhenActivityHappenPage, WhenActivityHappen.NotHappen).success.value
          navigator.nextPage(
            WhenActivityHappenPage,
            CheckMode,
            answers
          ) mustBe routes.ActivityTimePeriodController.onPageLoad(CheckMode)
        }

        "to the check your answers page when the user selects over 5 years" in {
          val answers = emptyUserAnswers
            .set(WhenActivityHappenPage, WhenActivityHappen.OverFiveYears).success.value
          navigator.nextPage(
            WhenActivityHappenPage,
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

        "to the check your answers page when there is no user selection" in {
          navigator.nextPage(
            ActivityTimePeriodPage,
            CheckMode,
            emptyUserAnswers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

      }

      "must go to individual information check page of the individual after successful update from" - {

        def assertNavigationToIndividualCheckAnswersPage(currentPage: IndexedConfirmationPage) = {
          val index = currentPage.index
          forAll(individualInformationGen) { individualInformationAnswer =>
            val userAnswers =
              UserAnswers("id").set(IndividualInformationPage(index), individualInformationAnswer).success.value
            navigator.nextPage(
              currentPage,
              CheckMode,
              userAnswers
            ) mustBe routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
          }
        }

        "Name page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualNamePage(Index(0)))
        }

        "Date of Birth page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualDateOfBirthPage(Index(0)))
        }

        "Age page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualAgePage(Index(0)))
        }

        "Contact details page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualContactDetailsPage(Index(0)))
        }

        "Address Confirmation page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualAddressPage(Index(0)))
        }

        "National insurance number page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualNationalInsuranceNumberPage(Index(0)))
        }

        "Individual connection page" in {
          assertNavigationToIndividualCheckAnswersPage(IndividualConnectionPage(Index(0)))
        }

        "Business details - changed to No/Don't know" in {
          import IndividualBusinessDetails._
          forAll(Gen.oneOf(No, DontKnow)) { answer =>
            val answers =
              UserAnswers("id").set(IndividualBusinessDetailsPage(Index(0)), answer).success.value
            navigator.nextPage(
              IndividualBusinessDetailsPage(Index(0)),
              CheckMode,
              answers
            ) mustBe routes.IndividualCheckYourAnswersController.onPageLoad(Index(0), CheckMode)
          }
        }
      }

      "must go from select connection business page" - {

        "to check your submission page for business journey" in {
          val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Business).success.value
          navigator.nextPage(
            SelectConnectionBusinessPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }
        "to check individual information check after updating business details" in {
          val answers = UserAnswers("id").set(IndividualOrBusinessPage, IndividualOrBusiness.Individual).success.value
          navigator.nextPage(
            SelectConnectionBusinessPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.IndividualCheckYourAnswersController.onPageLoad(Index(0), CheckMode)
        }

      }

      "must go from the business select country page" - {

        "to the check your answers page when select country" in {
          val answers = UserAnswers("id").set(BusinessSelectCountryPage(Index(0)), "foobar").success.value
          navigator.nextPage(
            BusinessSelectCountryPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.BusinessAddressController.onPageLoad(Index(0), CheckMode)
        }

        "to the journey recovery controller if there is no activity type set" in {
          navigator.nextPage(
            BusinessSelectCountryPage(Index(0)),
            CheckMode,
            UserAnswers("id")
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

      }

      "must go from the individual select country page" - {

        "to the individual select country page if the user has selected address" in {
          val answers = UserAnswers("id").set(IndividualSelectCountryPage(Index(0)), "country").success.value
          navigator.nextPage(
            IndividualSelectCountryPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.IndividualAddressController.onPageLoad(Index(0), CheckMode)
        }

        "to the journey recovery controller if there is no activity type set" in {
          navigator.nextPage(
            IndividualSelectCountryPage(Index(0)),
            CheckMode,
            UserAnswers("id")
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

      }

      "must go from the individual select country page" - {

        "to the individual select country page if the user has selected address" in {
          val answers = UserAnswers("id").set(IndividualSelectCountryPage(Index(0)), "country").success.value
          navigator.nextPage(
            IndividualSelectCountryPage(Index(0)),
            CheckMode,
            answers
          ) mustBe routes.IndividualAddressController.onPageLoad(Index(0), CheckMode)
        }

        "to the journey recovery controller if there is no activity type set" in {
          navigator.nextPage(
            IndividualSelectCountryPage(Index(0)),
            CheckMode,
            UserAnswers("id")
          ) mustBe routes.CheckYourAnswersController.onPageLoad
        }

      }

    }
  }
}
