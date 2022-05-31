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

import models.addresslookup.ProposedAddress

object countOfResults {

  sealed trait ResultsCount

  case class ResultsList(res: Seq[ProposedAddress]) extends ResultsCount

  case object NoResults extends ResultsCount

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
