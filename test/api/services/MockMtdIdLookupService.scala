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

package api.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import api.models.errors.MtdError
import org.scalatest.TestSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

object MockMtdIdLookupService {
  type MtdIdServiceOutcome = Either[MtdError, String]
}

trait MockMtdIdLookupService extends TestSuite with MockFactory {

  val mockMtdIdLookupService: MtdIdLookupService = mock[MtdIdLookupService]

  object MockedMtdIdLookupService {

    def lookup(nino: String): CallHandler[Future[MtdIdLookupService.Outcome]] = {
      (mockMtdIdLookupService
        .lookup(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino, *, *)
    }

  }

}
