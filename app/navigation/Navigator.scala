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

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject() () {

  private val normalRoutes: Page => UserAnswers => Call = {
    case ActivityTypePage                 => activityPageRoutes
    case IndividualOrBusinessPage         => individualOrBusinessRoutes
    case IndividualDateFormatPage(index)  => ageFormatPageRoutes(_, index)
    case IndividualNamePage(index)        => individualInformationRoutes(_, index, IndividualInformation.Name)
    case IndividualAgePage(index)         => individualInformationRoutes(_, index, IndividualInformation.Age)
    case IndividualDateOfBirthPage(index) => individualInformationRoutes(_, index, IndividualInformation.Age)
    case IndividualContactDetailsPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.ContactDetails)
    case IndividualNationalInsuranceNumberPage(index) =>
      individualInformationRoutes(_, index, IndividualInformation.NiNumber)
    case IndividualInformationPage(index)    => individualInformationRoutes(_, index)
    case BusinessNamePage(index)             => businessInformationRoutes(_, index, BusinessInformationCheck.Name)
    case TypeBusinessPage(index)             => businessInformationRoutes(_, index, BusinessInformationCheck.Type)
    case BusinessInformationCheckPage(index) => businessInformationRoutes(_, index)
    case _                                   => _ => routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  private def individualInformationRoute(answer: IndividualInformation, index: Index, mode: Mode): Call =
    answer match {
      case IndividualInformation.Name => routes.IndividualNameController.onPageLoad(index, mode)
      case IndividualInformation.Age  => routes.IndividualDateFormatController.onPageLoad(index, mode)
      // TODO add address when the pages are merged
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
      case BusinessInformationCheck.Name => routes.BusinessNameController.onPageLoad(index, mode)
      case BusinessInformationCheck.Type => routes.TypeBusinessController.onPageLoad(index, mode)
      // TODO add address, contact and business reference when the pages are merged

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
        Some(routes.IndividualConnectionController.onPageLoad(index, NormalMode))
      else
        BusinessInformationCheck.values.find(remainingSections.contains).map(
          businessInformationRoute(_, index, NormalMode)
        )
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

}
