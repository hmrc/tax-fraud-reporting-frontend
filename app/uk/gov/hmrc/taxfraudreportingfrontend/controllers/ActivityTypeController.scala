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

package uk.gov.hmrc.taxfraudreportingfrontend.controllers

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{SessionCache, UserAnswersCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.ActivityTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.services.ActivityTypeService
import uk.gov.hmrc.taxfraudreportingfrontend.viewmodels.ActivityTypeViewModel
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.ActivityTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ActivityTypeController @Inject() (
  mcc: MessagesControllerComponents,
  activityTypeView: ActivityTypeView,
  activityTypeProvider: ActivityTypeProvider,
  activityTypeService: ActivityTypeService,
  userAnswersCache: UserAnswersCache,
  sessionCache: SessionCache
)(implicit appConfig: AppConfig, ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  val form: Form[ActivityTypeViewModel] = activityTypeProvider()

  private def onSubmitActivityType(): Call = routes.ActivityTypeController.onSubmit()

  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    sessionCache.createCacheIfNotPresent flatMap { _ =>
      sessionCache.get map { fraudReport =>
        val filledForm = fraudReport match {
          case Some(f) if f.activityType.isDefined => form.fill(ActivityTypeViewModel from f.activityType.get)
          case _                                   => form
        }
        Ok(activityTypeView(filledForm, onSubmitActivityType(), activityTypeService.activities))
      }
    }
  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      val boundForm = form.bindFromRequest()
      boundForm.fold(
        formWithErrors =>
          Future.successful(
            BadRequest(activityTypeView(formWithErrors, onSubmitActivityType(), activityTypeService.activities))
          ),
        activityType =>
          userAnswersCache.cacheActivityType(activityType.toModel) map { _ =>
            Redirect(
              if (activityTypeService.otherDepartments contains activityType.activityName)
                routes.ShouldNotUseServiceController.onPageLoad(activityType.activityName)
              else
                routes.ReportingTypeController.onPageLoad()
            )
          }
      )
  }

}
