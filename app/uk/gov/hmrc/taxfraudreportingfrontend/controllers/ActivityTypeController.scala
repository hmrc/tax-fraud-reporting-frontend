/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.taxfraudreportingfrontend.controllers

import play.api.Mode
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.ActivityTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.ActivityTypeView

import javax.inject.Inject
import scala.concurrent.Future

class ActivityTypeController @Inject() (
                                         mcc: MessagesControllerComponents,
                                         activityTypeView: ActivityTypeView,
                                         activityTypeProvider: ActivityTypeProvider)(implicit
  appConfig: AppConfig
) extends FrontendController(mcc) {

  val form = activityTypeProvider()

  private def onSubmitCall() = routes.ActivityTypeController.onPageLoad()

  def onPageLoad(): Action[AnyContent] = Action { implicit request =>
    Ok(activityTypeView(form, onSubmitCall()))
  }

}
