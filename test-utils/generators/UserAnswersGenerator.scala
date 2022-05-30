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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(BusinessChooseYourAddressPage.type, JsValue)] ::
    arbitrary[(BusinessFindAddressPage.type, JsValue)] ::
    arbitrary[(ChooseYourAddressPage.type, JsValue)] ::
      arbitrary[(FindAddressPage.type, JsValue)] ::
      arbitrary[(BusinessSelectCountryPage, JsValue)] ::
      arbitrary[(IndividualSelectCountryPage, JsValue)] ::
      arbitrary[(IndividualConfirmRemovePage, JsValue)] ::
      arbitrary[(ActivitySourceOfInformationPage.type, JsValue)] ::
      arbitrary[(DocumentationDescriptionPage.type, JsValue)] ::
      arbitrary[(YourContactDetailsPage.type, JsValue)] ::
      arbitrary[(ProvideContactDetailsPage.type, JsValue)] ::
      arbitrary[(SupportingDocumentPage.type, JsValue)] ::
      arbitrary[(AddAnotherPersonPage.type, JsValue)] ::
      arbitrary[(HowManyPeopleKnowPage.type, JsValue)] ::
      arbitrary[(ActivityTimePeriodPage.type, JsValue)] ::
      arbitrary[(WhenActivityHappenPage.type, JsValue)] ::
      arbitrary[(ApproximateValuePage.type, JsValue)] ::
      arbitrary[(BusinessContactDetailsPage, JsValue)] ::
      arbitrary[(ReferenceNumbersPage, JsValue)] ::
      arbitrary[(SelectConnectionBusinessPage, JsValue)] ::
      arbitrary[(BusinessInformationCheckPage, JsValue)] ::
      arbitrary[(TypeBusinessPage, JsValue)] ::
      arbitrary[(BusinessInformationCheckPage, JsValue)] ::
      arbitrary[(IndividualAgePage, JsValue)] ::
      arbitrary[(IndividualDateOfBirthPage, JsValue)] ::
      arbitrary[(IndividualDateFormatPage, JsValue)] ::
      arbitrary[(BusinessNamePage, JsValue)] ::
      arbitrary[(IndividualBusinessDetailsPage, JsValue)] ::
      arbitrary[(IndividualConnectionPage, JsValue)] ::
      arbitrary[(IndividualNationalInsuranceNumberPage, JsValue)] ::
      arbitrary[(IndividualContactDetailsPage, JsValue)] ::
      arbitrary[(IndividualNamePage, JsValue)] ::
      arbitrary[(IndividualInformationPage, JsValue)] ::
      arbitrary[(IndividualOrBusinessPage.type, JsValue)] ::
      arbitrary[(ActivityTypePage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id <- nonEmptyString
        data <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers(
        id = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }

}
