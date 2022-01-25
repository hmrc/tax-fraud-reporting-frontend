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
    case ActivityTypePage         => activityPageRoutes
    case IndividualOrBusinessPage => individualOrBusinessRoutes
    case IndividualDateFormatPage(index) => ageFormatPageRoutes(_, index)
    case _                        => _ => routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  private def ageFormatPageRoutes(answers: UserAnswers, index: Index): Call =
    answers.get(IndividualDateFormatPage(index)).map {
      case IndividualDateFormat.Date => routes.IndividualDateOfBirthController.onPageLoad(NormalMode)
      case IndividualDateFormat.Age  => routes.IndividualAgeController.onPageLoad(NormalMode)
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
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

}
