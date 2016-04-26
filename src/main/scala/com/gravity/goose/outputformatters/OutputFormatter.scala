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

import org.jsoup.nodes._
import org.apache.commons.lang.StringEscapeUtils
import org.jsoup.select.Elements
import com.gravity.goose.text.StopWords
import scala.collection.JavaConversions._
import org.slf4j.Logger

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
  def getFormattedHTML(topNode: Element, title: String): String = {

    removeNodesWithNegativeScores(topNode)
    convertLinksToText(topNode)
    replaceTagsWithText(topNode)
    removeParagraphsWithFewWords(topNode)
    val header_meta = "<meta http-equiv='Content-Type' content='text/html;charset=utf-8'/>"
    val style = "<style>" +
      "h1 {font-size: 2.5em;}" +
      "p {font-size:1.25em;}" +
      "</style>"

    s"<html><head>$header_meta$style</head><body><h1>$title</h1>${convertToSimpleHTML(topNode)}</ body></ html>"
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

  def convertToSimpleHTML(topNode: Element): String = topNode match {

      case null => ""

      case node => {
//        (node.children().map((e: Element) => {
        node.getAllElements.map((e: Element) => {
          if (e.tagName() == "p") {
            s"<p>${StringEscapeUtils.unescapeHtml(e.text).trim}</p>"
          }
          else if (e.tagName() == "img") {
            if (e.hasAttr("srcset"))
            "<img srcset=\"" + e.attr("srcset")  + "\">"
            else if (e.hasAttr("src"))
              "<img src=\"" + e.attr("src")  + "\">"
            else
              ""
          }else if (e.tagName() == "iframe" &&
            (e.attr("src").startsWith("https://www.youtube.com/embed/") ||
              e.attr("src").startsWith("https://player.vimeo.com/video/")
              )
          ) {
            //e.outerHtml()
            var iframe_attributes =  e.attributes().filter((a: Attribute) => a.getKey() != "style").
              map((a: Attribute) => a.getKey.toString + "=\"" + a.getValue.toString + "\"").mkString(" ")

            iframe_attributes = iframe_attributes + "width=\"" + "100%" + "height=\"" + "auto" + "\""
            val wrapper_div_style = "position:relative;padding-bottom: 56.25%;padding-top: 25px;height:0;"
            val iframe_style= "position:absolute;top=0;left:0;width:80%;height:80%;"
            "<div style=\""+wrapper_div_style+ "\">" + "<iframe " + iframe_attributes + " style=\"" + iframe_style + "\"></iframe></div>"

//            "<div style=\""> <iframe ${iframe_attributes}></iframe></div>"
          }
          else {
            ""
          }



        }).toList.mkString("")
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
      val IGNORE_TAGS = Array("img", "iframe", "picture")

      val allNodes = topNode.getAllElements

      for (el <- allNodes) {
        try {
          val stopWords = StopWords.getStopWordCount(el.text)
          if ( (!IGNORE_TAGS.contains(el.tagName()) && IGNORE_TAGS.forall(tag => el.getElementsByTag(tag).isEmpty)) && stopWords.getStopWordCount < 3 && el.getElementsByTag("object").size == 0 && el.getElementsByTag("embed").size == 0) {
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