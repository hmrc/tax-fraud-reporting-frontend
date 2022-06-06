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

import com.google.inject.ImplementedBy
import config.Service
import models.addresslookup.{AddressLookup, AddressRecord, Country, ProposedAddress}
import play.api.Configuration
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import scala.util.Try

@ImplementedBy(classOf[AddressLookupService])
trait AddressService {

  def lookup(postcode: String, filter: Option[String] = None)(implicit hc: HeaderCarrier): Future[Seq[ProposedAddress]]

}

@Singleton
class AddressLookupService @Inject() (httpClient: HttpClient, configuration: Configuration)(implicit
  ec: ExecutionContext
) extends AddressService {

  private val baseUrl: String = configuration.get[Service]("microservice.services.address-lookup").baseUrl

  override def lookup(postcode: String, filter: Option[String] = None)(implicit
    hc: HeaderCarrier
  ): Future[Seq[ProposedAddress]] = {

    val pc                 = postcode.replaceAll(" ", "")
    val newHc              = hc.withExtraHeaders("X-Hmrc-Origin" -> "fraud")
    val addressRequestBody = AddressLookup(pc, filter)

    httpClient.POST[AddressLookup, List[AddressRecord]](s"$baseUrl/lookup", addressRequestBody)(
      implicitly,
      implicitly,
      newHc,
      implicitly
    ).map { found =>
      val results = found.map { addr =>
        ProposedAddress(
          addr.id,
          addr.uprn,
          addr.parentUprn,
          addr.usrn,
          addr.organisation,
          Some(addr.address.postcode),
          Some(addr.address.town),
          addr.address.lines,
          if ("UK" == addr.address.country.code) Country("GB", "United Kingdom")
          else addr.address.country,
          addr.poBox
        )
      }.filterNot(a => a.country.code != "GB")

      results.sortWith { (a, b) =>
        def sort(zipped: Seq[(Option[Int], Option[Int])]): Boolean = zipped match {
          case (Some(nA), Some(nB)) :: tail =>
            if (nA == nB) sort(tail) else nA < nB
          case (Some(_), None) :: _ => true
          case (None, Some(_)) :: _ => false
          case _                    => mkString(a) < mkString(b)
        }

        sort(numbersIn(a).zipAll(numbersIn(b), None, None).toList)
      }
    }
  }

  def mkString(p: ProposedAddress) = p.lines.mkString(" ").toLowerCase()

  def numbersIn(p: ProposedAddress): Seq[Option[Int]] =
    "([0-9]+)".r.findAllIn(mkString(p)).map(n => Try(n.toInt).toOption).toSeq.reverse :+ None

}

case class Address(lines: List[String], town: String, postcode: String, subdivision: Option[Country], country: Country)
