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

import config.Service
import controllers.routes
import models.{AddressResponse, NormalMode}
import play.api.Configuration
import play.api.http.HeaderNames
import play.api.http.Status.ACCEPTED
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookupService @Inject() (httpClient: HttpClient, configuration: Configuration, messagesApi: MessagesApi)(
  implicit ec: ExecutionContext
) {

  private val baseUrl: String = configuration.get[Service]("microservice.services.address-lookup-frontend").baseUrl
  private val hostUrl: String = configuration.get[String]("host")

  def startJourney(route: String)(implicit hc: HeaderCarrier): Future[String] = {

    val labels = List("en", "cy").map { lang =>
      val messages = messagesApi.preferred(List(Lang(lang)))
      Json.obj(lang -> Json.obj("appLevelLabels" -> Json.obj("navTitle" -> messages("service.name"))))
    }.foldLeft(Json.obj())(_ deepMerge _)

    val request = Json.obj(
      "version" -> 2,
      "options" -> Json.obj(
        "continueUrl"         -> s"$hostUrl$route",
        "serviceHref"         -> s"$hostUrl/${routes.IndexController.onPageLoad.url}",
        "phaseFeedbackLink"   -> "/help/beta",
        "showPhaseBanner"     -> true,
        "showBackButtons"     -> true,
        "pageHeadingStyle"    -> "govuk-heading-l",
        "includeHMRCBranding" -> false,
        "timeoutConfig" -> Json.obj(
          "timeoutAmount"       -> configuration.get[Int]("timeout-dialog.timeout"),
          "timeoutUrl"          -> routes.JourneyRecoveryController.onPageLoad().url, // TODO where should this go?
          "timeoutKeepAliveUrl" -> routes.ActivityTypeController.onPageLoad(NormalMode).url
        )
      ),
      "labels" -> labels
    )

    httpClient.POST[JsObject, HttpResponse](s"$baseUrl/api/init", request).flatMap { response =>
      if (response.status == ACCEPTED)
        response.header(HeaderNames.LOCATION)
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("Missing Location header")))
      else
        Future.failed(new Exception(s"Unexpected response from address lookup frontend: ${response.status}"))
    }
  }

  def retrieveAddress(id: String)(implicit hc: HeaderCarrier): Future[Option[AddressResponse]] =
    httpClient.GET[Option[AddressResponse]](s"$baseUrl/api/confirmed", List("id" -> id))

}
