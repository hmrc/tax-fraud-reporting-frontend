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

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject() () {

  private val normalRoutes: Page => UserAnswers => Call = {
    case ActivityTypePage                => activityPageRoutes
    case IndividualOrBusinessPage        => individualOrBusinessRoutes
    case IndividualDateFormatPage(index) => ageFormatPageRoutes(_, index)
    case IndividualNamePage(index)       => individualInformationRoutes(_, index, IndividualInformation.Name)
    case IndividualAddressConfirmationPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.Address)
    case IndividualAgePage(index)         => individualInformationRoutes(_, index, IndividualInformation.Age)
    case IndividualDateOfBirthPage(index) => individualInformationRoutes(_, index, IndividualInformation.Age)
    case IndividualContactDetailsPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.ContactDetails)
    case IndividualNationalInsuranceNumberPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.NiNumber)
    case IndividualInformationPage(index)    => individualInformationRoutes(_, index)
    case BusinessNamePage(index)             => businessInformationRoutes(_, index, BusinessInformationCheck.Name)
    case TypeBusinessPage(index)             => businessInformationRoutes(_, index, BusinessInformationCheck.Type)
    case ReferenceNumbersPage(index)         => businessInformationRoutes(_, index, BusinessInformationCheck.BusinessReference)
    case BusinessContactDetailsPage(index)   => businessInformationRoutes(_, index, BusinessInformationCheck.Contact)
    case BusinessInformationCheckPage(index) => businessInformationRoutes(_, index)
    case BusinessAddressConfirmationPage(index) =>
      businessInformationRoutes(_, index, BusinessInformationCheck.Address)
    case SelectConnectionBusinessPage(index)  => _ => routes.AddAnotherPersonController.onPageLoad(index, NormalMode)
    case AddAnotherPersonPage(index)          => addAnotherPersonRoutes(_, index)
    case IndividualBusinessDetailsPage(index) => individualBusinessDetailsRoutes(_, index)
    case ApproximateValuePage                 => _ => routes.WhenActivityHappenController.onPageLoad(NormalMode)
    case WhenActivityHappenPage               => whenActivityHappenRoutes
    case ActivityTimePeriodPage               => _ => routes.DescriptionActivityController.onPageLoad(NormalMode)
    case IndividualConnectionPage(index) =>
      _ => routes.IndividualBusinessDetailsController.onPageLoad(index, NormalMode)
    case DescriptionActivityPage   => _ => routes.HowManyPeopleKnowController.onPageLoad(NormalMode)
    case SupportingDocumentPage    => supportingDocumentRoutes
    case HowManyPeopleKnowPage     => _ => routes.ProvideContactDetailsController.onPageLoad(NormalMode)
    case ProvideContactDetailsPage => provideContactDetailsRoutes
    case YourContactDetailsPage    => _ => routes.SupportingDocumentController.onPageLoad(NormalMode)
    case _                         => _ => routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  private def individualInformationRoute(answer: IndividualInformation, index: Index, mode: Mode): Call =
    answer match {
      case IndividualInformation.Name           => routes.IndividualNameController.onPageLoad(index, mode)
      case IndividualInformation.Age            => routes.IndividualDateFormatController.onPageLoad(index, mode)
      case IndividualInformation.Address        => routes.IndividualAddressRedirectController.onPageLoad(index, mode)
      case IndividualInformation.ContactDetails => routes.IndividualContactDetailsController.onPageLoad(index, mode)
      case IndividualInformation.NiNumber       => routes.IndividualNationalInsuranceNumberController.onPageLoad(index, mode)
    }

  private def individualInformationRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualInformationPage(index)).flatMap { individualInformation =>
      IndividualInformation.values.find(individualInformation.contains).map(
        individualInformationRoute(_, index, NormalMode)
      )
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def individualInformationRoutes(answers: UserAnswers, index: Index, answer: IndividualInformation): Call =
    answers.get(IndividualInformationPage(index)).flatMap { individualInformation =>
      val remainingSections = individualInformation & IndividualInformation.values.dropWhile(_ != answer).drop(1).toSet
      if (remainingSections.isEmpty)
        Some(routes.IndividualConnectionController.onPageLoad(index, NormalMode))
      else
        IndividualInformation.values.find(remainingSections.contains).map(
          individualInformationRoute(_, index, NormalMode)
        )
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def ageFormatPageRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualDateFormatPage(index)).map {
      case IndividualDateFormat.Date => routes.IndividualDateOfBirthController.onPageLoad(index, NormalMode)
      case IndividualDateFormat.Age  => routes.IndividualAgeController.onPageLoad(index, NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def activityPageRoutes(answers: UserAnswers): Call =
    answers.get(ActivityTypePage).map { activity =>
      if (ActivityType.otherDepartments.isDefinedAt(activity.activityName))
        routes.DoNotUseThisServiceController.onPageLoad()
      else
        routes.IndividualOrBusinessController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

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
      case BusinessInformationCheck.Address           => routes.BusinessAddressRedirectController.onPageLoad(index, mode)

    }

  private def businessInformationRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(BusinessInformationCheckPage(index)).flatMap { businessInformation =>
      BusinessInformationCheck.values.find(businessInformation.contains).map(
        businessInformationRoute(_, index, NormalMode)
      )
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def businessInformationRoutes(answers: UserAnswers, index: Index, answer: BusinessInformationCheck): Call =
    answers.get(BusinessInformationCheckPage(index)).flatMap { businessInformation =>
      val remainingSections = businessInformation & BusinessInformationCheck.values.dropWhile(_ != answer).drop(1).toSet
      if (remainingSections.isEmpty)
        Some(routes.SelectConnectionBusinessController.onPageLoad(index, NormalMode))
      else
        BusinessInformationCheck.values.find(remainingSections.contains).map(
          businessInformationRoute(_, index, NormalMode)
        )
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def addAnotherPersonRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(AddAnotherPersonPage(index)).map {
      case AddAnotherPerson.Yes =>
        routes.IndividualInformationController.onPageLoad(Index(0), NormalMode)
      case AddAnotherPerson.No =>
        routes.ApproximateValueController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def individualBusinessDetailsRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualBusinessDetailsPage(index)).map {
      case IndividualBusinessDetails.Yes =>
        routes.BusinessInformationCheckController.onPageLoad(Index(0), NormalMode)
      case IndividualBusinessDetails.No =>
        routes.AddAnotherPersonController.onPageLoad(Index(0), NormalMode)
      case IndividualBusinessDetails.DontKnow =>
        routes.AddAnotherPersonController.onPageLoad(Index(0), NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def whenActivityHappenRoutes(answers: UserAnswers): Call =
    answers.get(WhenActivityHappenPage).map {
      case WhenActivityHappen.NotHappen =>
        routes.ActivityTimePeriodController.onPageLoad(NormalMode)
      case _ =>
        routes.DescriptionActivityController.onPageLoad(NormalMode)
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

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

}
