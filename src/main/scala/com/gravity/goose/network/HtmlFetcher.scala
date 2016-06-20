/**
 * Licensed to Gravity.com under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Gravity.com licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravity.goose.network

import org.apache.http.{ProtocolVersion, HttpEntity, HttpResponse, HttpVersion}
import org.apache.http.client.{RedirectStrategy, CookieStore, HttpClient}
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.http.client.utils.URIBuilder

import org.apache.http.conn.ssl.{SSLConnectionSocketFactory, TrustSelfSignedStrategy, SSLSocketFactory}
import org.apache.http.ssl.{SSLContextBuilder}

import org.apache.http.client.params.CookiePolicy
import org.apache.http.client.protocol.ClientContext
import org.apache.http.conn.scheme.PlainSocketFactory
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.cookie.Cookie
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.apache.http.params.HttpProtocolParams
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils
import java.io._
import java.net.{URL, SocketException, SocketTimeoutException, URLConnection}
import java.util.ArrayList
import java.util.Date
import java.util.List
import com.gravity.goose.utils.Logging
import com.gravity.goose.Configuration
import org.apache.http.impl.client.{HttpClients, DefaultHttpRequestRetryHandler, AbstractHttpClient, DefaultHttpClient}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


/**
 * User: Jim Plush
 * Date: 12/16/10
 * This guy is kind of a doozy because goose is meant to pull millions of articles per day so the legitimacy of these links
 * is in question. For example many times you'll see mp3, mov, wav, etc.. files mislabeled as HTML with HTML content types,
 * only through inspection of the actual content will you learn what the real type of content is. Also spam sites could
 * contain up to 1GB of text that is just wasted resources so we set a max bytes level on how much content we're going
 * to try and pull back before we say screw it.
 */
object HtmlFetcher extends AbstractHtmlFetcher with Logging {
  /**
   * holds a reference to our override cookie store, we don't want to store
   * cookies for head requests, only slows shit down
   */
  var emptyCookieStore: CookieStore = null
  /**
   * holds the HttpClient object for making requests
   */
  private var httpClient: HttpClient = null
  initHttpClient()
//  initClient()


  def getHttpClient: HttpClient = {
    httpClient
  }

