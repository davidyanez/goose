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

package com.gravity.goose

import cleaners.{StandardDocumentCleaner, DocumentCleaner}

import extractors.ContentExtractor
import images.{Image, UpgradedImageIExtractor, ImageExtractor}
import org.apache.http.client.HttpClient
import org.jsoup.nodes.{Document, Element}
import org.jsoup.Jsoup
import java.io._

import utils.{ParsingCandidate, URLHelper, Logging}
import com.gravity.goose.outputformatters.{StandardOutputFormatter, OutputFormatter}
import scala.collection.JavaConverters._

/**
 * Created by Jim Plush
 * User: jim
 * Date: 8/18/11
 */

case class CrawlCandidate(config: Configuration, url: String, rawHTML: String = null)

case class HtmlExtractResponse(html: String, status: HTMLExtractStatus.Status, msg: String = "")

object HTMLExtractStatus extends Enumeration {
  type Status = Value
  val OK, FAILED = Value
}

class Crawler(config: Configuration) {

  import Crawler._

  def crawl(crawlCandidate: CrawlCandidate): Article = {
    val article = new Article()
    for {
      parseCandidate <- URLHelper.getCleanedUrl(crawlCandidate.url)
      rawHtml <- getHTML(crawlCandidate, parseCandidate)
      doc <- getDocument(rawHtml)
    } {
      trace("Crawling url: " + parseCandidate.url)

      val extractor = getExtractor
      val docCleaner = getDocCleaner
      val outputFormatter = getOutputFormatter

      article.finalUrl = parseCandidate.url.toString
      article.domain = parseCandidate.url.getHost
      article.linkhash = parseCandidate.linkhash
      article.rawHtml = rawHtml
      article.doc = doc
      article.rawDoc = doc.clone()

      article.title = extractor.getTitle(article)
      article.publishDate = config.publishDateExtractor.extract(doc)
      article.additionalData = config.getAdditionalDataExtractor.extract(doc)
      article.metaDescription = extractor.getMetaDescription(article)
      article.metaKeywords = extractor.getMetaKeywords(article)
      article.canonicalLink = extractor.getCanonicalLink(article)
      article.tags = extractor.extractTags(article)
      // before we do any calcs on the body itself let's clean up the document
      article.doc =  docCleaner.clean(article)

      extractor.calculateBestNodeBasedOnClustering(article) match {
        case Some(node: Element) => {
          article.topNode = node

          val imageExtractor = getImageExtractor(article)
          imageExtractor.RemoveBadImages(article)

          article.cleanedArticleSimpleHTMLDoc =  outputFormatter.getFormattedHTML(article)
          article.cleanedArticleSimpleHTML = article.cleanedArticleSimpleHTMLDoc.get.html

        }
        case _ => trace("NO ARTICLE FOUND")
      }
      releaseResources(article)
      val validArticle = isValidArticle(article)
      article
    }
    val validArticle = isValidArticle(article)
    article
  }

