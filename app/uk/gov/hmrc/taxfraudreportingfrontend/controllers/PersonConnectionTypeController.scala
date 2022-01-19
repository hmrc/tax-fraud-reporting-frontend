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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{SessionCache, UserAnswersCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.PersonConnectionTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.PersonConnectionTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonConnectionTypeController @Inject() (
  mcc: MessagesControllerComponents,
  connectionView: PersonConnectionTypeView,
  formProvider: PersonConnectionTypeProvider,
  userAnswersCache: UserAnswersCache,
  sessionCache: SessionCache
)(implicit appConfig: AppConfig, executionContext: ExecutionContext)
    extends FrontendController(mcc) {

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    hc.sessionId map { _ =>
      sessionCache.get map { fraudReport =>
        val filledForm = (for {
          report         <- fraudReport
          connectionType <- report.connectionType
        } yield form.fill(connectionType)).getOrElse(form)
        Ok(connectionView(filledForm))
      }
    } getOrElse Future.successful {
      Redirect(routes.PersonConnectionTypeController.onPageLoad())
    }
  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      val boundForm = form.bindFromRequest()
      boundForm.fold(
        formWithErrors => Future.successful(BadRequest(connectionView(formWithErrors))),
        connectionType =>
          userAnswersCache.cacheConnection(Some(connectionType)) map { _ =>
            Redirect(routes.PersonOwnBusinessController.onPageLoad())
          }
      )
  }

}