  /**
   * Makes an http fetch to go retrieve the HTML from a url, store it to disk and pass it off
    *
    * @param config Goose Configuration
   * @param url The web address to fetch
   * @return If all goes well, a `Some[String]` otherwise `None`
   * @throws NotFoundException(String)
   * @throws BadRequestException(String)
   * @throws NotAuthorizedException(String, Int)
   * @throws ServerErrorException(String, Int)
   * @throws UnhandledStatusCodeException(String, Int)
   * @throws MaxBytesException()
   */
  def getHtml(config: Configuration, url: String): Option[String] = {

    var httpget: HttpGet = null
    var htmlResult: String = null
    var entity: HttpEntity = null
    var instream: InputStream = null
    var encodingType: String = "UTF-8"
    import org.apache.http.client.params.ClientPNames

    // Identified the the apache http client does not drop URL fragments before opening the request to the host
    // more info: http://stackoverflow.com/questions/4251841/400-error-with-httpclient-for-a-link-with-an-anchor
    val cleanUrl = {
      val foundAt = url.indexOf("#")
      if (foundAt >= 0) url.substring(0, foundAt) else url
    }


    try {
      val localContext: HttpContext = new BasicHttpContext

      localContext.setAttribute(ClientContext.COOKIE_STORE, HtmlFetcher.emptyCookieStore)

      httpget = initHttpGet(cleanUrl, config)
      httpget.setHeader("User-Agent", config.getBrowserUserAgent())

//      httpClient = initHttpClient2(config)
      val response: HttpResponse = httpClient.execute(httpget)

//      val response: HttpResponse = if (is_ssl) httpClient.execute(httpget) else
//        httpClient.execute(httpget, localContext)
//      val response: HttpResponse = httpClient.execute(httpget, localContext)

      HttpStatusValidator.validate(cleanUrl, response.getStatusLine.getStatusCode) match {
        case Left(ex) => throw ex
        case _ =>
      }

      entity = response.getEntity
      if (entity != null) {
        instream = entity.getContent
        var encodingType = EntityUtils.getContentCharSet(entity)
        try {

          if  (encodingType == null) {
            val contentType = entity.getContentType().toString
            val start_ix =  contentType.indexOf("charset=")
            if (start_ix > 0){
              encodingType = contentType.substring(start_ix+9)
            } else {

//              }
              if(instream.available() == 0){
                entity = httpClient.execute(httpget, localContext).getEntity()
                instream = entity.getContent
              }

              val rawlHtml =  EntityUtils.toString(entity)
              val is = new ByteArrayInputStream(rawlHtml.getBytes())
              val doc = Jsoup.parse(rawlHtml)
              encodingType = getCharSet(doc)

            }
          }

          if (encodingType == null) {
            encodingType = "UTF-8"//"UTF-8" ISO-8859-15
          }
        }
        catch {
          case e: Exception => {
            encodingType = "UTF-8"
            if (logger.isDebugEnabled) {
              trace("Unable to get charset for: " + cleanUrl)
              trace("Encoding Type is: " + encodingType)
            }
          }
        }
        try {
          if(instream.available() == 0){
            entity = httpClient.execute(httpget, localContext).getEntity()
            instream = entity.getContent
          }
          htmlResult = HtmlFetcher.convertStreamToString(instream, 15728640, encodingType).trim
          new String(htmlResult.getBytes("UTF-8"), "UTF8")

        }
        finally {
          EntityUtils.consume(entity)
        }
      }
      else {
        trace("Unable to fetch URL Properly: " + cleanUrl)
      }
    }
    catch {
      case e: NullPointerException => {
        logger.warn(e.toString + " " + e.getMessage + " Caught for URL: " + cleanUrl)
      }
      case e: MaxBytesException => {
        trace("GRVBIGFAIL: " + cleanUrl + " Reached max bytes size")
        throw e
      }
      case e: SocketException => {
        logger.warn(e.getMessage + " Caught for URL: " + cleanUrl)
      }
      case e: SocketTimeoutException => {
        trace(e.toString)
      }
      case e: LoggableException => {
        logger.warn(e.getMessage)
        return None
      }
      case e: Exception => {
        trace("FAILURE FOR LINK: " + cleanUrl + " " + e.toString)
        return None
      }
    }
    finally {
      if (instream != null) {
        try {
          instream.close()
        }
        catch {
          case e: Exception => {
            logger.warn(e.getMessage + " Caught for URL: " + cleanUrl)
          }
        }
      }
      if (httpget != null) {
        try {
          httpget.abort()
          entity = null
        }
        catch {
          case e: Exception => {
          }
        }
      }
    }
    if (logger.isDebugEnabled) {
      logger.debug("starting...")
    }
    if (htmlResult == null || htmlResult.length < 1) {
      if (logger.isDebugEnabled) {
        logger.debug("HTMLRESULT is empty or null")
      }
      throw new NotHtmlException(cleanUrl)
    }
    var is: InputStream = null
    var mimeType: String = null
    try {
      is = new ByteArrayInputStream(htmlResult.getBytes(encodingType))
      mimeType = URLConnection.guessContentTypeFromStream(is)
      if (mimeType != null) {
        if ((mimeType == "text/html") == true || (mimeType == "application/xml") == true) {
          return Some(htmlResult)
        }
        else {
          if (htmlResult.contains("<title>") == true && htmlResult.contains("<p>") == true) {
            return Some(htmlResult)
          }
          trace("GRVBIGFAIL: " + mimeType + " - " + cleanUrl)
          throw new NotHtmlException(cleanUrl)
        }
      }
      else {
        return Some(htmlResult)
//        throw new NotHtmlException(cleanUrl)
      }
    }
    catch {
      case e: UnsupportedEncodingException => {
        logger.warn(e.getMessage + " Caught for URL: " + cleanUrl)
      }
      case e: IOException => {
        logger.warn(e.getMessage + " Caught for URL: " + cleanUrl)
      }
    }
    None
  }

  private def getContentCharSet(entity: HttpEntity): String ={

    val default_charset = "UTF-8"
    var encodingCharSet = EntityUtils.getContentCharSet(entity)

    if  (encodingCharSet == null) {
      val contentType = entity.getContentType().toString
      val start_ix =  contentType.indexOf("charset=")
      if (start_ix > 0){
        encodingCharSet = contentType.substring(start_ix+9)
      } else{
        //still can't find it?  look through html

        try {
          val instream = entity.getContent
          val rawlHtml = EntityUtils.toString(entity)
          val is = new ByteArrayInputStream(rawlHtml.getBytes())
          val doc = Jsoup.parse(rawlHtml)
          encodingCharSet = getCharSet(doc)
        }
        finally {

          EntityUtils.consume(entity)
        }
      }
    }

    encodingCharSet
  }

