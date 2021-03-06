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

import controllers.routes
import models.{WhenActivityHappen, _}
import pages._
import play.api.mvc.Call
import services.ActivityTypeService

import javax.inject.{Inject, Singleton}
import scala.language.postfixOps

@Singleton
class Navigator @Inject() (activityTypeService: ActivityTypeService) {

  private val normalRoutes: Page => UserAnswers => Call = {
    case ActivityTypePage         => activityPageRoutes
    case IndividualOrBusinessPage => individualOrBusinessRoutes

    /** START Individual journey fork */
    case IndividualInformationPage(index) => individualInformationRoutes(_, index)
    case IndividualNamePage(index)        => individualInformationRoutes(_, index, IndividualInformation.Name)
    case IndividualAgePage(index)         => individualInformationRoutes(_, index, IndividualInformation.Age)
    case IndividualDateFormatPage(index)  => ageFormatPageRoutes(_, index)
    case IndividualAddressPage(index)     => individualInformationRoutes(_, index, IndividualInformation.Address)
    case IndividualDateOfBirthPage(index) => individualInformationRoutes(_, index, IndividualInformation.Age)
    case IndividualContactDetailsPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.ContactDetails)
    case IndividualNationalInsuranceNumberPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.NiNumber)
    case IndividualSelectCountryPage(index) => individualSelectCountryPageRoute(_, index)
    case BusinessSelectCountryPage(index)   => businessSelectCountryPageRoute(_, index)
    case FindAddressPage(index)             => _ => routes.ChooseYourAddressController.onPageLoad(index, NormalMode)
    case BusinessFindAddressPage(index)     => _ => routes.BusinessChooseYourAddressController.onPageLoad(index, NormalMode)
    case ChooseYourAddressPage(index)       => _ => routes.ConfirmAddressController.onPageLoad(index, NormalMode)
    case ConfirmAddressPage(index)          => confirmAddressNormalModeRoute(_, index, NormalMode)
    case BusinessConfirmAddressPage(index)  => businessConfirmAddressNormalModeRoute(_, index, NormalMode)
    case BusinessChooseYourAddressPage(index) =>
      _ => routes.BusinessConfirmAddressController.onPageLoad(index, NormalMode)

    /** END Individual journey
      * Start Business journey */
    case BusinessNamePage(index)             => businessInformationRoutes(_, index, BusinessInformationCheck.Name)
    case TypeBusinessPage(index)             => businessInformationRoutes(_, index, BusinessInformationCheck.Type)
    case ReferenceNumbersPage(index)         => businessInformationRoutes(_, index, BusinessInformationCheck.BusinessReference)
    case BusinessContactDetailsPage(index)   => businessInformationRoutes(_, index, BusinessInformationCheck.Contact)
    case BusinessInformationCheckPage(index) => businessInformationRoutes(_, index, NormalMode)
    case BusinessAddressPage(index)          => businessInformationRoutes(_, index, BusinessInformationCheck.Address)
    case SelectConnectionBusinessPage(_)     => selectConnectionBusinessRoutes

    /** END Business journey */

    case AddAnotherPersonPage                 => addAnotherPersonRoutes
    case IndividualCheckYourAnswersPage(_)    => _ => routes.AddAnotherPersonController.onPageLoad(NormalMode)
    case IndividualConfirmRemovePage(_)       => individualConfirmRemoveRoutes
    case IndividualBusinessDetailsPage(index) => individualBusinessDetailsRoutes(_, index, NormalMode)
    case ApproximateValuePage                 => approximateValueRoutes
    case WhenActivityHappenPage               => whenActivityHappenRoutes
    case ActivityTimePeriodPage               => _ => routes.ApproximateValueController.onPageLoad(NormalMode)
    case IndividualConnectionPage(index) =>
      _ => routes.IndividualBusinessDetailsController.onPageLoad(index, NormalMode)
    case DescriptionActivityPage         => _ => routes.ProvideContactDetailsController.onPageLoad(NormalMode)
    case SupportingDocumentPage          => supportingDocumentRoutes
    case HowManyPeopleKnowPage           => _ => routes.DescriptionActivityController.onPageLoad(NormalMode)
    case ProvideContactDetailsPage       => provideContactDetailsRoutes
    case YourContactDetailsPage          => _ => routes.SupportingDocumentController.onPageLoad(NormalMode)
    case ActivitySourceOfInformationPage => _ => routes.WhenActivityHappenController.onPageLoad(NormalMode)
    case DocumentationDescriptionPage =>
      _ => routes.CheckYourAnswersController.onPageLoad
    case ZeroValidationPage => zeroValidationRoutes
    case _                  => _ => routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case SupportingDocumentPage               => supportingDocumentCheckRoutes
    case ProvideContactDetailsPage            => provideContactDetailsRoutes
    case WhenActivityHappenPage               => whenActivityHappenCheckRoutes
    case IndividualDateFormatPage(index)      => individualDateFormatPageCheckRoutes(_, index)
    case IndividualBusinessDetailsPage(index) => individualBusinessDetailsRoutes(_, index, CheckMode)
    case BusinessInformationCheckPage(index)  => businessInformationRoutes(_, index, CheckMode)
    case BusinessNamePage(index)              => businessInformationRoutes(_, index, BusinessInformationCheck.Name, CheckMode)
    case TypeBusinessPage(index)              => businessInformationRoutes(_, index, BusinessInformationCheck.Type, CheckMode)
    case ReferenceNumbersPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.BusinessReference, CheckMode)
    case BusinessContactDetailsPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Contact, CheckMode)
    case BusinessAddressPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Address, CheckMode)
    case SelectConnectionBusinessPage(index) => selectConnectionBusinessCheckRoute(_, index)
    case IndividualSelectCountryPage(index)  => individualSelectCountryPageRoutes(_, index, CheckMode)
    case BusinessSelectCountryPage(index)    => businessSelectCountryPageRoutes(_, index, CheckMode)
    case FindAddressPage(index)              => _ => routes.ChooseYourAddressController.onPageLoad(index, CheckMode)
    case BusinessFindAddressPage(index)      => _ => routes.BusinessChooseYourAddressController.onPageLoad(index, CheckMode)
    case ApproximateValuePage                => approximateValueCheckRoutes
    case ZeroValidationPage                  => zeroValidationCheckRoutes
    case ConfirmAddressPage(index)           => confirmAddressCheckModeRoutes(_, index, CheckMode)
    case BusinessConfirmAddressPage(index)   => businessConfirmAddressCheckModeRoutes(_, index, CheckMode)
    case p: IndexedConfirmationPage          => _ => routes.IndividualCheckYourAnswersController.onPageLoad(p.index, CheckMode)
    case _                                   => _ => routes.CheckYourAnswersController.onPageLoad
  }

  private val updateIndividualMode: Page => UserAnswers => Call = {
    case IndividualDateFormatPage(index)       => individualDateFormatPageUpdateRoutes(_, index)
    case IndividualCheckYourAnswersPage(index) => _ => routes.AddAnotherPersonController.onPageLoad(NormalMode)
    case IndividualSelectCountryPage(index)    => individualSelectCountryPageRoutes(_, index, UpdateIndividualMode)
    case FindAddressPage(index)                => _ => routes.ChooseYourAddressController.onPageLoad(index, UpdateIndividualMode)
    case IndividualBusinessDetailsPage(index)  => individualBusinessDetailsRoutes(_, index, UpdateIndividualMode)
    case BusinessInformationCheckPage(index)   => businessInformationRoutes(_, index, UpdateIndividualMode)
    case BusinessNamePage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Name, UpdateIndividualMode)
    case TypeBusinessPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Type, UpdateIndividualMode)
    case ReferenceNumbersPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.BusinessReference, UpdateIndividualMode)
    case BusinessContactDetailsPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Contact, UpdateIndividualMode)
    case BusinessAddressPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Address, UpdateIndividualMode)
    case SelectConnectionBusinessPage(index) => selectConnectionBusinessCheckRoute(_, index)
    case BusinessSelectCountryPage(index)    => businessSelectCountryPageRoutes(_, index, UpdateIndividualMode)
    case ConfirmAddressPage(index)           => confirmAddressCheckModeRoutes(_, index, UpdateIndividualMode)
    case BusinessConfirmAddressPage(index)   => businessConfirmAddressCheckModeRoutes(_, index, UpdateIndividualMode)
    case BusinessFindAddressPage(index) =>
      _ => routes.BusinessChooseYourAddressController.onPageLoad(index, UpdateIndividualMode)
    case p: IndexedConfirmationPage =>
      _ => routes.IndividualCheckYourAnswersController.onPageLoad(p.index, UpdateIndividualMode)
  }

  private def individualDateFormatPageUpdateRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualDateFormatPage(index)).map {
      case IndividualDateFormat.Date =>
        if (answers.get(IndividualDateOfBirthPage(index)).isDefined)
          routes.IndividualCheckYourAnswersController.onPageLoad(index, UpdateIndividualMode)
        else
          routes.IndividualDateOfBirthController.onPageLoad(index, UpdateIndividualMode)
      case IndividualDateFormat.Age =>
        if (answers.get(IndividualAgePage(index)).isDefined)
          routes.IndividualCheckYourAnswersController.onPageLoad(index, UpdateIndividualMode)
        else
          routes.IndividualAgeController.onPageLoad(index, UpdateIndividualMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def individualInformationRoute(answer: IndividualInformation, index: Index, mode: Mode): Call =
    answer match {
      case IndividualInformation.Name           => routes.IndividualNameController.onPageLoad(index, mode)
      case IndividualInformation.Age            => routes.IndividualDateFormatController.onPageLoad(index, mode)
      case IndividualInformation.Address        => routes.IndividualSelectCountryController.onPageLoad(index, mode)
      case IndividualInformation.ContactDetails => routes.IndividualContactDetailsController.onPageLoad(index, mode)
      case IndividualInformation.NiNumber       => routes.IndividualNationalInsuranceNumberController.onPageLoad(index, mode)
    }

  private def individualInformationRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualInformationPage(index)).flatMap { individualInformation =>
      IndividualInformation.values.find(individualInformation.contains).map(
        individualInformationRoute(_, index, NormalMode)
      )
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  def individualInformationRoutes(
    answers: UserAnswers,
    index: Index,
    answer: IndividualInformation,
    mode: Mode = NormalMode
  ): Call =
    answers get IndividualInformationPage(index) flatMap { checkedInfo =>
      val laterSections     = IndividualInformation.values.dropWhile(_ != answer).drop(1).toSet
      val remainingSections = checkedInfo & laterSections

      if (remainingSections.isEmpty) Some {
        mode match {
          case NormalMode => routes.IndividualConnectionController.onPageLoad(index, mode)
          case CheckMode =>
            routes.CheckYourAnswersController.onPageLoad
          case UpdateIndividualMode => routes.IndividualCheckYourAnswersController.onPageLoad(index, mode)
        }
      }
      else
        IndividualInformation.values find remainingSections.contains map {
          mode match {
            case NormalMode | UpdateIndividualMode => individualInformationRoute(_, index, mode)
            case CheckMode =>
              routes.CheckYourAnswersController.onPageLoad
              individualInformationRoute(_, index, mode)
          }
        }
    } getOrElse routes.JourneyRecoveryController.onPageLoad()

  private def ageFormatPageRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualDateFormatPage(index)).map {
      case IndividualDateFormat.Date => routes.IndividualDateOfBirthController.onPageLoad(index, NormalMode)
      case IndividualDateFormat.Age  => routes.IndividualAgeController.onPageLoad(index, NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def activityPageRoutes(answers: UserAnswers): Call =
    answers get ActivityTypePage match {
      case Some(activity) =>
        activityTypeService getDepartmentFor activity match {
          case Some(_) => routes.DoNotUseThisServiceController.onPageLoad()
          case None    => routes.IndividualOrBusinessController.onPageLoad(NormalMode)
        }
      case None => routes.JourneyRecoveryController.onPageLoad()
    }

  private def individualOrBusinessRoutes(answers: UserAnswers): Call =
    answers.get(IndividualOrBusinessPage).map {
      case IndividualOrBusiness.Individual =>
        routes.IndividualInformationController.onPageLoad(Index(0), NormalMode)
      case IndividualOrBusiness.Business =>
        routes.BusinessInformationCheckController.onPageLoad(Index(0), NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def businessInformationRoute(answer: BusinessInformationCheck, index: Index, mode: Mode): Call =
    answer match {
      case BusinessInformationCheck.Name              => routes.BusinessNameController.onPageLoad(index, mode)
      case BusinessInformationCheck.Type              => routes.TypeBusinessController.onPageLoad(index, mode)
      case BusinessInformationCheck.BusinessReference => routes.ReferenceNumbersController.onPageLoad(index, mode)
      case BusinessInformationCheck.Contact           => routes.BusinessContactDetailsController.onPageLoad(index, mode)
      case BusinessInformationCheck.Address           => routes.BusinessSelectCountryController.onPageLoad(index, mode)
    }

  private def businessInformationRoutes(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(BusinessInformationCheckPage(index)).flatMap { businessInformation =>
      BusinessInformationCheck.values.find(businessInformation.contains).map(businessInformationRoute(_, index, mode))
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  //noinspection ScalaStyle
  def businessInformationRoutes(
    answers: UserAnswers,
    index: Index,
    answer: BusinessInformationCheck,
    mode: Mode = NormalMode
  ): Call =
    answers get BusinessInformationCheckPage(index) flatMap { checkedInfo =>
      import BusinessInformationCheck._
      def isEmpty(section: BusinessInformationCheck) = section match {
        case Name              => answers get BusinessNamePage(index) isEmpty
        case Type              => answers get TypeBusinessPage(index) isEmpty
        case Contact           => answers get BusinessContactDetailsPage(index) isEmpty
        case Address           => answers get BusinessAddressPage(index) isEmpty
        case BusinessReference => answers get ReferenceNumbersPage(index) isEmpty
      }
      val laterSections     = BusinessInformationCheck.values dropWhile (_ != answer) drop 1 toSet
      val remainingSections = checkedInfo & laterSections filter isEmpty

      if (remainingSections.isEmpty) Some {
        mode match {
          case NormalMode =>
            val sortedSteps = checkedInfo.toSeq.sortBy(_.order)
            sortedSteps.find(_.order > answer.order) match {
              case Some(nextStep) => businessInformationRoute(nextStep, index, NormalMode)
              case None           => routes.SelectConnectionBusinessController.onPageLoad(index, NormalMode)
            }
          case CheckMode | UpdateIndividualMode =>
            if (!answers.isBusinessJourney)
              routes.IndividualCheckYourAnswersController.onPageLoad(index, mode)
            else
              routes.CheckYourAnswersController.onPageLoad
        }
      }
      else
        BusinessInformationCheck.values find remainingSections.contains map {
          mode match {
            case NormalMode => businessInformationRoute(_, index, mode)
            case CheckMode | UpdateIndividualMode =>
              businessInformationRoute(_, index, mode)
          }
        }

    } getOrElse routes.JourneyRecoveryController.onPageLoad()

  private def addAnotherPersonRoutes(answers: UserAnswers): Call =
    answers.get(AddAnotherPersonPage).map {
      case AddAnotherPerson.Yes =>
        routes.IndividualInformationController.onPageLoad(
          Index(answers.get(IndividualIndexPage).getOrElse(List.empty).length),
          NormalMode
        )
      case AddAnotherPerson.No =>
        routes.ActivitySourceOfInformationController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def individualConfirmRemoveRoutes(answers: UserAnswers): Call =
    if (answers.get(NominalsQuery).getOrElse(List.empty).isEmpty)
      routes.IndividualOrBusinessController.onPageLoad(NormalMode)
    else
      routes.AddAnotherPersonController.onPageLoad(NormalMode)

  private def individualBusinessDetailsRoutes(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(IndividualBusinessDetailsPage(index)).map {
      case IndividualBusinessDetails.Yes =>
        routes.BusinessInformationCheckController.onPageLoad(index, mode)
      case _ =>
        mode match {
          case CheckMode  => routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
          case NormalMode => routes.AddAnotherPersonController.onPageLoad(mode)
          case UpdateIndividualMode =>
            routes.IndividualCheckYourAnswersController.onPageLoad(index, UpdateIndividualMode)
        }
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def individualSelectCountryPageRoute(answers: UserAnswers, index: Index): Call =
    if (answers.get(IndividualSelectCountryPage(index)).contains("gb"))
      routes.FindAddressController.onPageLoad(index, NormalMode)
    else
      routes.IndividualAddressController.onPageLoad(index, NormalMode)

  private def businessSelectCountryPageRoute(answers: UserAnswers, index: Index): Call =
    if (answers.get(BusinessSelectCountryPage(index)).contains("gb"))
      routes.BusinessFindAddressController.onPageLoad(index, NormalMode)
    else
      routes.BusinessAddressController.onPageLoad(index, NormalMode)

  private def whenActivityHappenRoutes(answers: UserAnswers): Call =
    answers.get(WhenActivityHappenPage).map {
      case WhenActivityHappen.NotHappen =>
        routes.ActivityTimePeriodController.onPageLoad(NormalMode)
      case _ =>
        routes.ApproximateValueController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def approximateValueRoutes(answers: UserAnswers): Call =
    if (answers.get(ApproximateValuePage).get.equals(0))
      routes.ZeroValidationController.onPageLoad(NormalMode)
    else
      routes.HowManyPeopleKnowController.onPageLoad(NormalMode)

  private def confirmAddressNormalModeRoute(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(ConfirmAddressPage(index)).map {
      case true =>
        mode match {
          case NormalMode =>
            if (answers.isBusinessDetails(index))
              businessInformationRoutes(answers, index, BusinessInformationCheck.Address, mode)
            else
              individualInformationRoutes(answers, index, IndividualInformation.Address, mode)
          case CheckMode | UpdateIndividualMode =>
            if (answers.isBusinessDetails(index))
              businessInformationRoutes(answers, index, BusinessInformationCheck.Address, mode)
            else
              routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
        }
      case false =>
        if (answers.get(IndividualSelectCountryPage(index)).contains("gb"))
          routes.ChooseYourAddressController.onPageLoad(index, NormalMode)
        else
          routes.IndividualAddressController.onPageLoad(index, NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def businessConfirmAddressNormalModeRoute(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(BusinessConfirmAddressPage(index)).map {
      case true =>
        businessInformationRoutes(answers, index, BusinessInformationCheck.Address, mode)
      case false =>
        if (answers.get(BusinessSelectCountryPage(index)).contains("gb"))
          routes.BusinessChooseYourAddressController.onPageLoad(index, mode)
        else
          routes.BusinessAddressController.onPageLoad(index, mode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def zeroValidationRoutes(answers: UserAnswers): Call =
    answers.get(ZeroValidationPage).map {
      case true  => routes.HowManyPeopleKnowController.onPageLoad(NormalMode)
      case false => routes.ApproximateValueController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def supportingDocumentRoutes(answers: UserAnswers): Call =
    answers.get(SupportingDocumentPage).map {
      case SupportingDocument.Yes =>
        routes.DocumentationDescriptionController.onPageLoad(NormalMode)
      case SupportingDocument.No =>
        routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def provideContactDetailsRoutes(answers: UserAnswers): Call =
    answers.get(ProvideContactDetailsPage).map {
      case ProvideContactDetails.Yes =>
        routes.YourContactDetailsController.onPageLoad(NormalMode)
      case ProvideContactDetails.No =>
        routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def supportingDocumentCheckRoutes(answers: UserAnswers): Call =
    answers.get(SupportingDocumentPage).map {
      case SupportingDocument.Yes =>
        if (answers.get(DocumentationDescriptionPage).isDefined)
          routes.CheckYourAnswersController.onPageLoad
        else
          routes.DocumentationDescriptionController.onPageLoad(CheckMode)
      case SupportingDocument.No => routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.CheckYourAnswersController.onPageLoad)

  private def selectConnectionBusinessRoutes(answers: UserAnswers): Call =
    if (answers.isBusinessJourney)
      routes.ActivitySourceOfInformationController.onPageLoad(NormalMode)
    else
      routes.AddAnotherPersonController.onPageLoad(NormalMode)

  private def approximateValueCheckRoutes(answers: UserAnswers): Call =
    if (answers.get(ApproximateValuePage).get.equals(0))
      routes.ZeroValidationController.onPageLoad(CheckMode)
    else
      routes.CheckYourAnswersController.onPageLoad

  private def confirmAddressCheckModeRoutes(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(ConfirmAddressPage(index)).map {
      case true =>
        if (answers.isBusinessDetails(index))
          businessInformationRoutes(answers, index, BusinessInformationCheck.Address, mode)
        else
          routes.IndividualCheckYourAnswersController.onPageLoad(index, mode)
      case false =>
        if (answers.isBusinessDetails(index))
          businessInformationRoutes(answers, index, BusinessInformationCheck.Address, mode)
        else
          routes.IndividualCheckYourAnswersController.onPageLoad(index, mode)
        if (!answers.isBusinessDetails(index))
          individualInformationRoutes(answers, index, IndividualInformation.Address, mode)
        else
          routes.IndividualAddressController.onPageLoad(index, mode)
        if (answers.get(IndividualSelectCountryPage(index)).contains("gb"))
          routes.ChooseYourAddressController.onPageLoad(index, mode)
        else
          routes.IndividualAddressController.onPageLoad(index, mode)
    }.getOrElse(routes.IndividualCheckYourAnswersController.onPageLoad(index, mode))

  private def businessConfirmAddressCheckModeRoutes(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(BusinessConfirmAddressPage(index)).map {
      case true =>
        if (answers.isBusinessDetails(index))
          businessInformationRoutes(answers, index, BusinessInformationCheck.Address, mode)
        else
          routes.CheckYourAnswersController.onPageLoad
      case false =>
        if (answers.get(BusinessSelectCountryPage(index)).contains("gb"))
          routes.BusinessChooseYourAddressController.onPageLoad(index, mode)
        else
          routes.BusinessAddressController.onPageLoad(index, mode)
    }.getOrElse(routes.CheckYourAnswersController.onPageLoad)

  private def zeroValidationCheckRoutes(answers: UserAnswers): Call =
    answers.get(ZeroValidationPage).map {
      case true  => routes.CheckYourAnswersController.onPageLoad
      case false => routes.ApproximateValueController.onPageLoad(CheckMode)
    }.getOrElse(routes.CheckYourAnswersController.onPageLoad)

  private def selectConnectionBusinessCheckRoute(answers: UserAnswers, index: Index): Call =
    if (answers.isBusinessJourney)
      routes.CheckYourAnswersController.onPageLoad
    else
      routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)

  private def individualDateFormatPageCheckRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualDateFormatPage(index)).map {
      case IndividualDateFormat.Date =>
        if (answers.get(IndividualDateOfBirthPage(index)).isDefined)
          routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
        else
          routes.IndividualDateOfBirthController.onPageLoad(index, CheckMode)
      case IndividualDateFormat.Age =>
        if (answers.get(IndividualAgePage(index)).isDefined)
          routes.IndividualCheckYourAnswersController.onPageLoad(index, CheckMode)
        else
          routes.IndividualAgeController.onPageLoad(index, CheckMode)
    }.getOrElse(routes.CheckYourAnswersController.onPageLoad)

  private def whenActivityHappenCheckRoutes(answers: UserAnswers): Call =
    answers.get(WhenActivityHappenPage).map {
      case WhenActivityHappen.NotHappen =>
        if (answers.get(ActivityTimePeriodPage).isDefined)
          routes.CheckYourAnswersController.onPageLoad
        else
          routes.ActivityTimePeriodController.onPageLoad(CheckMode)
      case _ =>
        routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def individualSelectCountryPageRoutes(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(IndividualSelectCountryPage(index)).map {
      case _ =>
        if (answers.get(IndividualSelectCountryPage(index)).contains("gb"))
          routes.FindAddressController.onPageLoad(index, mode)
        else
          routes.IndividualAddressController.onPageLoad(index, mode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def businessSelectCountryPageRoutes(answers: UserAnswers, index: Index, mode: Mode): Call =
    answers.get(BusinessSelectCountryPage(index)).map {
      case _ =>
        if (answers.get(BusinessSelectCountryPage(index)).contains("gb"))
          routes.BusinessFindAddressController.onPageLoad(index, mode)
        else
          routes.BusinessAddressController.onPageLoad(index, mode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case UpdateIndividualMode =>
      updateIndividualMode(page)(userAnswers)
  }

}
