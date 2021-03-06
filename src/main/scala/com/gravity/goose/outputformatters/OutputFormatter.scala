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
import org.jsoup.parser.Tag
import com.gravity.goose.text.StopWords
import scala.collection.JavaConversions._
import org.slf4j.Logger

import scala.collection.immutable.List
import java.util.Calendar;
     import java.text.SimpleDateFormat;
import scala.collection.mutable.ListBuffer
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
    removeElementsWithFewWords(topNode)
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
    cleanLinks(topNode)
    cleanParagraphs(topNode)
    convertLinksToText(topNode)
    replaceTagsWithText(topNode)
    removeDuplicatedImages(topNode)
    removeDuplicatedtext(topNode)

    removeElementsWithFewWords(topNode)

    convertToText(topNode)

  }

  def removeDuplicatedImages(topNode: Element): Unit =  {

      val image_nodes = topNode.select("img")

      for (image_node <- image_nodes) {
        if (image_node.hasAttr("src")){
          val src = image_node.attr("src")
          val duplicated_tags = topNode.getElementsByAttributeValue("src", src)

          if (duplicated_tags.length > 1){
            for ( duplicated_tag <- duplicated_tags.drop(1)){
              duplicated_tag.remove()
            }
          }
        }

      }
    }

  def removeDuplicatedtext(topNode: Element): Unit =  {

    val all_nodes = topNode.getAllElements
    val MIN_TEXT_WORDS = 10


    for (node <- all_nodes) {
      val text = node.text()
      if (text.length > 1 && StopWords.getStopWordCount(text).getWordCount >= MIN_TEXT_WORDS){
        val duplicated_tags = topNode.getElementsContainingOwnText(text)
        if (duplicated_tags.length > 1){
          for ( duplicated_tag <- duplicated_tags.drop(1)){
            duplicated_tag.remove()
          }
        }
      }
    }
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
    cleanParagraphs(topNode)
    removeElementsWithFewWords(topNode)
    removeDuplicatedImages(topNode)
    removeDuplicatedtext(topNode)

    val doc = getSimpleHTMLDoc(topNode, article)
    if (doc.isDefined) {
      val el = doc.get.body()
      removeElementsWithFewWords(el)
      cleanHeaders(el)
      keepFirstImagesOnly(el)
    }
    if (article.outputFormat == "ARTICLE" ){
      var document =  new Document("/")
      document.appendChild(doc.get.body())
      Some(document)
    }else{
      doc
    }

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






   def article_html(article: Article): String ={


     val keep_tags: List[String] = List("hr", "figcaption", "br", "blockquote")
     val SKIP_ATTRIBUTES: List[String] = List("style", "class", "alt", "width", "height", "max-width")
     val HEADERS: List[String] = List("h1","h2", "h3", "h4", "h5", "h6")
     var header_count = 0

     val topNode = article.topNode
     val processed_elements = new ListBuffer[Element]

     def get_article_div_html(node: Element): String =  {

            node.getAllElements.map((e: Element) => {

              if (!processed_elements.find(el => el == e).isDefined)
              {
                processed_elements += e

                if (e.tagName() == "p" ) {
                  if (e.text() != article.title)
                    s"<p>${getcleanParagraphHTML(e)}</p>"
                  else
                    ""
                }else if (e.tagName() == "blockquote"){
                  e.tagName("div")

                  s"<blockquote>${get_article_div_html(e)}</blockquote>"
                }
                else if (Array("br", "hr").contains(e.tagName())) {
                  if (e.previousElementSibling() != null &&  e.tagName() != e.previousElementSibling().tagName()){
                    // avoid to have this tag repeated consecutively
                    s"${e.outerHtml()}"
                  } else{
                    ""
                  }
                }
                else if (e.tagName() == "video" || e.tagName() == "object") {
                  s"<div class='video-wrap'>${e.outerHtml()}</div>"
                }
                else if (e.tagName().contains(List("ol", "ul"))) {
                  s"<div class='list'>${e.outerHtml()}</div>"
                }
                else if (keep_tags.contains(e.tagName())) {
                  s"<${e.tagName()}>"
                }
                else if (e.tagName() == "img" ||  (e.tagName() == "source" && e.parent().tagName() == "picture")) {

                  if (e.hasAttr("src") && (e.attr("src").startsWith("http"))) {}
                  else if (e.hasAttr("src") && e.attr("src").startsWith("//")) {
                    e.attr("src", "http:" + e.attr("src"))
                  }
                  else {
                    val src =
                    e.attr("src", "http://" + (article.domain + '/' + e.attr("src")).replace("//", "/") )
                  }
                  if (e.hasAttr("srcset") && e.attr("srcset").length > 0) {
                    e.removeAttr("srcset")
                  }
                  var img_attributes = e.attributes().filter((a: Attribute) => !SKIP_ATTRIBUTES.contains(a.getKey())).
                    map((a: Attribute) => a.getKey + "=\"" + a.getValue + "\"").mkString(" ")

                  s"<div class='image-wrap'><img class='image' $img_attributes></div>"

                } else if (e.tagName() == "iframe"
                  && (e.attr("src").contains("//www.youtube.com/embed/")
                  || e.attr("src").contains("//player.vimeo.com/video/"))
                  ||  e.hasAttr("allowfullscreen ")
                ) {

                  var iframe_attributes = e.attributes().filter((a: Attribute) => a.getKey() != "style").
                    map((a: Attribute) => if (a.getKey == "src" && a.getValue.startsWith("//")) a.getKey + "=\"https:" + a.getValue + "\""
                    else a.getKey + "=\"" + a.getValue.replace("http:", "https:") + "\"").mkString(" ")

                  val wrapper_div_style = "position:relative;padding-bottom: 56.25%;padding-top: 25px;height:0;"
                  val iframe_style = "position:absolute;top=0;left:0;width:100%;height:95%;"
                  "<div class='video-iframe-wrap'>" + "<iframe class='video-iframe'" + iframe_attributes + "></iframe></div>"
                }
                else if (HEADERS.contains(e.tagName())) {
                  // to avoid having two h1 headers in the top , title and first h1 tag.
                  val tag_name =  e.tagName()
                  header_count+=1
                  val similarity_th = 0.60

                  if (StringSimilarity.similarity(e.text(), StringEscapeUtils.unescapeHtml(article.title).trim) < similarity_th )
                    s"<${tag_name}>${e.text}</${tag_name}>"
                  else {""}
                }
                else {""}
              } else {""}


          }).toList.mkString("")
          }

     val article_div_html =  get_article_div_html(topNode)

     val today = Calendar.getInstance().getTime()
     val formatter = new SimpleDateFormat("MMMMMMMMMM dd, yyyy")
     val today_str =  formatter.format(today)


     val article_footer =
       "<hr><div class=\"reflink\">"+
         "<span> Read on the original source </span>"+
         s"<a class='link' href='${article.finalUrl}' target='_blank'>${article.domain}</a>" +
      s"</div>"+
        s"<div class='scraped_date'>Pulled on $today_str </div>"+
      ""

     article_div_html + article_footer
   }

  def getSimpleHTMLDoc(topNode: Element, article: Article): Option[Document] = topNode match {

      case null => None

      case node => {

        def create_article_div(): Element = {

          val article_div = new Element(Tag.valueOf("div"), "/")
          article_div.attr("id", "web-article")
          article_div.append(s"<h1>${article.title}</h1>")
          val article_div_html = article_html(article)
          article_div.append(article_div_html)
          article_div
        }

        val doc = new Document("/")

        val FOLLOW_HEADER_TAGS : List[String] = List("p", "img", "iframe", "video", "picture", "figure", "hr")


        val root = doc.appendElement("html")
        val head = root.appendElement("head")
        val body = root.appendElement("body")

        val head_meta_charset = s"<meta charset='UTF-8'>"
        val head_meta_description = s"<meta name='keywords' content='${article.metaKeywords}'>"  + s"<meta name='description' content='${article.metaDescription}'>"
        head.append(head_meta_charset)
        head.append(head_meta_description)

        if (article.outputFormat == "HTML_STYLE"){
          val head_meta_style = """<style>
                          #web-article {}
                          #web-article h1 {font-size: 2.5em; font-color: red;}
                          #web-article h2 {}
                          #web-article h3 {}
                          #web-article h4 {}
                          #web-article h5 {}
                          #web-article h6 {}
                          #web-article p {font-size:1.25em; }

                          #web-article .video-wrap {}
                          #web-article .image-wrap { width: 90%;}
                          #web-article .image { max-width: 100%; height: auto; }
                          #web-article .video-iframe-wrap { position:relative;padding-bottom: 56.25%;padding-top: 25px;height:0; }
                          #web-article .video-iframe { position:absolute;top=0;left:0;width:100%;height:95%; }
                          #web-article .list {}
                          </style>""".stripMargin

          head.append(head_meta_style)
        }
        val article_div =  create_article_div()
        body.appendChild(article_div)


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

    var headers = Collector.collect(HEADER_TAGS, topNode)

    for (header <- headers) {
      if (header.text().length < 1) {
         header.remove()
      }
    }

    headers = Collector.collect(HEADER_TAGS, topNode)

    for (header <- headers.drop(1)) {
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


    private def cleanLink(tag: Element): Unit = {

      if (tag.tagName() == "a" && tag.hasAttr("href")){

              val href = tag.attr("href")
              tag.attributes().map(attr => tag.removeAttr(attr.getKey))
      //        tag.attr("href", href)
      //        tag.attr("target", "_blank")
              val newDoc = new Document("/")
              val newTag = newDoc.createElement("a")
              newTag.text(tag.text())
              newTag.attr("href", href)
              newTag.attr("target", "_blank")

//              for (img <- tag.select("img")) {
//                newTag.appendChild(img.clone())
//              }
              tag.replaceWith(newTag)

            }

    }

    private def getcleanParagraphHTML(paragraph: Element): String = {

      val ACCEPTED_TAGS  = List("b", "strong", "em", "a", "hr", "br", "span")
      val p = paragraph.clone()

      for (tag <- p.children()){
        if (!ACCEPTED_TAGS.contains(tag.tagName())  ){
          tag.remove()
        }
      if (tag.tagName() == "a" && tag.hasAttr("href")){
        cleanLink(tag)
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
    * If there are many consecutives images, only keep the first Image.
    */
  def keepFirstImagesOnly(topNode: Element): Unit ={

      for (image <- topNode.select("div[class=image-wrap]")){
        val prev_img = image.previousElementSibling()
        if (prev_img != null && prev_img.hasAttr("class") && prev_img.attr("class") == "image-wrap") {
          image.remove()
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
  private def removeElementsWithFewWords(topNode: Element, ignore_tags: Array[String]= Array.empty) {
    if (topNode != null) {
      if (logger.isDebugEnabled) {
        logger.debug("removeParagraphsWithFewWords starting...")
      }
      val IGNORE_TAGS = Array("img", "iframe", "picture", "video","figure","hr", "h1", "h2", "h3", "h4", "br", "b", "strong", "a", "li", "object", "span") ++ ignore_tags
      val IGNORE_CLASSES = Array("link", "scraped_date")
      val INNER_SAFE_TAGS = Array("img", "iframe", "picture", "video", "figure", "strong", "h1", "h2", "h3", "h4")  // do not delete paragraphs containing this tags

      val allNodes = topNode.getAllElements

      for (el <- allNodes) {
        try {

          val stopWords = StopWords.getStopWordCount(el.text)
          if (!IGNORE_TAGS.contains(el.tagName()) &&  el.parents().filter(parent => IGNORE_TAGS.contains(el.tagName())).length == 0 &&
            !IGNORE_CLASSES.contains(el.attr("class")) &&
            !IGNORE_TAGS.contains(el.parent().tagName())   && INNER_SAFE_TAGS.forall(tag => el.getElementsByTag(tag).isEmpty)
            && stopWords.getStopWordCount <= 5 && el.getElementsByTag("object").size == 0 && el.getElementsByTag("embed").size == 0) {
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


  object StringSimilarity {

    def similarity(s1: String, s2: String): Double = {
      var longer = s1
      var shorter = s2
      if (s1.length < s2.length) {
        longer = s2
        shorter = s1
      }
      val longerLength = longer.length
      if (longerLength == 0) {
        return 1.0
      }
      (longerLength - editDistance(longer, shorter)) / longerLength.toDouble
    }

    def editDistance(str1: String, str2: String) = {
      val s1 = str1.toLowerCase()
      val s2 = str2.toLowerCase()
      val costs = Array.ofDim[Int](s2.length + 1)
      var i = 0
      while (i <= s1.length) {
        var lastValue = i
        var j = 0
        while (j <= s2.length) {
          if (i == 0) costs(j) = j else {
            if (j > 0) {
              var newValue = costs(j - 1)
              if (s1.charAt(i - 1) != s2.charAt(j - 1)) newValue = Math.min(Math.min(newValue, lastValue),
                costs(j)) + 1
              costs(j - 1) = lastValue
              lastValue = newValue
            }
          }
          j += 1
        }
        if (i > 0) costs(s2.length) = lastValue
        i += 1
      }
      costs(s2.length)
    }
  }


}