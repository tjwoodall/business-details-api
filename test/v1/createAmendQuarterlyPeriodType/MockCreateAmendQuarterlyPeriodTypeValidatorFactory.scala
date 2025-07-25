/*
 * Copyright 2025 HM Revenue & Customs
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

package v1.createAmendQuarterlyPeriodType

import api.controllers.validators.Validator
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import play.api.libs.json.JsValue
import v1.createAmendQuarterlyPeriodType.model.request.CreateAmendQuarterlyPeriodTypeRequestData

trait MockCreateAmendQuarterlyPeriodTypeValidatorFactory extends TestSuite with MockFactory {

  val mockCreateAmendQuarterlyPeriodTypeValidatorFactory: CreateAmendQuarterlyPeriodTypeValidatorFactory =
    mock[CreateAmendQuarterlyPeriodTypeValidatorFactory]

  object MockedCreateAmendQuarterlyPeriodTypeValidatorFactory {

    def expectValidator(): CallHandler[Validator[CreateAmendQuarterlyPeriodTypeRequestData]] = {
      (mockCreateAmendQuarterlyPeriodTypeValidatorFactory
        .validator(_: String, _: String, _: String, _: JsValue))
        .expects(*, *, *, *)
    }

  }

  def willUseValidator(
      use: Validator[CreateAmendQuarterlyPeriodTypeRequestData]): CallHandler[Validator[CreateAmendQuarterlyPeriodTypeRequestData]] = {
    MockedCreateAmendQuarterlyPeriodTypeValidatorFactory
      .expectValidator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: CreateAmendQuarterlyPeriodTypeRequestData): Validator[CreateAmendQuarterlyPeriodTypeRequestData] =
    new Validator[CreateAmendQuarterlyPeriodTypeRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendQuarterlyPeriodTypeRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[CreateAmendQuarterlyPeriodTypeRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[CreateAmendQuarterlyPeriodTypeRequestData] =
    new Validator[CreateAmendQuarterlyPeriodTypeRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendQuarterlyPeriodTypeRequestData] = Invalid(result)
    }

}
