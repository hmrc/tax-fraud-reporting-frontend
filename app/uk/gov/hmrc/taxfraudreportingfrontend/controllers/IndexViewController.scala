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

import uk.gov.hmrc.taxfraudreportingfrontend.views.html.IndexView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.taxfraudreportingfrontend.config.AppConfig

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class IndexViewController @Inject() (mcc: MessagesControllerComponents, indexView: IndexView)(
  implicit appConfig: AppConfig,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc) {

  def onPageLoad(): Action[AnyContent] = Action { implicit request =>
    val response = Ok(indexView())

    if (request.session.data contains SessionKeys.sessionId) response
    else response.addingToSession(SessionKeys.sessionId -> UUID.randomUUID.toString)
  }

}
