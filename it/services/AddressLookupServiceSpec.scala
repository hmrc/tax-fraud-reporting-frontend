package services

import com.github.tomakehurst.wiremock.client.WireMock._
import controllers.routes
import models.{AddressLookupLabels, AddressResponse, Index, LookupPageLabels, NormalMode}
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.HeaderNames
import play.api.http.Status.ACCEPTED
import play.api.i18n.{Lang, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

class AddressLookupServiceSpec extends AnyFreeSpec with Matchers with WireMockHelper with ScalaFutures with IntegrationPatience with OptionValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.address-lookup-frontend.port" -> server.port,
        "host" -> "http://localhost",
        "timeout-dialog.timeout" -> 900
      )
      .build()

  lazy val service: AddressLookupService = application.injector.instanceOf[AddressLookupService]
  lazy val messagesApi: MessagesApi = application.injector.instanceOf[MessagesApi]

  val labels: AddressLookupLabels = AddressLookupLabels(
    lookupPageLabels = LookupPageLabels(
      title = "lookup.title",
      heading = "lookup.heading"
    )
  )

  "startJourney" - {

    lazy val english = messagesApi.preferred(List(Lang("en")))
    lazy val welsh = messagesApi.preferred(List(Lang("cy")))

    lazy val expectedJson = Json.obj(
      "version" -> 2,
      "options" -> Json.obj(
        "continueUrl" -> "http://localhost/foo",
        "serviceHref" -> s"http://localhost/${routes.IndexController.onPageLoad.url}",
        "phaseFeedbackLink" -> "/help/beta",
        "showPhaseBanner" -> true,
        "showBackButtons" -> true,
        "pageHeadingStyle" -> "govuk-heading-l",
        "includeHMRCBranding" -> false,
        "timeoutConfig" -> Json.obj(
          "timeoutAmount" -> 900,
          "timeoutUrl" -> routes.JourneyRecoveryController.onPageLoad().url, // TODO where should this go?
          "timeoutKeepAliveUrl" -> routes.ActivityTypeController.onPageLoad(NormalMode).url
        )
      ),
      "labels" -> Json.obj(
        "en" -> Json.obj(
          "appLevelLabels" -> Json.obj(
            "navTitle" -> english("service.name"),
          ),
          "lookupPageLabels" -> Json.obj(
            "title" -> english("lookup.title"),
            "heading" -> english("lookup.heading")
          )
        ),
        "cy" -> Json.obj(
          "appLevelLabels" -> Json.obj(
            "navTitle" -> welsh("service.name")
          ),
          "lookupPageLabels" -> Json.obj(
            "title" -> welsh("lookup.title"),
            "heading" -> welsh("lookup.heading")
          )
        )
      )
    )

    "must return the redirect url when the server responds with ACCEPTED" in {

      server.stubFor(
        post(urlEqualTo("/api/init"))
          .willReturn(status(ACCEPTED).withHeader(HeaderNames.LOCATION, "foobar"))
      )

      service.startJourney("/foo", labels).futureValue mustBe "foobar"

      server.verify(
        postRequestedFor(urlEqualTo("/api/init"))
          .withRequestBody(equalToJson(Json.stringify(expectedJson)))
      )
    }

    "must fail when the server responds with ACCEPTED but no redirect location" in {

      server.stubFor(
        post(urlEqualTo("/api/init"))
          .willReturn(status(ACCEPTED))
      )

      service.startJourney("foo", labels).failed.futureValue.getMessage mustEqual "Missing Location header"
    }

    "must fail when the server responds with other status codes" in {

      server.stubFor(
        post(urlEqualTo("/api/init"))
          .willReturn(serverError())
      )

      service.startJourney("foo", labels).failed.futureValue.getMessage mustEqual "Unexpected response from address lookup frontend: 500"
    }
  }

  "retrieveAddress" - {

    "must return the address for a given journey" in {

      val json = """{
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
                   |}""".stripMargin

      val expected = AddressResponse(
        lines = List("10 Other Place", "Some District", "Anytown"),
        postcode = Some("ZZ1 1ZZ"),
        country = Some("GB")
      )

      server.stubFor(
        get(urlEqualTo("/api/confirmed?id=foo"))
          .willReturn(okJson(json))
      )

      service.retrieveAddress("foo").futureValue.value mustEqual expected
    }

    "must return `None` when the service returns NOT_FOUND" in {

      server.stubFor(
        get(urlEqualTo("/api/confirmed?id=foo"))
          .willReturn(notFound())
      )

      service.retrieveAddress("foo").futureValue mustNot be (defined)
    }

    "must fail when the server responds with an error" in {

      server.stubFor(
        get(urlEqualTo("/api/confirmed?id=foo"))
          .willReturn(serverError())
      )

      service.retrieveAddress("foo").failed.futureValue
    }
  }
}
