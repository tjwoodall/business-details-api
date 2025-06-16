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

package api.connectors

import config.{AppConfig, FeatureSwitches}
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import utils.Logging

import java.net.URLEncoder
import scala.concurrent.{ExecutionContext, Future}

trait BaseDownstreamConnector extends Logging {
  val httpClientV2: HttpClientV2
  val appConfig: AppConfig

  // This is to provide an implicit AppConfig in existing connector implementations (which
  // typically declare the abstract `appConfig` field non-implicitly) without having to change them.
  implicit protected lazy val _appConfig: AppConfig = appConfig

  implicit protected lazy val featureSwitches: FeatureSwitches = FeatureSwitches(appConfig)

  private val jsonContentTypeHeader = Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)

//  def post[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit
//      ec: ExecutionContext,
//      hc: HeaderCarrier,
//      httpReads: HttpReads[DownstreamOutcome[Resp]],
//      correlationId: String): Future[DownstreamOutcome[Resp]] = {
//
//    val strategy = uri.strategy
//
//    def doPost(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
//      http.POST(getBackendUri(uri.path, strategy), body)
//    }
//
//    for {
//      headers <- getBackendHeaders(strategy, jsonContentTypeHeader)
//      result  <- doPost(headers)
//    } yield result
//  }

  def post[Body: Writes, Resp](
      body: Body,
      uri: DownstreamUri[Resp]
  )(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String,
      httpClientV2: HttpClientV2
  ): Future[DownstreamOutcome[Resp]] = {

    val strategy = uri.strategy

    for {
      headers <- getBackendHeaders(strategy, jsonContentTypeHeader)
      result <- httpClientV2
        .post(url"${getBackendUri(uri.path, strategy)}")
        .withBody(Json.toJson(body))
        .setHeader(headers: _*) // works now, because headers is Seq[(String, String)]
        .execute[DownstreamOutcome[Resp]]
    } yield result
  }

//  def get3[Resp](uri: DownstreamUri[Resp], queryParams: Seq[(String, String)] = Seq.empty, maybeIntent: Option[String] = None)(implicit
//      ec: ExecutionContext,
//      hc: HeaderCarrier,
//      httpReads: HttpReads[DownstreamOutcome[Resp]],
//      correlationId: String): Future[DownstreamOutcome[Resp]] = {
//
//    val strategy = uri.strategy
//
//    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] =
//      http.GET(getBackendUri(uri.path, strategy), queryParams)
//
//    for {
//      headers <- getBackendHeaders(strategy, intentHeader(maybeIntent))
//      result  <- doGet(headers)
//    } yield result
//  }

  def get[Resp](
      uri: DownstreamUri[Resp],
      queryParams: Seq[(String, String)] = Seq.empty,
      maybeIntent: Option[String] = None
  )(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String
      // httpClientV2: HttpClientV2
  ): Future[DownstreamOutcome[Resp]] = {

    val strategy = uri.strategy

    val baseUri = getBackendUri(uri.path, strategy)
    val fullUri = buildUrlWithParams(baseUri, queryParams)

    for {
      headers <- getBackendHeaders(strategy, intentHeader(maybeIntent))
      result <- httpClientV2
        .get(url"$fullUri")
        .setHeader(headers: _*)
        .execute[DownstreamOutcome[Resp]]
    } yield result
  }

//  def delete[Resp](uri: DownstreamUri[Resp])(implicit
//      ec: ExecutionContext,
//      hc: HeaderCarrier,
//      httpReads: HttpReads[DownstreamOutcome[Resp]],
//      correlationId: String): Future[DownstreamOutcome[Resp]] = {
//
//    val strategy = uri.strategy
//
//    def doDelete(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
//      http.DELETE(getBackendUri(uri.path, strategy))
//    }
//
//    for {
//      headers <- getBackendHeaders(strategy)
//      result  <- doDelete(headers)
//    } yield result
//  }

  def delete[Resp](
      uri: DownstreamUri[Resp]
  )(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String,
      httpClientV2: HttpClientV2
  ): Future[DownstreamOutcome[Resp]] = {

    val strategy = uri.strategy

    for {
      headers <- getBackendHeaders(strategy)
      result <- httpClientV2
        .delete(url"${getBackendUri(uri.path, strategy)}")
        .setHeader(headers: _*)
        .execute[DownstreamOutcome[Resp]]
    } yield result
  }

//  def put[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit
//      ec: ExecutionContext,
//      hc: HeaderCarrier,
//      httpReads: HttpReads[DownstreamOutcome[Resp]],
//      correlationId: String): Future[DownstreamOutcome[Resp]] = {
//
//    val strategy = uri.strategy
//
//    def doPut(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
//      http.PUT(getBackendUri(uri.path, strategy), body)
//    }
//
//    for {
//      headers <- getBackendHeaders(strategy, jsonContentTypeHeader)
//      result  <- doPut(headers)
//    } yield result
//  }

  def put[Body: Writes, Resp](
      body: Body,
      uri: DownstreamUri[Resp]
  )(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String
      // httpClientV2: HttpClientV2
  ): Future[DownstreamOutcome[Resp]] = {

    val strategy = uri.strategy

    for {
      headers <- getBackendHeaders(strategy, jsonContentTypeHeader)
      result <- httpClientV2
        .put(url"${getBackendUri(uri.path, strategy)}")
        .withBody(Json.toJson(body))
        .setHeader(headers: _*)
        .execute[DownstreamOutcome[Resp]]
    } yield result
  }

  private def intentHeader(maybeIntent: Option[String]): Seq[(String, String)] =
    maybeIntent.map(intent => Seq("intent" -> intent)).getOrElse(Nil)

  private def getBackendUri(path: String, strategy: DownstreamStrategy): String =
    s"${strategy.baseUrl}/$path"

  private def getBackendHeaders(strategy: DownstreamStrategy, additionalHeaders: Seq[(String, String)] = Seq.empty)(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      correlationId: String): Future[Seq[(String, String)]] = {

    for {
      contractHeaders <- strategy.contractHeaders(correlationId)
    } yield {
      val apiHeaders = hc.extraHeaders ++ contractHeaders ++ additionalHeaders

      val passThroughHeaders = hc
        .headers(strategy.environmentHeaders)
        .filterNot(hdr => apiHeaders.exists(_._1.equalsIgnoreCase(hdr._1)))

      // HeaderCarrier(extraHeaders = apiHeaders ++ passThroughHeaders)
      val allHeaders: Seq[(String, String)] = (apiHeaders ++ passThroughHeaders)

      allHeaders
    }
  }

  private def buildUrlWithParams(baseUrl: String, queryParams: Seq[(String, String)]): String = {
    val encodedParams = queryParams
      .map { case (k, v) =>
        s"${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
      }
      .mkString("&")

    if (encodedParams.nonEmpty) s"$baseUrl?$encodedParams" else baseUrl
  }

}
