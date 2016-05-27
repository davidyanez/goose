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

package com.gravity.goose.outputformatters

import scala.util.control._

import com.gravity.goose.Article
import org.jsoup.nodes._
import org.apache.commons.lang.StringEscapeUtils
import org.jsoup.select.{TagsEvaluator, Collector, Elements}
import com.gravity.goose.text.StopWords
import scala.collection.JavaConversions._
import org.slf4j.Logger

import scala.collection.immutable.List

/**
* Created by Jim Plush
* User: jim
* Date: 8/17/11
*/

trait OutputFormatter {
  val logPrefix = "outformat: "

  // used to remove tags within tags
  val tagReplace = "<[^>]+>".r

  def logger: Logger

  private def selectElements(query: String, topNode: Element): Elements = topNode match {
    case null => new Elements(List.empty[Element])
    case n => n.select(query)
  }
  
  /**
  * Depricated use {@link #getFormattedText(Element)}
    *
    * @param topNode the top most node to format
  * @return the prepared Element
  */
  @Deprecated def getFormattedElement(topNode: Element): Element = {
    removeNodesWithNegativeScores(topNode)
    convertLinksToText(topNode)
    replaceTagsWithText(topNode)
    removeParagraphsWithFewWords(topNode)
    topNode
  }

  /**
  * Removes all unnecessarry elements and formats the selected text nodes
    *
    * @param topNode the top most node to format
  * @return a formatted string with all HTML removed
  */
  def getFormattedText(topNode: Element): String = {
    removeNodesWithNegativeScores(topNode)
    convertLinksToText(topNode)
    replaceTagsWithText(topNode)
    removeParagraphsWithFewWords(topNode)
    convertToText(topNode)
  }

  /**
  * Removes all unnecessarry elements and formats the selected text nodes ...
    *
    * @param topNode the top most node to format
  * @return a formatted string with all HTML removed
  */
  def getFormattedHTML(article: Article): Option[Document] = {

    val topNode =  article.topNode
    val title = article.title

    removeNodesWithNegativeScores(topNode)
    cleanLinks(topNode)
    cleanHeaders(topNode)
    cleanParagraphs(topNode)
    removeParagraphsWithFewWords(topNode)
    val doc = getSimpleHTMLDoc(topNode, article)
    if (doc.isDefined)
      removeParagraphsWithFewWords(doc.get.body())
    doc
  }

  /**
  * Depricated use {@link #getFormattedText(Element)}
  * takes an element and turns the P tags into \n\n
  *
  * @return
  */
  def convertToText(topNode: Element): String = topNode match {
      case null => ""
      case node => {
        (node.children().map((e: Element) => {
          StringEscapeUtils.unescapeHtml(e.text).trim
        })).toList.mkString("\n\n")
      }

    }



