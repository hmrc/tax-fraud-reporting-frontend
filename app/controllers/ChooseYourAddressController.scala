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
import controllers.countOfResults.{NoResults, ResultsList}
import controllers.helper.{AddressLookUpHelper, EventHelper}
import forms.ChooseYourAddressFormProvider

import javax.inject.Inject
import models.{AddressSansCountry, Index, Mode}
import navigation.Navigator
import pages.{ChooseYourAddressPage, FindAddressPage, IndividualAddressPage}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.AddressService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ChooseYourAddressView

import scala.concurrent.{ExecutionContext, Future}

class ChooseYourAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ChooseYourAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ChooseYourAddressView,
  addressService: AddressService,
  addressLookUpHelper: AddressLookUpHelper,
  val eventHelper: EventHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      eventHelper.pageLoadEvent(request.path)
      request.userAnswers.get(FindAddressPage(index)) match {
        case None => Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
        case Some(value) =>
          addressLookUpHelper.addressLookUp(value) map {
            case ResultsList(addresses) =>
              sessionRepository.set(
                request.userAnswers.set(ChooseYourAddressPage(index), addresses)
                  getOrElse (throw new Exception(s"Address is not saved in cache"))
              )
              Ok(view(form, index, mode, Proposals(Some(addresses))))

            case NoResults => Redirect(routes.CanNotFindAddressController.onPageLoad(index, mode))
          }
      }
  }

  def onSubmit(index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          eventHelper.formErrorEvent(
            request.path,
            messagesApi.preferred(List(Lang("en")))(formWithErrors.errors.head.message)
          )
          Future.successful(
            BadRequest(
              view(formWithErrors, index, mode, Proposals(request.userAnswers.get(ChooseYourAddressPage(index))))
            )
          )
        },
        value =>
          request.userAnswers.get(ChooseYourAddressPage(index)) match {
            case Some(addressList) =>
              addressList.find(_.addressId == value.addressId) match {
                case Some(address) =>
                  for {
                    updatedAnswers <- Future.fromTry(
                      request.userAnswers.set(
                        IndividualAddressPage(index),
                        AddressSansCountry(
                          address.line1,
                          address.line2,
                          address.line3,
                          address.town.getOrElse(""),
                          address.postcode
                        )
                      )
                    )
                    _ <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(routes.ConfirmAddressController.onPageLoad(index, false, mode))
                case None => Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
              }
            case _ =>
              Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
          }
      )
  }

}
