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
import controllers.countOfResults.{NoResults, ResultsCount, ResultsList}
import forms.{AddressFormProvider, ChooseYourAddressFormProvider}
import models.addresslookup.{AddressRecord, Countries, Country, LocalCustodian, ProposedAddress}
import models.requests.DataRequest

import javax.inject.Inject
import models.{AddressSansCountry, ChooseYourAddress, FindAddress, Index, Mode}
import navigation.Navigator
import pages.{BusinessAddressPage, BusinessSelectCountryPage, ChooseYourAddressPage, FindAddressPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{Format, Json, __}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.{Address, AddressService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.{BusinessPart, IndividualPart}
import views.html.ChooseYourAddressView

import scala.concurrent.{ExecutionContext, Future}

object countOfResults {

  sealed trait ResultsCount

  case class ResultsList(res: Seq[ProposedAddress]) extends ResultsCount

  case object NoResults extends ResultsCount

}

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
  addressService: AddressService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.get(FindAddressPage) match {
        case None => Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
        case Some(value) =>
          addressLookUp(value) map {
            case ResultsList(addresses) =>
              sessionRepository.set(
                request.userAnswers.set(ChooseYourAddressPage, addresses)
                  getOrElse (throw new Exception(s"Address is not saved in cache"))
              )
              Ok(view(form, mode, Proposals(Some(addresses))))

            case _ => Redirect(routes.JourneyRecoveryController.onPageLoad())
          }
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
          form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, Proposals(request.userAnswers.get(ChooseYourAddressPage))))),
        value =>
          request.userAnswers.get(ChooseYourAddressPage) match {
            case Some(addressList) =>  addressList.find(_.addressId == value.addressId)  match {
              case Some(address) =>
              /*  for {
                  //TODO for confirmation page
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(ChooseYourAddressPage, mode, request.userAnswers))*/
                Future.successful(Redirect(navigator.nextPage(ChooseYourAddressPage, mode, request.userAnswers)))
              case None => Future.successful(Redirect(navigator.nextPage(ChooseYourAddressPage, mode, request.userAnswers)))
            }
              Future.successful(Redirect(navigator.nextPage(ChooseYourAddressPage, mode, request.userAnswers)))
          }
      )
  }

  def addressLookUp(value: FindAddress)(implicit hc: HeaderCarrier): Future[ResultsCount] =
    addressService.lookup(value.Postcode, value.Property) flatMap {
      case noneFound if noneFound.isEmpty =>
        if (value.Property.isDefined)
          addressLookUp(value.copy(Property = None))
        else {
          //Future.successful(NoResults)
          val cannedComplexAddresses = List(
            cannedAddress(5000L, List("Flat 1", "74 Comeragh Road"), "ZZ11 1ZZ"),
            cannedAddress(6000L, List("Flat 2", "The Curtains Up", "Comeragh Road"), "ZZ11 1ZZ"),
            cannedAddress(7000L, List("Flat 1", "70 Comeragh Road"), "ZZ11 1ZZ"),
            cannedAddress(8000L, List("Flat 2", "74 Comeragh Road"), "ZZ11 1ZZ"),
            cannedAddress(9000L, List("Flat B", "78 Comeragh Road"), "ZZ11 1ZZ"),
            cannedAddress(10000L, List("72a", "Comeragh Road"), "ZZ11 1ZZ"),
            cannedAddress(11000L, List("Flat 1", "The Curtains Up", "Comeragh Road"), "ZZ11 1ZZ")
          )

          Future.successful(ResultsList(toProposals(cannedComplexAddresses)))
        }
      case displayProposals =>
        //Future.successful(ResultsList(displayProposals))

        val cannedComplexAddresses = List(
          cannedAddress(5000L, List("Flat 1", "74 Comeragh Road"), "ZZ11 1ZZ"),
          cannedAddress(6000L, List("Flat 2", "The Curtains Up", "Comeragh Road"), "ZZ11 1ZZ"),
          cannedAddress(7000L, List("Flat 1", "70 Comeragh Road"), "ZZ11 1ZZ"),
          cannedAddress(8000L, List("Flat 2", "74 Comeragh Road"), "ZZ11 1ZZ"),
          cannedAddress(9000L, List("Flat B", "78 Comeragh Road"), "ZZ11 1ZZ"),
          cannedAddress(10000L, List("72a", "Comeragh Road"), "ZZ11 1ZZ"),
          cannedAddress(11000L, List("Flat 1", "The Curtains Up", "Comeragh Road"), "ZZ11 1ZZ")
        )

        Future.successful(ResultsList(toProposals(cannedComplexAddresses)))

    }

  def cannedAddress(uprn: Long, lines: List[String], postCode: String, organisation: Option[String] = None) =
    AddressRecord(
      uprn.toString,
      Some(uprn),
      None,
      None,
      organisation,
      Address(lines, "some-town", postCode, Some(Countries.England), Country("GB", "United Kingdom")),
      "en",
      Some(LocalCustodian(123, "Tyne & Wear")),
      None,
      None,
      None,
      None
    )

  private def toProposals(found: List[AddressRecord]): Seq[ProposedAddress] =
    found.map { addr =>
      ProposedAddress(
        addr.id,
        uprn = addr.uprn,
        parentUprn = addr.parentUprn,
        usrn = addr.usrn,
        organisation = addr.organisation,
        addr.address.postcode,
        addr.address.town,
        addr.address.lines,
        addr.address.country
      )
    }

}

case class Proposals(proposals: Option[Seq[ProposedAddress]]) {

  def toHtmlOptions: Seq[(String, String)] =
    proposals
      .map { props =>
        props.map { addr =>
          (addr.addressId, addr.toDescription)
        }.sorted
      }
      .getOrElse(Seq.empty)

}
