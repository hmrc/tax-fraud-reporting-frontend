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

package models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class AddressResponseSpec extends AnyFreeSpec with Matchers {

  "must successfully read from json" - {

    "when all fields are present" in {
      val json   = Json.parse("""{
                   |    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
                   |    "id" : "GB990091234524",
                   |    "address" : {
                   |        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
                   |        "town" : "town",
                   |        "postcode" : "ZZ1 1ZZ",
                   |        "country" : {
                   |            "code" : "GB",
                   |            "name" : "United Kingdom"
                   |        }
                   |    }
                   |}""".stripMargin)
      val result = json.as[AddressResponse]
      result mustEqual AddressResponse(
        lines = List("10 Other Place", "Some District", "Anytown"),
        town = Some("town"),
        postcode = Some("ZZ1 1ZZ"),
        country = Some("GB")
      )
    }

    "when town is missing" in {
      val json   = Json.parse("""{
                                |    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
                                |    "id" : "GB990091234524",
                                |    "address" : {
                                |        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
                                |        "postcode" : "ZZ1 1ZZ",
                                |        "country" : {
                                |            "code" : "GB",
                                |            "name" : "United Kingdom"
                                |        }
                                |    }
                                |}""".stripMargin)
      val result = json.as[AddressResponse]
      result mustEqual AddressResponse(
        lines = List("10 Other Place", "Some District", "Anytown"),
        town = None,
        postcode = Some("ZZ1 1ZZ"),
        country = Some("GB")
      )
    }

    "when postcode is missing" in {
      val json   = Json.parse("""{
                              |    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
                              |    "id" : "GB990091234524",
                              |    "address" : {
                              |        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
                              |        "town" : "town",
                              |        "country" : {
                              |            "code" : "GB",
                              |            "name" : "United Kingdom"
                              |        }
                              |    }
                              |}""".stripMargin)
      val result = json.as[AddressResponse]
      result mustEqual AddressResponse(
        lines = List("10 Other Place", "Some District", "Anytown"),
        town = Some("town"),
        postcode = None,
        country = Some("GB")
      )
    }

    "when country is missing" in {
      val json   = Json.parse("""{
                              |    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
                              |    "id" : "GB990091234524",
                              |    "address" : {
                              |        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
                              |        "town" : "town",
                              |        "postcode" : "ZZ1 1ZZ"
                              |    }
                              |}""".stripMargin)
      val result = json.as[AddressResponse]
      result mustEqual AddressResponse(
        lines = List("10 Other Place", "Some District", "Anytown"),
        town = Some("town"),
        postcode = Some("ZZ1 1ZZ"),
        country = None
      )
    }

    "when only lines exists" in {
      val json   = Json.parse("""{
                              |    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
                              |    "id" : "GB990091234524",
                              |    "address" : {
                              |        "lines" : [ "10 Other Place", "Some District", "Anytown" ]
                              |    }
                              |}""".stripMargin)
      val result = json.as[AddressResponse]
      result mustEqual AddressResponse(
        lines = List("10 Other Place", "Some District", "Anytown"),
        town = None,
        postcode = None,
        country = None
      )
    }
  }

  "must write to json" in {
    val expected = Json.parse("""{
                            |    "address" : {
                            |        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
                            |        "postcode" : "ZZ1 1ZZ",
                            |        "town" : "town",
                            |        "country" : {
                            |            "code" : "GB"
                            |        }
                            |    }
                            |}""".stripMargin)
    val model = AddressResponse(
      lines = List("10 Other Place", "Some District", "Anytown"),
      town = Some("town"),
      postcode = Some("ZZ1 1ZZ"),
      country = Some("GB")
    )
    Json.toJson(model) mustEqual expected
  }
}