  def getSimpleHTMLDoc(topNode: Element, article: Article): Option[Document] = topNode match {

      case null => None

      case node => {

        val doc = new Document("/")
        val root = doc.appendElement("html")
        val head = root.appendElement("head")
        val body = root.appendElement("body")

        val head_meta_charset = if (article.charSet != null || article.charSet.length > 0) s"<meta charset='${article.charSet}'>" else s"<meta charset='UTF-8'>"
        val head_meta_description = s"<meta name='keywords' content='${article.metaKeywords}'>"  + s"<meta name='description' content='${article.metaDescription}'>"
        val head_meta_style =
          """<style>
              h1 {font-size: 2.5em;}
              p {font-size:1.25em; }
            </style>""".stripMargin

        head.append(head_meta_charset)
        head.append(head_meta_description)
        head.append(head_meta_style)

        body.append(s"<h1>${article.title}</h1>")

        val domain = article.domain
        val title = article.title

        val SKIP_ATTRIBUTES: List[String] = List("style", "class", "alt")
        val HEADERS: List[String] = List("h1","h2", "h3", "h4", "h5", "h6")
        val keep_tags: List[String] = List("hr", "figcaption")
        val FOLLOW_HEADER_TAGS : List[String] = List("p", "img", "iframe", "video", "picture", "figure", "hr")
//        var processed_element = new ListBuffer[Int]

        val body_html = node.getAllElements.map((e: Element) => {

          if (e.tagName() == "p") {
            if (e.text() != title)
              s"<p>${getcleanParagraphHTML(e)}</p>"
            else
              ""
          }
          else if (e.tagName() == "video") {
            s"<p>${e.outerHtml()}</p>"
          }
          else if (e.tagName().contains(List("ol", "ul"))) {
            s"<p>${e.outerHtml()}</p>"
          }
          else if (keep_tags.contains(e.tagName())) {
            s"<p>${e.outerHtml()}</p>"
          }
          else if (e.tagName() == "img") {

            if (e.hasAttr("src") && (e.attr("src").startsWith("http"))) {}
            else if (e.hasAttr("src") && e.attr("src").startsWith("//")) {
              e.attr("src", "http:" + e.attr("src"))
            }
            else {
              e.attr("src", "http://" + domain + e.attr("src"))
            }
            if (e.hasAttr("srcset") && e.attr("srcset").length > 0) {
              var img_sources = e.attr("srcset").split(",").map((url: String) => url.trim())
              img_sources = img_sources.map(src => if (src.startsWith("http")) src
              else if (src.startsWith("//")) "http:" + src else "http://" + domain + src)
              val srcset = String.join(", ", img_sources.toList)
              e.attr("srcset", srcset)
            }
            var img_attributes = e.attributes().filter((a: Attribute) => !SKIP_ATTRIBUTES.contains(a.getKey())).
              map((a: Attribute) => a.getKey + "=\"" + a.getValue + "\"").mkString(" ")

            s"<p><img $img_attributes ></p>"

          } else if (e.tagName() == "iframe"
            && (e.attr("src").contains("//www.youtube.com/embed/")
            || e.attr("src").contains("//player.vimeo.com/video/"))
            ||  e.hasAttr("allowfullscreen ")
          ) {

            var iframe_attributes = e.attributes().filter((a: Attribute) => a.getKey() != "style").
              map((a: Attribute) => if (a.getKey == "src" && a.getValue.startsWith("//")) a.getKey + "=\"http:" + a.getValue + "\""
              else a.getKey + "=\"" + a.getValue + "\"").mkString(" ")

            val wrapper_div_style = "position:relative;padding-bottom: 56.25%;padding-top: 25px;height:0;"
            val iframe_style = "position:absolute;top=0;left:0;width:100%;height:95%;"
            "<p><div style=\"" + wrapper_div_style + "\">" + "<iframe " + iframe_attributes + " style=\"" + iframe_style + "\"></iframe></div></p>"
          }
          else if (HEADERS.contains(e.tagName())) {
            // to avoid having two h1 headers in the top , title and first h1 tag.
            val tag_name =  if (e.tagName() == "h1") "h2" else e.tagName()
            if (e.text() != title)

              s"<p><${tag_name}>${e.html}</${tag_name}></p>"
            //              s"<${e.tagName}>${trim_element_text(e)}</${e.tagName}>"
            else {""}
            // && FOLLOW_HEADER_TAGS.contains(e.nextElementSibling().tagName())
          }
          else {""}

        }).toList.mkString("")

        body.append(body_html)
        Some(doc)

      }
    }

  /**
  * cleans up and converts any nodes that should be considered text into text
  */
  private def convertLinksToText(topNode: Element) {
    if (topNode != null) {
      logger.trace(logPrefix + "Turning links to text")
      val baseUri = topNode.baseUri()

      val links = topNode.getElementsByTag("a")
      for (item <- links) {
        if (item.getElementsByTag("img").isEmpty) {
          val tn = new TextNode(item.text, baseUri)
          item.replaceWith(tn)
        }
      }
    }
  }

  /**
    * cleans up links, the links outside p are removed
    *
    */
  private def cleanLinks(topNode: Element) {
    if (topNode != null) {
      logger.trace(logPrefix + "Turning links to text")
      val baseUri = topNode.baseUri()

      val links = topNode.getElementsByTag("a")
      for (item <- links) {
        if (item.getElementsByTag("img").isEmpty) {
          if (item.parents().map(e => e.tagName()).filter(tag => tag == "p").length == 0){
            item.remove()
          }
        }
      }
    }
  }

  /**
    * cleans up headers, keep only the header followed by an element that contains only accepted tags
    *
    */
  private def cleanHeaders(topNode: Element): Unit ={

    val ACCEPTED_TAGS = TagsEvaluator("p","img","video","figure","picture", "ol", "iframe", "div")
    val HEADER_TAGS = TagsEvaluator("h1","h2","h3","h4","h5","h6")

    val headers = Collector.collect(HEADER_TAGS, topNode)
    for (header <- headers) {
      //      check if next sibling contains ACCEPTED_TAGS
      val sibling_good_elements = Collector.collect(ACCEPTED_TAGS, header.nextElementSibling())
      if (sibling_good_elements.length == 0) {
        header.remove()
      }
    }
  }

