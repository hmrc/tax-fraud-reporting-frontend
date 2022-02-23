package services

import com.github.tomakehurst.wiremock.client.WireMock._
import models._
import models.backend._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages._
import play.api.Application
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR}
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import java.time._
import java.time.format.DateTimeFormatter

class SubmissionServiceSpec extends AnyFreeSpec with Matchers with WireMockHelper with ScalaFutures with IntegrationPatience with OptionValues with TryValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.tax-fraud-reporting.port" -> server.port,
        "host" -> "http://localhost"
      )
      .build()

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE

  lazy val messages: Messages = application.injector.instanceOf[MessagesApi].preferred(List(Lang("en")))
  lazy val service: SubmissionService = application.injector.instanceOf[SubmissionService]

  "submit" - {

    lazy val individualAnswers: UserAnswers = UserAnswers("id")
      .set(ActivityTypePage, ActivityType.list.head).success.value
      .set(IndividualOrBusinessPage, IndividualOrBusiness.Individual).success.value
      .set(IndividualNamePage(Index(0)), IndividualName(
        firstName = Some("forename"),
        middleName = Some("middlename"),
        lastName = Some("surname"),
        aliases = Some("alias")
      )).success.value
      .set(IndividualAddressConfirmationPage(Index(0)), AddressResponse(
        lines = List("line 1", "line 2", "line 3", "line 4"), town = Some("town"), postcode = Some("postcode"), country = Some("country")
      )).success.value
      .set(IndividualContactDetailsPage(Index(0)), IndividualContactDetails(
        landlineNumber = Some("landline"), mobileNumber = Some("mobile"), email = Some("email")
      )).success.value
      .set(IndividualDateOfBirthPage(Index(0)), LocalDate.of(2000, 2, 1)).success.value
      .set(IndividualAgePage(Index(0)), 30).success.value
      .set(IndividualNationalInsuranceNumberPage(Index(0)), "nino").success.value
      .set(IndividualConnectionPage(Index(0)), IndividualConnection.Other("connection type")).success.value
      .set(IndividualBusinessDetailsPage(Index(0)), IndividualBusinessDetails.Yes).success.value
      .set(BusinessNamePage(Index(0)), "business name").success.value
      .set(TypeBusinessPage(Index(0)), "business type").success.value
      .set(BusinessAddressConfirmationPage(Index(0)), AddressResponse(
        lines = List("business line 1", "business line 2", "business line 3", "business line 4"),
        town = Some("town"), postcode = Some("business postcode"), country = Some("business country")
      )).success.value
      .set(BusinessContactDetailsPage(Index(0)), BusinessContactDetails(
        landlineNumber = Some("business landline"), mobileNumber = Some("business mobile"), email = Some("business email")
      )).success.value
      .set(ReferenceNumbersPage(Index(0)), ReferenceNumbers(
        vatRegistration = Some("vat number"), employeeRefNo = Some("employee ref no"), corporationTax = Some("ct utr")
      )).success.value
      .set(SelectConnectionBusinessPage(Index(0)), SelectConnectionBusiness.Other("business connection type")).success.value
      .set(ApproximateValuePage, 1234f).success.value
      .set(WhenActivityHappenPage, WhenActivityHappen.NotHappen).success.value
      .set(ActivityTimePeriodPage, ActivityTimePeriod.Later).success.value
      .set(HowManyPeopleKnowPage, HowManyPeopleKnow.OneToFiveIndividuals).success.value
      .set(DescriptionActivityPage, "additional details").success.value
      .set(SupportingDocumentPage, SupportingDocument.Yes).success.value
      .set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
      .set(YourContactDetailsPage, YourContactDetails(
        FirstName = "forename", LastName = "surname", Tel = "tel", Email = Some("email"), MemorableWord = "memorable word"
      )).success.value

    lazy val businessAnswers: UserAnswers = UserAnswers("id")
      .set(ActivityTypePage, ActivityType.list.head).success.value
      .set(IndividualOrBusinessPage, IndividualOrBusiness.Business).success.value
      .set(BusinessNamePage(Index(0)), "business name").success.value
      .set(TypeBusinessPage(Index(0)), "business type").success.value
      .set(BusinessAddressConfirmationPage(Index(0)), AddressResponse(
        lines = List("business line 1", "business line 2", "business line 3", "business line 4"),
        town = Some("town"), postcode = Some("business postcode"), country = Some("business country")
      )).success.value
      .set(BusinessContactDetailsPage(Index(0)), BusinessContactDetails(
        landlineNumber = Some("business landline"), mobileNumber = Some("business mobile"), email = Some("business email")
      )).success.value
      .set(ReferenceNumbersPage(Index(0)), ReferenceNumbers(
        vatRegistration = Some("vat number"), employeeRefNo = Some("employee ref no"), corporationTax = Some("ct utr")
      )).success.value
      .set(SelectConnectionBusinessPage(Index(0)), SelectConnectionBusiness.Other("business connection type")).success.value
      .set(ApproximateValuePage, 1234f).success.value
      .set(WhenActivityHappenPage, WhenActivityHappen.NotHappen).success.value
      .set(ActivityTimePeriodPage, ActivityTimePeriod.Later).success.value
      .set(HowManyPeopleKnowPage, HowManyPeopleKnow.OneToFiveIndividuals).success.value
      .set(DescriptionActivityPage, "additional details").success.value
      .set(SupportingDocumentPage, SupportingDocument.Yes).success.value
      .set(ProvideContactDetailsPage, ProvideContactDetails.Yes).success.value
      .set(YourContactDetailsPage, YourContactDetails(
        FirstName = "forename", LastName = "surname", Tel = "tel", Email = Some("email"), MemorableWord = "memorable word"
      )).success.value

    "must send the correct data and return a successful future when the server responds with CREATED for an individual payload" in {

      lazy val expectedJson: JsValue = Json.toJson(
        FraudReportBody(
          activityType = ActivityType.list.head.activityName,
          nominals = List(
            Nominal(
              person = Some(Person(
                name = Some(Name(
                  forename = Some("forename"),
                  surname = Some("surname"),
                  middleName = Some("middlename"),
                  alias = Some("alias")
                )),
                address = Some(Address(
                  addressLine1 = Some("line 1"),
                  addressLine2 = Some("line 2"),
                  addressLine3 = Some("line 3, line 4"),
                  townCity = Some("town"),
                  postcode = Some("postcode"),
                  country = Some("country")
                )),
                contact = Some(Contact(
                  landline = Some("landline"),
                  mobile = Some("mobile"),
                  email = Some("email")
                )),
                dob = Some(LocalDate.of(2000, 2, 1).format(dateFormatter)),
                age = Some(30),
                NINO = Some("nino"),
                connectionType = "connection type"
              )),
              business = Some(Business(
                businessName = Some("business name"),
                businessType = Some("business type"),
                address = Some(Address(
                  addressLine1 = Some("business line 1"),
                  addressLine2 = Some("business line 2"),
                  addressLine3 = Some("business line 3, business line 4"),
                  townCity = Some("town"),
                  postcode = Some("business postcode"),
                  country = Some("business country")
                )),
                contact = Some(Contact(
                  landline = Some("business landline"),
                  mobile = Some("business mobile"),
                  email = Some("business email")
                )),
                businessVatNo = Some("vat number"),
                ctUtr = Some("ct utr"),
                employeeRefNo = Some("employee ref no"),
                connectionType = "business connection type"
              ))
            )
          ),
          valueFraud = Some(1234),
          durationFraud = Some(messages("activityTimePeriod.later")),
          howManyKnow = Some(messages("howManyPeopleKnow.oneToFiveIndividuals")),
          additionalDetails = Some("additional details"),
          reporter = Some(Reporter(
            forename = Some("forename"),
            surname = Some("surname"),
            telephoneNumber = Some("tel"),
            emailAddress = Some("email"),
            memorableWord = Some("memorable word")
          )),
          hasEvidence = true
        )
      )

      server.stubFor(
        post(urlEqualTo("/tax-fraud-reporting/fraud-report"))
          .willReturn(status(CREATED))
      )

      service.submit(individualAnswers).futureValue

      server.verify(
        postRequestedFor(urlEqualTo("/tax-fraud-reporting/fraud-report"))
          .withRequestBody(equalToJson(Json.stringify(expectedJson)))
      )
    }

    "must send the correct data and return a successful future when the server responds with CREATED for a business payload" in {

      lazy val expectedJson: JsValue = Json.toJson(
        FraudReportBody(
          activityType = ActivityType.list.head.activityName,
          nominals = List(
            Nominal(
              person = None,
              business = Some(Business(
                businessName = Some("business name"),
                businessType = Some("business type"),
                address = Some(Address(
                  addressLine1 = Some("business line 1"),
                  addressLine2 = Some("business line 2"),
                  addressLine3 = Some("business line 3, business line 4"),
                  townCity = Some("town"),
                  postcode = Some("business postcode"),
                  country = Some("business country")
                )),
                contact = Some(Contact(
                  landline = Some("business landline"),
                  mobile = Some("business mobile"),
                  email = Some("business email")
                )),
                businessVatNo = Some("vat number"),
                ctUtr = Some("ct utr"),
                employeeRefNo = Some("employee ref no"),
                connectionType = "business connection type"
              ))
            )
          ),
          valueFraud = Some(1234),
          durationFraud = Some(messages("activityTimePeriod.later")),
          howManyKnow = Some(messages("howManyPeopleKnow.oneToFiveIndividuals")),
          additionalDetails = Some("additional details"),
          reporter = Some(Reporter(
            forename = Some("forename"),
            surname = Some("surname"),
            telephoneNumber = Some("tel"),
            emailAddress = Some("email"),
            memorableWord = Some("memorable word")
          )),
          hasEvidence = true
        )
      )

      server.stubFor(
        post(urlEqualTo("/tax-fraud-reporting/fraud-report"))
          .willReturn(status(CREATED))
      )

      service.submit(businessAnswers).futureValue

      server.verify(
        postRequestedFor(urlEqualTo("/tax-fraud-reporting/fraud-report"))
          .withRequestBody(equalToJson(Json.stringify(expectedJson)))
      )
    }

    "must fail if the answers cannot be turned into a valid fraud report" in {
      service.submit(UserAnswers("id")).failed.futureValue.getMessage mustEqual "Failed to create fraud report from user answers, failing pages: activityType, approximateValue, howManyPeopleKnow, nominals"
    }

    "must fail if the server responds with a failure" in {

      server.stubFor(
        post(urlEqualTo("/tax-fraud-reporting/fraud-report"))
          .willReturn(status(INTERNAL_SERVER_ERROR))
      )

      service.submit(individualAnswers).failed.futureValue.getMessage mustEqual "Tax fraud reporting service responded to submission with 500 status"
    }
  }
}