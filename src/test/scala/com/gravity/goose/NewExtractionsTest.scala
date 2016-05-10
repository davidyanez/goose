package com.gravity.goose

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date

import com.gravity.goose.extractors.PublishDateExtractor
import com.gravity.goose.utils.FileHelper
import org.jsoup.nodes.Element
import org.jsoup.select.Selector
import org.junit.Assert._
import org.junit.Test

/**
 * Created by Jim Plush
 * User: jim
 * Date: 8/19/11
 */

class NewExtractionsTest {

  def getHtml(filename: String): String = {
    FileHelper.loadResourceFile(TestUtils.staticHtmlDir + filename, Goose.getClass)
  }

  @Test
  def jlo_home_test() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.dailymail.co.uk/tvshowbiz/article-3546267/Jennifer-Lopez-reduces-price-spectacular-Hidden-Hills-mansion-12-5-million.html"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)
    val out_html = new PrintWriter(new File("jlo_home.html" ))
    out_html.write(article.cleanedArticleSimpleHTML)
    out_html.close()

  }

  @Test
  def bloomberg_tesla_test() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url =  "http://www.bloomberg.com/news/articles/2016-04-21/tesla-changed-cars-forever-now-it-must-deliver"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)
    val out_html = new PrintWriter(new File("bloomberg_tesla.html" ))
    out_html.write(article.cleanedArticleSimpleHTML)
    out_html.close()

  }

  @Test
   def theverge_nasa_test() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =   "http://www.theverge.com/2016/4/20/11466636/nasa-aerojet-rocketdyne-solar-electric-propulsion-contract"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("theverge_nasa.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def ie_youtube_test() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =   "http://interestingengineering.com/hacker-installed-windows-95-smartwatch/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("ie_youtube_win95.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def wired_nexlist_test() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =   "http://www.wired.com/2016/04/wired-nextlist-2016/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("wired_nextlist.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

   @Test
   def newscientist_spacex_mars() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =  "https://www.newscientist.com/article/2086270-spacex-claims-it-can-get-to-mars-by-2018-what-are-its-chances/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("newscientist_spacex_mars.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }


  @Test
   def outbrain_webcontent_tools() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG
        // Fails!  no article found ?>  when page is loaded it only shows  loading_page
        val url =  "http://www.outbrain.com/blog/2016/04/10-tools-every-content-marketer-needs-to-stay-ahead-in-2016.html"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("outbrain_webcontent_tools.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def rt_spacex() {

        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =  "https://www.rt.com/usa/341166-spacex-founders-micro-satellite-rockets/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("rt_spacex.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

   @Test
   def wsj_5gadgets() {

        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =  "http://www.wsj.com/articles/5-gadgets-tough-enough-for-campers-1461862177?mod=ST1"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("wsj_5gadgets.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

   @Test
     def theage_steve_wozniak_ai() {

          implicit val config = TestUtils.NO_IMAGE_CONFIG

          val url =  "http://www.theage.com.au/technology/innovation/apple-cofounder-steve-wozniak-artificial-intelligence-revolution-is-near-20160428-gogy87.html"
          val article = TestUtils.getArticle(url = url)
          println(article.cleanedArticleSimpleHTML)
          val out_html = new PrintWriter(new File("theage_steve_wozniak_ai.html" ))
          out_html.write(article.cleanedArticleSimpleHTML)
          out_html.close()
        }

  @Test
  def theinquirer_ai_revolution() {

       implicit val config = TestUtils.NO_IMAGE_CONFIG

       val url =  "http://www.theinquirer.net/inquirer/news/2408538/artificial-intelligence-will-create-the-next-industrial-revolution-experts-claim"
       val article = TestUtils.getArticle(url = url)
       println(article.cleanedArticleSimpleHTML)
       val out_html = new PrintWriter(new File("theinquirer_ai_revolution.html" ))
       out_html.write(article.cleanedArticleSimpleHTML)
       out_html.close()
     }

  @Test
  def newyork_to_london_highway() {
        // failed:
       implicit val config = TestUtils.NO_IMAGE_CONFIG

       val url =  "http://www.thinkinghumanity.com/2016/04/russia-proposes-superhighway-linking-new-york-and-london.html"
       val article = TestUtils.getArticle(url = url)
       println(article.cleanedArticleSimpleHTML)
       val out_html = new PrintWriter(new File("newyork_to_london_highway.html" ))
       out_html.write(article.cleanedArticleSimpleHTML)
       out_html.close()
       }

  @Test
    def economist_neurological_night_watch() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url =  "http://www.economist.com/news/science-and-technology/21697213-why-familiar-bed-provides-good-nights-sleep-neurological-night-watch"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("economist_neurological_night_watch.html" ))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
     }

  @Test
    def engadget_dyson_dryer() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url =  "http://www.engadget.com/2016/04/27/dyson-made-a-hair-dryer/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("engadget_dyson_dryer.html" ))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
     }

  @Test
  def vice_mini_europe() {
    // failed:  exception on downloading an image, getting a not authorized message
   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url =  "http://www.vice.com/read/a-model-continent-eu-postcards"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)
   val out_html = new PrintWriter(new File("vice_mini_europe.html" ))
   out_html.write(article.cleanedArticleSimpleHTML)
   out_html.close()
   }

  @Test
    def vice_robots_ai() {
      // failed:  exception on downloading an image, getting a not authorized message
     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url =  "http://motherboard.vice.com/read/computers-might-just-see-like-humans-after-all-vision-deep-learning-neural-networks"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("vice_robots_ai.html" ))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
     }

  @Test
  def vice_f35_bugs() {
    // failed:  exception on downloading an image, getting a not authorized message
    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://motherboard.vice.com/read/the-f-35s-software-is-so-buggy-it-might-ground-the-whole-fleet"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)
    val out_html = new PrintWriter(new File("vice_f35_bugs.html"))
    out_html.write(article.cleanedArticleSimpleHTML)
    out_html.close()
  }

  @Test
  def hbr_social_media() {
      // failed:  exception on downloading an image, getting a not authorized message
      implicit val config = TestUtils.NO_IMAGE_CONFIG

      val url = "https://hbr.org/2016/04/social-media-is-too-important-to-be-left-to-the-marketing-department"
      val article = TestUtils.getArticle(url = url)
      println(article.cleanedArticleSimpleHTML)
      val out_html = new PrintWriter(new File("hbr_social_media.html"))
      out_html.write(article.cleanedArticleSimpleHTML)
      out_html.close()
    }

  @Test
     def dailyedge_pizza_box() {
       // failed:  exception on downloading an image, getting a not authorized message
       implicit val config = TestUtils.NO_IMAGE_CONFIG

       val url = "http://www.dailyedge.ie/pizza-pizza-box-amazing-2742051-Apr2016/"
       val article = TestUtils.getArticle(url = url)
       println(article.cleanedArticleSimpleHTML)
       val out_html = new PrintWriter(new File("dailyedge_pizza_box.html"))
       out_html.write(article.cleanedArticleSimpleHTML)
       out_html.close()
     }

  @Test
      def fool_bombardier_order() {

        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url = "http://www.fool.ca/2016/04/28/bombardier-inc-gets-another-order/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("fool_bombardier_order.html"))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def stradigi() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.stradigi.ca"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("stradigi.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }


  @Test
   def business_insider() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.businessinsider.com"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("businessinsider.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }

  @Test
   def newscientist_google_ai_nhs() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "https://www.newscientist.com/article/2086454-revealed-google-ai-has-access-to-huge-haul-of-nhs-patient-data/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("newscientist_google_ai_nhs.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }

  @Test
   def motortrend_tesla3() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.motortrend.com/news/tesla-model-3-photos-analysis/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("motortrend_tesla3.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }


  // http://www.motortrend.com/news/tesla-model-3-photos-analysis/


  // https://www.newscientist.com/article/2086454-revealed-google-ai-has-access-to-huge-haul-of-nhs-patient-data/


  // http://motherboard-images.vice.com/content-images/contentimage/no-id/1461696042952895.jpeg
  // http://www.engadget.com/2016/04/27/dyson-made-a-hair-dryer/
  // https://hbr.org/2016/04/social-media-is-too-important-to-be-left-to-the-marketing-department
  // http://www.dailyedge.ie/pizza-pizza-box-amazing-2742051-Apr2016/
  // http://www.fool.ca/2016/04/28/bombardier-inc-gets-another-order/

}