  def getCharSet(doc: Document): String ={

    var meta_content_type: String = ""
    var encodingCharSet: String = null

      if (doc != None) {

        var slice_index =  0
        if (doc.select("meta[charset]").size() > 0){
          meta_content_type = doc.select("meta[charset]").first.toString
          slice_index =   meta_content_type.indexOfSlice("charset=") + 9
        }else if(doc.select("meta[http-equiv=Content-Type]").size() > 0){
          meta_content_type =  doc.select("meta[http-equiv=Content-Type]").first.toString
          slice_index =   meta_content_type.indexOfSlice("charset=") + 8
        }

        if (slice_index >= 8){
          val meta_content_type_slice =  meta_content_type.substring(slice_index)
          val possible_endix_array = Array(meta_content_type_slice.indexOf("'"), meta_content_type_slice.indexOf("\""), meta_content_type_slice.indexOf(" ")).filter(i => i > 0)
          if (possible_endix_array.length > 0 ){
            val end_ix =   possible_endix_array.reduceLeft(_ min _)
            encodingCharSet = meta_content_type_slice.substring(0, end_ix)
          }
        }
      }

    encodingCharSet
  }

  private def initHttpClient(config: Configuration = null): HttpClient = {

    val connection_timeout = if (config != null) config.getConnectionTimeout() else 10000
    val socket_timeout = if (config != null) config.getSocketTimeout() else 10000

    val request_config = RequestConfig.custom()
     .setConnectTimeout(connection_timeout).setCircularRedirectsAllowed(true)
     .setSocketTimeout(socket_timeout).setRedirectsEnabled(true).setMaxRedirects(3)
     .build();

    val builder = new SSLContextBuilder();
    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy())
    val sslsf = new SSLConnectionSocketFactory(builder.build());
    httpClient = HttpClients.custom().setSSLSocketFactory(
                sslsf).setDefaultRequestConfig(request_config).build()
    httpClient
  }

  private def initHttpGet(url: String, config: Configuration): HttpGet ={
    val httpget = new HttpGet(url)
    httpget.setHeader("User-Agent", config.getBrowserUserAgent())
    httpget.setHeader("Accept-Language", "en-us")
    httpget.setHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5")
    httpget.setHeader("Cache-Control", "max-age=0")
//    httpget.setProtocolVersion(new ProtocolVersion("HTTP", 1, 1))

    httpget
  }

  private def initClient() {

    trace("Initializing HttpClient")

    val httpParams: HttpParams = new BasicHttpParams
    HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000)
    HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000)
    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1)
    emptyCookieStore = new CookieStore {
      def addCookie(cookie: Cookie) {
      }

      def getCookies: List[Cookie] = {
        emptyList
      }

      def clearExpired(date: Date): Boolean = {
        false
      }

      def clear() {
      }

      private[network] var emptyList: ArrayList[Cookie] = new ArrayList[Cookie]
    }
    httpParams.setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY)
    httpParams.setParameter("http.User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; de; rv:1.9.2.8) Gecko/20100723 Ubuntu/10.04 (lucid) Firefox/3.6.8")
    httpParams.setParameter("http.language.Accept-Language", "en-us")
    httpParams.setParameter("http.protocol.content-charset", "ISO-8859-15")
    httpParams.setParameter("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5")
    httpParams.setParameter("Cache-Control", "max-age=0")
    httpParams.setParameter("http.connection.stalecheck", false)
    val schemeRegistry: SchemeRegistry = new SchemeRegistry
    schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory))
    schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory))
    val cm = new ThreadSafeClientConnManager(schemeRegistry)
    cm.setMaxTotal(20000)
    cm.setDefaultMaxPerRoute(500)
    httpClient = new DefaultHttpClient(cm, httpParams)
    httpClient.asInstanceOf[AbstractHttpClient].setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
    httpClient.getParams.setParameter("http.conn-manager.timeout", 120000L)
    httpClient.getParams.setParameter("http.protocol.wait-for-continue", 10000L)
    httpClient.getParams.setParameter("http.tcp.nodelay", true)
  }

  /**
   * reads bytes off the string and returns a string
   *
   * @param is the source stream from the response
   * @param maxBytes The max bytes that we want to read from the input stream
   * @return String
   */
  def convertStreamToString(is: InputStream, maxBytes: Int, encodingType: String): String = {
    val buf: Array[Char] = new Array[Char](2048)
    var r: Reader = null
    val s = new StringBuilder
    try {
      r = new InputStreamReader(is, encodingType)
      var bytesRead: Int = 2048
      var inLoop = true
      while (inLoop) {
        if (bytesRead >= maxBytes) {
          throw new MaxBytesException
        }
        var n: Int = r.read(buf)
        bytesRead += 2048

        if (n < 0) inLoop = false
        if (inLoop) s.appendAll(buf, 0, n)
      }
      return s.toString()
    }
    catch {
      case e: SocketTimeoutException => {
        logger.warn(e.toString + " " + e.getMessage)
      }
      case e: UnsupportedEncodingException => {
        logger.warn(e.toString + " Encoding: " + encodingType)
      }
      case e: IOException => {
        logger.warn(e.toString + " " + e.getMessage)
      }
    }
    finally {
      if (r != null) {
        try {
          r.close()
        }
        catch {
          case e: Exception => {
          }
        }
      }
    }
    null
  }


}


