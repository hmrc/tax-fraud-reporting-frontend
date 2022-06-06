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

package services

import com.github.tomakehurst.wiremock.client.WireMock._
import models.addresslookup.Country
import org.scalatest.{OptionValues, TryValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{PafFixtures, WireMockHelper}

import scala.io.Source

class AddressLookupServiceSpec
    extends AnyFreeSpec with Matchers with WireMockHelper with ScalaFutures with IntegrationPatience with OptionValues
    with TryValues with PafFixtures {

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure("microservice.services.address-lookup.port" -> server.port, "host" -> "http://localhost")
      .build()

  def addressLookupService: AddressLookupService = application.injector.instanceOf[AddressLookupService]

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val urlPost = "/lookup"

  val requestBody: JsObject = Json.obj("postcode" -> "ZZ11ZZ", "filter" -> "2")

  val addressRecordSet: String =
    Source.fromInputStream(getClass.getResourceAsStream("/address-lookup/recordSet.json")).mkString

  val missingAddressLineRecordSet: String =
    Source
      .fromInputStream(getClass.getResourceAsStream("/address-lookup/recordSetWithMissingAddressLines.json"))
      .mkString

  val emptyRecordSet = "[]"

  "AddressLookupService" - {
    "lookup is called as post" - {

      "return a List of addresses matching the given postcode, if any matching record exists" in {

        val wholeStreetRequestBody: JsObject = Json.obj("postcode" -> "ZZ11ZZ")

        server.stubFor(
          post(urlEqualTo(urlPost))
            .withRequestBody(equalToJson(wholeStreetRequestBody.toString))
            .willReturn(ok(addressRecordSet))
        )

        addressLookupService.lookup("ZZ11ZZ", None).futureValue mustBe matchedAddress

        server.verify(
          postRequestedFor(urlEqualTo(urlPost))
            .withHeader("X-Hmrc-Origin", equalTo("fraud"))
        )
      }

      "return a List of addresses matching the given postcode and house number, if any matching record exists" in {
        server.stubFor(
          post(urlEqualTo(urlPost))
            .withRequestBody(equalToJson(requestBody.toString))
            .willReturn(ok(addressRecordSet))
        )

        addressLookupService.lookup("ZZ11ZZ", Some("2")).futureValue mustBe oneAndTwoOtherPlacePafRecordSet

      }
    }

    "return a List of addresses, filtering out addresses with no lines" in {

      server.stubFor(
        post(urlEqualTo(urlPost))
          .withRequestBody(equalToJson(requestBody.toString))
          .willReturn(ok(missingAddressLineRecordSet))
      )

      val result = addressLookupService.lookup("ZZ11ZZ", Some("2"))

      result.futureValue mustBe twoOtherPlaceRecordSet
    }

    "return an empty response for the given house name/number and postcode, if matching record doesn't exist" in {

      server.stubFor(
        post(urlEqualTo(urlPost))
          .withRequestBody(equalToJson(requestBody.toString))
          .willReturn(ok(emptyRecordSet))
      )

      val result = addressLookupService.lookup("ZZ11ZZ", Some("2"))

      result.futureValue mustBe List()
    }

    "return a List of addresses matching the given postcode, if any matching record exists and validate the country code" in {

      val wholeStreetRequestBody: JsObject = Json.obj("postcode" -> "ZZ11ZZ")

      server.stubFor(
        post(urlEqualTo(urlPost))
          .withRequestBody(equalToJson(wholeStreetRequestBody.toString))
          .willReturn(ok(addressRecordSet))
      )

      val result = addressLookupService.lookup("ZZ11ZZ", None).futureValue
      result.head.country mustBe  Country("GB", "United Kingdom")

      server.verify(
        postRequestedFor(urlEqualTo(urlPost))
          .withHeader("X-Hmrc-Origin", equalTo("fraud"))
      )
    }

  }
}