  /**
    * cleans up Paragraphs
    *
    */
    private def cleanParagraphs(topNode: Element): Unit = {

      val max_link_words_percent = 0.7
      val loop = new Breaks;

      for (p <- topNode.select("p")){
        loop.breakable {
          for (tag <- p.children()) {

            if (tag.tagName() == "a") {
              val text_world_count = StopWords.getStopWordCount(p.text()).getWordCount
              val link_world_count = StopWords.getStopWordCount(tag.text()).getWordCount
              if (link_world_count.toFloat > max_link_words_percent * text_world_count) {
                // this is a link paragraph and should be removed
                try {
                  p.remove()
                  loop.break()
                } catch {
                  case _ => {}
                }

              }
            }
          }
        }
      }
    }

    private def getcleanParagraphHTML(paragraph: Element): String = {

      val ACCEPTED_TAGS  = List("b", "strong", "em", "a", "hr", "br", "span")
      val p = paragraph.clone()

      for (tag <- p.children()){
        if (!ACCEPTED_TAGS.contains(tag.tagName())  ){
          tag.remove()
        }else if (tag.tagName() == "a" && tag.select("img").length > 0){
          tag.remove()
        }
      }
      p.html
    }

  /**
  * if there are elements inside our top node that have a negative gravity score, let's
  * give em the boot
  */
  private def removeNodesWithNegativeScores(topNode: Element) {
    def tryInt(text: String): Int = try {
      Integer.parseInt(text)
    } catch {
      case _: Exception => 0
    }

    val gravityItems = selectElements("*[gravityScore]", topNode)
    for (item <- gravityItems) {
      val score = tryInt(item.attr("gravityScore"))
      if (score < 1) {
        item.remove()
      }
    }
  }

  /**
  * replace common tags with just text so we don't have any crazy formatting issues
  * so replace <br>, <i>, <strong>, etc.... with whatever text is inside them
  */
  private def replaceTagsWithText(topNode: Element) {
    if (topNode != null) {
      val baseUri = topNode.baseUri()
      val bolds = topNode.getElementsByTag("b")
      for (item <- bolds) {
        val tn = new TextNode(getTagCleanedText(item), baseUri)
        item.replaceWith(tn)
      }

      val strongs = topNode.getElementsByTag("strong")
      for (item <- strongs) {
        val tn = new TextNode(getTagCleanedText(item), baseUri)
        item.replaceWith(tn)
      }

      val italics = topNode.getElementsByTag("i")
      for (item <- italics) {
        val tn = new TextNode(getTagCleanedText(item), baseUri)
        item.replaceWith(tn)

      }
    }
  }

  private def getTagCleanedText(item: Node): String = {
    val sb = new StringBuilder()

    item.childNodes().foreach {
      case childText: TextNode => {
        sb.append(childText.getWholeText)
      }
      case childElement: Element => {
        sb.append(childElement.outerHtml())
      }
      case _ =>
    }

    val text = tagReplace replaceAllIn(sb.toString(), "")
    text
  }

  /**
  * remove paragraphs that have less than x number of words, would indicate that it's some sort of link
  */
  private def removeParagraphsWithFewWords(topNode: Element) {
    if (topNode != null) {
      if (logger.isDebugEnabled) {
        logger.debug("removeParagraphsWithFewWords starting...")
      }
      val IGNORE_TAGS = Array("img", "iframe", "picture", "video","figure","hr", "h1", "h2", "h3", "h4", "br", "b", "strong", "a", "li")
      val INNER_SAFE_TAGS = Array("img", "iframe", "picture", "video", "figure")  // do not delete paragraphs containing this tags

      val allNodes = topNode.getAllElements

      for (el <- allNodes) {
        try {

          val stopWords = StopWords.getStopWordCount(el.text)
          if (!IGNORE_TAGS.contains(el.tagName())  && INNER_SAFE_TAGS.forall(tag => el.getElementsByTag(tag).isEmpty) && stopWords.getStopWordCount <= 3 && el.getElementsByTag("object").size == 0 && el.getElementsByTag("embed").size == 0) {
            logger.debug("removeParagraphsWithFewWords - swcnt: %d removing text: %s".format(stopWords.getStopWordCount, el.text()))
            el.remove()
          }
        }
        catch {
          case e: IllegalArgumentException => {
            logger.error(e.getMessage)
          }
        }
      }

      Option(topNode.getElementsByTag("p").first()).foreach {
        case firstModdedNode: Element => {
          // check for open parens as the first paragraph, e.g. businessweek4.txt (IT)
          val trimmed = firstModdedNode.text().trim()
          if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            logger.trace("Removing parenthesis paragraph that is first paragraph")
            firstModdedNode.remove()
          }
        }
      }
    }
  }
}