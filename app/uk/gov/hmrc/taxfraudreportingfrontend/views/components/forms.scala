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

package uk.gov.hmrc.taxfraudreportingfrontend.views.components

import javax.inject.{Inject, Singleton}

@Singleton
class forms @Inject() (
  val formWithCSRF: uk.gov.hmrc.govukfrontend.views.html.components.FormWithCSRF,
  val fieldSet: uk.gov.hmrc.taxfraudreportingfrontend.views.html.components.fieldset,
  val inputText: uk.gov.hmrc.taxfraudreportingfrontend.views.html.components.inputText,
  val inputRadio: uk.gov.hmrc.taxfraudreportingfrontend.views.html.components.inputRadio,
  val autocomplete: uk.gov.hmrc.taxfraudreportingfrontend.views.html.components.autocomplete,
  val errorSummary: uk.gov.hmrc.taxfraudreportingfrontend.views.html.components.errorSummary
)
