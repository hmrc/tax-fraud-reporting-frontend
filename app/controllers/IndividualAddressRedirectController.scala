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

package controllers

import controllers.actions._
import models.{AddressLookupLabels, Index, LookupPageLabels, Mode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AddressLookupService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class IndividualAddressRedirectController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  addressLookupService: AddressLookupService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val labels: AddressLookupLabels = AddressLookupLabels(lookupPageLabels =
    LookupPageLabels(title = "individualAddress.lookup.title", heading = "individualAddress.lookup.heading")
  )

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      addressLookupService.startJourney(
        routes.IndividualAddressConfirmationController.onPageLoad(index, mode, None).url,
        labels
      ).map(Redirect(_, SEE_OTHER))
  }

}
