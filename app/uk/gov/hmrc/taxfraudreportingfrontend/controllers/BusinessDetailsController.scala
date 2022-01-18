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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{SessionCache, UserAnswersCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.BusinessDetailsProvider
import uk.gov.hmrc.taxfraudreportingfrontend.models.BusinessDetails
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.BusinessDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  view: BusinessDetailsView,
  formProvider: BusinessDetailsProvider,
  userAnswersCache: UserAnswersCache,
  sessionCache: SessionCache
)(implicit appConfig: AppConfig, executionContext: ExecutionContext)
    extends FrontendController(mcc) {

  private val form: Form[BusinessDetails] = formProvider()

  private def onSubmitDetails() = routes.BusinessDetailsController.onSubmit()

  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    hc.sessionId map { _ =>
      sessionCache.get map { fraudReport =>
        val filledForm = fraudReport match {
          case Some(f) if f.businessDetails.nonEmpty => form.fill(f.businessDetails.get)
          case _                                     => form
        }
        Ok(view(filledForm, onSubmitDetails()))
      }
    } getOrElse Future.successful {
      Redirect(routes.BusinessDetailsController.onPageLoad())
    }
  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      val boundForm = form.bindFromRequest()
      boundForm.fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, onSubmitDetails()))),
        businessDetails =>
          userAnswersCache.cacheBusinessDetails(Some(businessDetails)) map { _ =>
            Redirect(routes.AddAnotherPersonController.onPageLoad())
          }
      )
  }

}
