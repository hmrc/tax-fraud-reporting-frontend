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

import play.api.data.Form
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig
import uk.gov.hmrc.taxfraudreportingfrontend.forms.ReportingTypeProvider
import uk.gov.hmrc.taxfraudreportingfrontend.models.ReportingType
import uk.gov.hmrc.taxfraudreportingfrontend.views.html.ReportingTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReportingTypeController @Inject() (
  mcc: MessagesControllerComponents,
  reportingTypeView: ReportingTypeView,
  formProvider: ReportingTypeProvider
)(implicit appConfig: AppConfig, executionContext: ExecutionContext)
    extends FrontendController(mcc) {

  val form: Form[ReportingType] = formProvider()

  private def onSubmitReportingType() = routes.ReportingTypeController.onSubmit()

  def onPageLoad(): Action[AnyContent] = Action.async {

    implicit request =>
      Future.successful(Ok(reportingTypeView(form, onSubmitReportingType())))

  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(reportingTypeView(formWithErrors, onSubmitReportingType()))),
        reportingType =>
          Future.successful(
            if (ReportingType.Person == reportingType)
              Redirect(
                uk.gov.hmrc.taxfraudreportingfrontend.controllers.routes.IndividualInformationCheckController.onPageLoad()
              )
            else
              Redirect(uk.gov.hmrc.taxfraudreportingfrontend.controllers.routes.NameController.onPageLoad())
          )
      )
  }

}
