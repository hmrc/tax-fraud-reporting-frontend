/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.taxfraudreportingfrontend.forms

import play.api.data.Forms.mapping
import play.api.data.{Form, Forms}
import uk.gov.hmrc.taxfraudreportingfrontend.forms.mappings.Mappings
import uk.gov.hmrc.taxfraudreportingfrontend.services.ActivityTypeService
import uk.gov.hmrc.taxfraudreportingfrontend.viewmodels.ActivityTypeViewModel

import javax.inject.Inject
import scala.language.postfixOps

class ActivityTypeProvider @Inject() (activityTypeService: ActivityTypeService) extends Mappings {

  def apply(): Form[ActivityTypeViewModel] =
    Form(
      mapping(
        "activityType" -> Forms.text.verifying(
          "activityType.error.invalid",
          code => activityTypeService.isValidActivityTypeCode(code)

        )
      )(code => ActivityTypeViewModel from (activityTypeService getActivityTypeByCode code get))(
        model => Some(model.code)
      )
    )

}
