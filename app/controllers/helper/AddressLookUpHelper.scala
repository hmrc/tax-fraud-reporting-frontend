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

package controllers.helper

import controllers.countOfResults.{NoResults, ResultsCount, ResultsList}
import models.FindAddress
import play.api.Configuration
import services.AddressService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookUpHelper @Inject() (
  httpClient: HttpClient,
  configuration: Configuration,
  addressService: AddressService
)(implicit ec: ExecutionContext) {

  def addressLookUp(value: FindAddress)(implicit hc: HeaderCarrier): Future[ResultsCount] =
    addressService.lookup(value.Postcode, value.Property) flatMap {
      case noneFound if noneFound.isEmpty =>
        if (value.Property.isDefined)
          addressLookUp(value.copy(Property = None))
        else
          Future.successful(NoResults)
      case displayProposals =>
        Future.successful(ResultsList(displayProposals))
    }

}
