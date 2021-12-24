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

import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.cache.{SessionCache, UserAnswersCache}
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.IndividualInformationCheckProvider
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.{IndividualInformationCheckView, IndividualNameView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualInformationCheckController @Inject() (
  mcc: MessagesControllerComponents,
  informationCheckView: IndividualInformationCheckView,
  formProvider: IndividualInformationCheckProvider,
  userAnswersCache: UserAnswersCache,
  sessionCache: SessionCache,
  nameView: IndividualNameView
)(implicit appConfig: AppConfig, executionContext: ExecutionContext)
    extends FrontendController(mcc) {

  val form = formProvider()

  private def onSubmitIndividualCheck(): Call = routes.IndividualInformationCheckController.onSubmit()

  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    hc.sessionId map { sessionID =>
      sessionCache.isCacheNotPresentCreateOne(sessionID.value) map { fraudReport =>
        val filledForm = form fill fraudReport.individualInformationCheck
        Ok(informationCheckView(filledForm, onSubmitIndividualCheck()))
      }
    } getOrElse Future.successful {
      Redirect(routes.IndividualInformationCheckController.onPageLoad())
    }
  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      val boundForm = form.bindFromRequest()
      boundForm.fold(
        formWithErrors =>
          Future.successful(BadRequest(informationCheckView(formWithErrors, onSubmitIndividualCheck()))),
        individualInformationCheck =>
          userAnswersCache.cacheIndividualInformationCheck(individualInformationCheck) map { _ =>
            Redirect(routes.NameController.onPageLoad())
          }
      )
  }

}
