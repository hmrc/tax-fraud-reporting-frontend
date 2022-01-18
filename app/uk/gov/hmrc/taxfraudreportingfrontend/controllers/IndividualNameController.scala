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

import play.api.i18n._
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{SessionCache, UserAnswersCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualNameProvider
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.IndividualNameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualNameController @Inject() (
  mcc: MessagesControllerComponents,
  nameView: IndividualNameView,
  formProvider: IndividualNameProvider,
  userAnswersCache: UserAnswersCache,
  sessionCache: SessionCache
)(implicit appConfig: AppConfig, executionContext: ExecutionContext, messages: MessagesApi)
    extends FrontendController(mcc) with I18nSupport {

  private def form(implicit req: Request[AnyContent]) = formProvider()

  private def onNameSubmit(): Call = routes.IndividualNameController.onSubmit()

  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    hc.sessionId map { sessionID =>
      sessionCache.get map { fraudReport =>
        val filledForm = fraudReport match {
          case Some(f) if f.individualName.nonEmpty => form.fill(f.individualName.get)
          case _                                    => form
        }
        Ok(nameView(filledForm, onNameSubmit()))
      }
    } getOrElse Future.successful {
      Redirect(routes.IndividualNameController.onPageLoad())
    }
  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      val boundForm = form.bindFromRequest()
      boundForm.fold(
        formWithErrors => Future.successful(BadRequest(nameView(formWithErrors, onNameSubmit()))),
        individualName =>
          userAnswersCache.cacheIndividualName(Some(individualName)) map { _ =>
            //TODO when refactoring the code
            Redirect(routes.PersonConnectionTypeController.onPageLoad())
          }
      )
  }

}