  def extractArticle(crawlCandidate: CrawlCandidate, outputFormat: String = "ARTICLE", verbose: Boolean = false): HtmlExtractResponse = {

    val OUTPUT_FORMATS = Array("HTML", "HTML_STYLE", "ARTICLE")

    val OutputFormat = if (OUTPUT_FORMATS.contains(outputFormat)) outputFormat else "HTML"

    val article = new Article()
    var article_found = true

    try{
      for {
        parseCandidate <- URLHelper.getCleanedUrl(crawlCandidate.url)
        rawHtml <- getHTML(crawlCandidate, parseCandidate)
        doc <- getDocument(rawHtml)
      }
      {
        trace("Crawling url: " + parseCandidate.url)

        val extractor = getExtractor
        val docCleaner = getDocCleaner
        val outputFormatter = getOutputFormatter

        article.finalUrl = parseCandidate.url.toString
        article.domain = parseCandidate.url.getHost
        article.linkhash = parseCandidate.linkhash
        article.rawHtml = rawHtml
        article.doc = doc
        article.rawDoc = doc.clone()
        article.outputFormat = OutputFormat

        article.title = extractor.getTitle(article)
        article.publishDate = config.publishDateExtractor.extract(doc)
        article.additionalData = config.getAdditionalDataExtractor.extract(doc)
        article.metaDescription = extractor.getMetaDescription(article)
        article.metaKeywords = extractor.getMetaKeywords(article)
        article.canonicalLink = extractor.getCanonicalLink(article)
        article.tags = extractor.extractTags(article)
        // before we do any calcs on the body itself let's clean up the document
        article.doc =  docCleaner.clean(article)


        extractor.calculateBestNodeBasedOnClustering(article) match {
          case Some(node: Element) => {
            article.topNode = node

            val imageExtractor = getImageExtractor(article)
            imageExtractor.RemoveBadImages(article)

            article.cleanedArticleSimpleHTMLDoc =  outputFormatter.getFormattedHTML(article)
            article.cleanedArticleSimpleHTML = article.cleanedArticleSimpleHTMLDoc.get.html

          }
          case _ => {println("NO ARTICLE FOUND");article_found=false }
        }
        releaseResources(article)
      }
      if (verbose){
        println("Article HTML:")
        println(article.cleanedArticleSimpleHTML)
      }

      val validArticle = isValidArticle(article)

      if (article_found && validArticle){
        HtmlExtractResponse(html=article.cleanedArticleSimpleHTML, status = HTMLExtractStatus.OK)
      }
      else {
        val msg = if (!validArticle) {
          s"Article is not Valid"
         } else {s"Article not Found"}
        println(msg)
        HtmlExtractResponse(html=article.cleanedArticleSimpleHTML, status = HTMLExtractStatus.FAILED, msg=msg)
      }
    } catch{
      case e => {
        val msg = s"Error Processing The Article: ${e.getMessage}"
        println(msg)
        HtmlExtractResponse(html=article.cleanedArticleSimpleHTML, status = HTMLExtractStatus.FAILED , msg=msg)
      }
    }
  }

  def isValidArticle(article: Article): Boolean ={

    if (article.cleanedArticleSimpleHTMLDoc.isDefined) {

      val paragraph_inner_valid_tags =  Array("img", "iframe", "video", "ul", "table")
      val mim_paragraph_words = 4
      val min_paragraphs = 1

      val n_paragraphs = article.cleanedArticleSimpleHTMLDoc.get.select("p").asScala.filter(
        p => p.text().length() > mim_paragraph_words || paragraph_inner_valid_tags.map(tag => p.select(tag).size() > 0).reduce((a,b) => a || b)
      ).length


       if  (n_paragraphs >= min_paragraphs) {
         true
       }  else {
         println(s"n_paragraphs = $n_paragraphs")
         false
       }
    }
    else{
      println("cleanedArticleSimpleHTMLDoc not Defined")
      false
    }

   }

  def getHTML(crawlCandidate: CrawlCandidate, parsingCandidate: ParsingCandidate): Option[String] = {
    if (crawlCandidate.rawHTML != null) {
      Some(crawlCandidate.rawHTML)
    } else {
      config.getHtmlFetcher.getHtml(config, parsingCandidate.url.toString) match {
        case Some(html) => {
          Some(html)
        }
        case _ => None
      }
    }
  }


  def getImageExtractor(article: Article): ImageExtractor = {
    val httpClient: HttpClient = config.getHtmlFetcher.getHttpClient
    new UpgradedImageIExtractor(httpClient, article, config)
  }

  def getOutputFormatter: OutputFormatter = {
    StandardOutputFormatter
  }

  def getDocCleaner: DocumentCleaner = {
    new StandardDocumentCleaner
  }

  def getDocument(rawlHtml: String): Option[Document] = {

    try {

      Some(Jsoup.parse(rawlHtml))
    } catch {
      case e: Exception => {
        trace("Unable to parse this html properly into JSoup Doc")
        None
      }
    }
  }

  def getExtractor: ContentExtractor = {
    config.contentExtractor
  }

  /**
  * cleans up any temp files we have laying around like temp images
  * removes any image in the temp dir that starts with the linkhash of the url we just parsed
  */
  def releaseResources(article: Article) {
    trace(logPrefix + "STARTING TO RELEASE ALL RESOURCES")

    val dir: File = new File(config.localStoragePath)

    dir.list.foreach(filename => {
      if (filename.startsWith(article.linkhash)) {
        val f: File = new File(dir.getAbsolutePath + "/" + filename)
        if (!f.delete) {
          warn("Unable to remove temp file: " + filename)
        }
      }
    })
  }

}

object Crawler extends Logging {
  val logPrefix = "crawler: "
}