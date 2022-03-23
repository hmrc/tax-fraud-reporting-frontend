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

import cats.data.NonEmptyChain
import cats.implicits._
import config.Service
import models._
import models.backend._
import pages._
import play.api.Configuration
import play.api.http.Status.CREATED
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionService @Inject() (httpClient: HttpClient, configuration: Configuration, messagesApi: MessagesApi)(
  implicit ec: ExecutionContext
) {

  private val baseUrl: String                  = configuration.get[Service]("microservice.services.tax-fraud-reporting").baseUrl
  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
  private implicit val messages: Messages      = messagesApi.preferred(List(Lang("en")))

  def submit(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Unit] = {

    val report: Either[NonEmptyChain[String], FraudReportBody] = (
      getAnswer(answers, ActivityTypePage),
      getAnswer(answers, ApproximateValuePage),
      getAnswer(answers, HowManyPeopleKnowPage),
      getNominals(answers).toRight(NonEmptyChain.one("nominals")),
      getAnswer(answers, ActivitySourceOfInformationPage)
    ).parMapN { (activityTypeNameKey, valueFraud, howManyKnow, nominals, informationSource) =>
      FraudReportBody(
        activityType = messages(s"activityType.name.$activityTypeNameKey"),
        nominals = nominals,
        valueFraud = Some(valueFraud),
        durationFraud = getFraudDuration(answers),
        howManyKnow = Some(messages(s"howManyPeopleKnow.$howManyKnow")),
        additionalDetails = answers.get(DescriptionActivityPage),
        reporter = getReporter(answers),
        hasEvidence = answers.get(SupportingDocumentPage).contains(SupportingDocument.Yes),
        informationSource = getInformationSource(informationSource),
        evidenceDetails = sanitizeOptionalString(answers.get(DocumentationDescriptionPage))
      )
    }

    report match {
      case Right(payload) =>
        httpClient.POST[FraudReportBody, HttpResponse](s"$baseUrl/tax-fraud-reporting/fraud-report", payload).flatMap {
          response =>
            if (response.status == CREATED)
              Future.successful(())
            else
              Future.failed(
                new Exception(s"Tax fraud reporting service responded to submission with ${response.status} status")
              )
        }
      case Left(errors) =>
        Future.failed(
          new Exception(
            s"Failed to create fraud report from user answers, failing pages: ${errors.toList.mkString(", ")}"
          )
        )
    }
  }

  private def getAnswer[A](answers: UserAnswers, page: QuestionPage[A])(implicit
    rds: Reads[A]
  ): Either[NonEmptyChain[String], A] =
    answers.get(page).toRight(NonEmptyChain.one(page.toString))

  private def getNominals(answers: UserAnswers): Option[Seq[Nominal]] =
    answers.get(IndividualOrBusinessPage).map {
      case IndividualOrBusiness.Individual =>
        val numberOfNominals = answers.get(NominalsQuery).getOrElse(List.empty).length
        (0 until numberOfNominals).map { index =>
          val p = getPerson(answers, index)
          val b = answers.get(IndividualBusinessDetailsPage(Index(index))).flatMap {
            case IndividualBusinessDetails.Yes => getBusiness(answers, index)
            case _                             => None
          }
          Nominal(p, b)
        }
      case IndividualOrBusiness.Business =>
        List(Nominal(None, getBusiness(answers, 0)))
    }

  private def getPerson(answers: UserAnswers, index: Int): Option[Person] = for {
    connection <- answers.get(IndividualConnectionPage(Index(index)))
  } yield Person(
    name = answers.get(IndividualNamePage(Index(index))).map { name =>
      Name(
        forename = sanitizeOptionalString(name.firstName),
        surname = sanitizeOptionalString(name.lastName),
        middleName = sanitizeOptionalString(name.middleName),
        alias = sanitizeOptionalString(name.aliases)
      )
    },
    address = answers.get(IndividualAddressPage(Index(index))),
    contact = answers.get(IndividualContactDetailsPage(Index(index))).map { details =>
      Contact(landline = details.landlineNumber, mobile = details.mobileNumber, email = details.email)
    },
    dob = answers.get(IndividualDateOfBirthPage(Index(index))).map(_.format(dateFormatter)),
    age = answers.get(IndividualAgePage(Index(index))),
    NINO = answers.get(IndividualNationalInsuranceNumberPage(Index(index))),
    connectionType = connection match {
      case IndividualConnection.Other(value) => value
      case _                                 => messages(s"individualConnection.$connection")
    }
  )

  private def getBusiness(answers: UserAnswers, index: Int): Option[Business] = for {
    connection <- answers.get(SelectConnectionBusinessPage(Index(index)))
  } yield Business(
    businessName = sanitizeOptionalString(answers.get(answers.get(BusinessNamePage(Index(index)))),
    businessType = sanitizeOptionalString(answers.get(answers.get(TypeBusinessPage(Index(index)))),
    address = answers.get(BusinessAddressPage(Index(index))),
    contact = answers.get(BusinessContactDetailsPage(Index(index))).map { details =>
      Contact(landline = details.landlineNumber, mobile = details.mobileNumber, email = details.email)
    },
    businessVatNo = answers.get(ReferenceNumbersPage(Index(index))).flatMap(_.vatRegistration),
    ctUtr = answers.get(ReferenceNumbersPage(Index(index))).flatMap(_.corporationTax),
    employeeRefNo = answers.get(ReferenceNumbersPage(Index(index))).flatMap(_.employeeRefNo),
    connectionType = connection match {
      case SelectConnectionBusiness.Other(value) => value
      case _                                     => messages(s"selectConnectionBusiness.$connection")
    }
  )

  private def getReporter(answers: UserAnswers): Option[Reporter] =
    for {
      provideContactDetails <- answers.get(ProvideContactDetailsPage)
      if provideContactDetails == ProvideContactDetails.Yes
      details <- answers.get(YourContactDetailsPage)
    } yield Reporter(
      forename = sanitizeString(details.FirstName), // TODO Should these be optional? They're guaranteed to exist
      surname = sanitizeString(details.LastName),
      telephoneNumber = Some(details.Tel),
      emailAddress = details.Email,
      memorableWord = sanitizeOptionalString(details.MemorableWord)
    )

  // TODO more tests for this please
  private def getFraudDuration(answers: UserAnswers): Option[String] =
    answers.get(WhenActivityHappenPage).flatMap {
      case WhenActivityHappen.NotHappen =>
        answers.get(ActivityTimePeriodPage).map(period => messages(s"activityTimePeriod.$period"))
      case whenActivityHappen => Some(messages(s"whenActivityHappen.$whenActivityHappen"))
    }

  private def getInformationSource(answer: ActivitySourceOfInformation): String =
    answer match {
      case ActivitySourceOfInformation.Other(value) => value
      case _                                        => messages(s"activitySourceOfInformation.$answer")
    }

  private def sanitizeString(optionalStringVal: String): Option[String] = optionalStringVal match {
    case string if string != null => Some(string.replaceAll("\\?|\\*", ""))
    case _                        => Option.empty
  }

  private def sanitizeOptionalString(string: Option[String]): Option[String] =
    string.filter(_.nonEmpty).map(_ replaceAll ("\\?|\\*", ""))

}

object SubmissionService {

  def getErrorMessage(errors: Seq[String]) =
    s"Failed to create fraud report from user answers, failing pages: ${errors mkString ", "}"

}
