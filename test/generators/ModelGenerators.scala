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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryDateFormat: Arbitrary[IndividualDateFormat] =
    Arbitrary {
      Gen.oneOf(IndividualDateFormat.values.toSeq)
    }

  implicit lazy val arbitraryIndividualBusinessDetails: Arbitrary[IndividualBusinessDetails] =
    Arbitrary {
      Gen.oneOf(IndividualBusinessDetails.values)
    }

  implicit lazy val arbitraryIndividualConnection: Arbitrary[IndividualConnection] =
    Arbitrary {
      Gen.oneOf(
        Gen.alphaStr.map(IndividualConnection.Other),
        Gen.const(IndividualConnection.Partner),
        Gen.const(IndividualConnection.ExPartner),
        Gen.const(IndividualConnection.FamilyMember),
        Gen.const(IndividualConnection.BusinessCompetitor),
        Gen.const(IndividualConnection.Employer),
        Gen.const(IndividualConnection.ExEmployer),
        Gen.const(IndividualConnection.Employee),
        Gen.const(IndividualConnection.Colleague),
        Gen.const(IndividualConnection.Friend),
        Gen.const(IndividualConnection.Neighbour),
        Gen.const(IndividualConnection.Customer),
        Gen.const(IndividualConnection.BusinessCompetitor)
      )
    }

  implicit lazy val arbitraryIndividualContactDetails: Arbitrary[IndividualContactDetails] =
    Arbitrary {
      for {
        landlineNumber <- arbitrary[Option[String]]
        mobileNumber   <- arbitrary[Option[String]]
        email          <- arbitrary[Option[String]]
      } yield IndividualContactDetails(landlineNumber, mobileNumber, email)
    }

  implicit lazy val arbitraryIndividualName: Arbitrary[IndividualName] =
    Arbitrary {
      for {
        firstName  <- arbitrary[Option[String]]
        middleName <- arbitrary[Option[String]]
        lastName   <- arbitrary[Option[String]]
        aliases    <- arbitrary[Option[String]]
      } yield IndividualName(firstName, middleName, lastName, aliases)
    }

  implicit lazy val arbitraryIndividualInformation: Arbitrary[IndividualInformation] =
    Arbitrary {
      Gen.oneOf(IndividualInformation.values)
    }

  implicit lazy val arbitraryIndividualOrBusiness: Arbitrary[IndividualOrBusiness] =
    Arbitrary {
      Gen.oneOf(IndividualOrBusiness.values)
    }

  implicit val arbitraryActivityType: Arbitrary[ActivityType] = Arbitrary(Gen.oneOf(ActivityType.list))
}
