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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.services.ActivityTypeService
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.{ErrorTemplate, ShouldNotUseServiceView}

import javax.inject.Inject

class ShouldNotUseServiceController @Inject() (
  mcc: MessagesControllerComponents,
  serviceNotUseView: ShouldNotUseServiceView,
  activityTypeService: ActivityTypeService,
  errorTemplate: ErrorTemplate
)(implicit appConfig: AppConfig)
    extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(activityType: String): Action[AnyContent] = Action { implicit request =>
    activityTypeService.otherDepartments get activityType match {
      case Some(department) => Ok(serviceNotUseView(department))
      case None =>
        NotFound(
          errorTemplate(
            "activityType.activityName.not-found.title",
            "activityType.activityName.not-found.header",
            "activityType.activityName.not-found.p1"
          )
        )
    }
  }

}
