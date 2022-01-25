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

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

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

        "to the business information check page" ignore {}

        "to the journey recovery controller if there is no individual or business set" in {
          navigator.nextPage(
            IndividualOrBusinessPage,
            NormalMode,
            UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from individual date format page" - {

        "to the individual approximate age page when they select 'age'" in {
          val answers = UserAnswers("id").set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Age).success.value
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            NormalMode, answers
          ) mustBe routes.IndividualAgeController.onPageLoad(NormalMode)
        }

        "to the individual date of birth page when they select 'date of birth'" in {
          val answers = UserAnswers("id").set(IndividualDateFormatPage(Index(0)), IndividualDateFormat.Date).success.value
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            NormalMode, answers
          ) mustBe routes.IndividualDateOfBirthController.onPageLoad(NormalMode)
        }

        "to the journey recovery controller if there is no individual date format set" in {
          navigator.nextPage(
            IndividualDateFormatPage(Index(0)),
            NormalMode, UserAnswers("id")
          ) mustBe routes.JourneyRecoveryController.onPageLoad()
        }
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
