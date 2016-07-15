package com.gravity.goose

import java.io._
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

  val output_folder = "output"

  def getHtml(filename: String): String = {
    FileHelper.loadResourceFile(TestUtils.staticHtmlDir + filename, Goose.getClass)
  }

  @Test
  def jlo_home_test() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.dailymail.co.uk/tvshowbiz/article-3546267/Jennifer-Lopez-reduces-price-spectacular-Hidden-Hills-mansion-12-5-million.html"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)
    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"jlo_home.html" ))
    out_html.write(article.cleanedArticleSimpleHTML)
    out_html.close()

  }

  @Test
  def bloomberg_tesla_test() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url =  "http://www.bloomberg.com/news/articles/2016-04-21/tesla-changed-cars-forever-now-it-must-deliver"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)
    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"bloomberg_tesla.html" ))
    out_html.write(article.cleanedArticleSimpleHTML)
    out_html.close()

  }

  @Test
   def theverge_nasa_test() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =   "http://www.theverge.com/2016/4/20/11466636/nasa-aerojet-rocketdyne-solar-electric-propulsion-contract"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"theverge_nasa.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def ie_youtube_test() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =   "http://interestingengineering.com/hacker-installed-windows-95-smartwatch/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"ie_youtube_win95.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def wired_nexlist_test() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =   "http://www.wired.com/2016/04/wired-nextlist-2016/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"wired_nextlist.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

   @Test
   def newscientist_spacex_mars() {
        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =  "https://www.newscientist.com/article/2086270-spacex-claims-it-can-get-to-mars-by-2018-what-are-its-chances/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"newscientist_spacex_mars.html" ))
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
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"outbrain_webcontent_tools.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

  @Test
   def rt_spacex() {

        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =  "https://www.rt.com/usa/341166-spacex-founders-micro-satellite-rockets/"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"rt_spacex.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

   @Test
   def wsj_5gadgets() {

        implicit val config = TestUtils.NO_IMAGE_CONFIG

        val url =  "http://www.wsj.com/articles/5-gadgets-tough-enough-for-campers-1461862177?mod=ST1"
        val article = TestUtils.getArticle(url = url)
        println(article.cleanedArticleSimpleHTML)
        val out_html = new PrintWriter(new File("./"+output_folder+"/"+"wsj_5gadgets.html" ))
        out_html.write(article.cleanedArticleSimpleHTML)
        out_html.close()
      }

   @Test
     def theage_steve_wozniak_ai() {

          implicit val config = TestUtils.NO_IMAGE_CONFIG

          val url =  "http://www.theage.com.au/technology/innovation/apple-cofounder-steve-wozniak-artificial-intelligence-revolution-is-near-20160428-gogy87.html"
          val article = TestUtils.getArticle(url = url)
          println(article.cleanedArticleSimpleHTML)
          val out_html = new PrintWriter(new File("./"+output_folder+"/"+"theage_steve_wozniak_ai.html" ))
          out_html.write(article.cleanedArticleSimpleHTML)
          out_html.close()
        }

  @Test
  def theinquirer_ai_revolution() {

       implicit val config = TestUtils.NO_IMAGE_CONFIG

       val url =  "http://www.theinquirer.net/inquirer/news/2408538/artificial-intelligence-will-create-the-next-industrial-revolution-experts-claim"
       val article = TestUtils.getArticle(url = url)
       println(article.cleanedArticleSimpleHTML)
       val out_html = new PrintWriter(new File("./"+output_folder+"/"+"theinquirer_ai_revolution.html" ))
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
       val out_html = new PrintWriter(new File("./"+output_folder+"/"+"newyork_to_london_highway.html" ))
       out_html.write(article.cleanedArticleSimpleHTML)
       out_html.close()
       }

  @Test
    def economist_neurological_night_watch() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url =  "http://www.economist.com/news/science-and-technology/21697213-why-familiar-bed-provides-good-nights-sleep-neurological-night-watch"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"economist_neurological_night_watch.html" ))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
     }

  @Test
    def engadget_dyson_dryer() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url =  "http://www.engadget.com/2016/04/27/dyson-made-a-hair-dryer/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"engadget_dyson_dryer.html" ))
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
   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"vice_mini_europe.html" ))
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
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"vice_robots_ai.html" ))
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
    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"vice_f35_bugs.html"))
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
      val out_html = new PrintWriter(new File("./"+output_folder+"/"+"hbr_social_media.html"))
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
       val out_html = new PrintWriter(new File("./"+output_folder+"/"+"dailyedge_pizza_box.html"))
       out_html.write(article.cleanedArticleSimpleHTML)
       out_html.close()
     }

  @Test
  def fool_bombardier_order() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.fool.ca/2016/04/28/bombardier-inc-gets-another-order/"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)
    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"fool_bombardier_order.html"))
    out_html.write(article.cleanedArticleSimpleHTML)
    out_html.close()
  }

  @Test
   def stradigi() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.stradigi.ca"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"stradigi.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }


  @Test
   def business_insider() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.businessinsider.com"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"businessinsider.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }

  @Test
   def newscientist_google_ai_nhs() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "https://www.newscientist.com/article/2086454-revealed-google-ai-has-access-to-huge-haul-of-nhs-patient-data/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"newscientist_google_ai_nhs.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }

  @Test
   def buzzfeed_apple() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.buzzfeed.com/stephaniemlee/uncommon-core#.elgP5PEar"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"buzzfeed_apple.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }

  @Test
   def art_sheep_qivo1545() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://art-sheep.com/10-models-that-are-anything-but-ordinary/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"art_sheep_1.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }

  @Test
   def qivo_1502() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.modernsalon.com/qa-kl-christoffersen-stylist-behind-fluid-hair-painting"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)
     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"modern_salon_qivo1502.html"))
     out_html.write(article.cleanedArticleSimpleHTML)
     out_html.close()
   }


  @Test
  def qivo_1450() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.food.com/recipe/bourbon-chicken-45809"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)
   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"bourbon-chicken_q1450.html"))
   out_html.write(article.cleanedArticleSimpleHTML)
   out_html.close()
  }

  @Test
  def qivo_1554() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://screenrant.com/iron-man-batman-costs-infographics/all/1/"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)
   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"q1564_batman.html"))
   out_html.write(article.cleanedArticleSimpleHTML)
   out_html.close()
  }

  @Test
  def qivo_1638() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.techradar.com/us/news/world-of-tech/future-tech/graphene-breakthrough-is-a-step-closer-to-a-phone-battery-that-lasts-for-a-week-1309253"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)
   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1638.html"))
   out_html.write(article.cleanedArticleSimpleHTML)
   out_html.close()
  }

  @Test
  def qivo_1587() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://ici.radio-canada.ca/emissions/medium_large/2015-2016/chronique.asp?idChronique=389093"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1587.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
    }


  @Test
  def qivo_1561() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://nickoskitchen.com/blogs/10-perfectly-delicious-peanut-butter-cupcakes"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1561.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
    }

  @Test
  def qivo_1553() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "https://www.buzzfeed.com/stephaniemlee/wearing-this-bracelet-could-lower-your-stress-levels?utm_term=.umqVgeN1v#.ce7jr2zRO"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1553.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_1640() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://business.financialpost.com/news/mining/canadian-miner-lucara-uncovers-1111-carat-diamond-believed-to-be-second-largest-ever-found"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1640.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_1727() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.bbc.com/news/in-pictures-34880592"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1727.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
    def qivo_1722() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.bbc.com/news/science-environment-34809804?ocid=socialflow_facebook&ns_mchannel=social&ns_campaign=bbcnews&ns_source=facebook"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)

     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1722.html"))
     out_html.write(article.cleanedArticleSimpleHTML)

     out_html.close()
    }

  @Test
  def qivo_1748() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.oddee.com/item_91848.aspx"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1748.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
    def qivo_4324() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.whowhatwear.com/chanel-fall-winter-2016-runway-collection-karl-lagerfeld"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)

     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_4324.html"))
     out_html.write(article.cleanedArticleSimpleHTML)

     out_html.close()
    }

  @Test
  def qivo_4890() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.sciencemag.org/news/2016/03/rich-sexual-past-between-modern-humans-and-neandertals-revealed?utm_source=sciencemagazine&utm_medium=facebook-text&utm_campaign=neanderpast-3042"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_4890.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
    def qivo_1647() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.bistroisakaya.com/"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)

     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1647.html"))
     out_html.write(article.cleanedArticleSimpleHTML)

     out_html.close()
    }


  @Test
  def qivo_1832() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://m.nzherald.co.nz/lifestyle/news/article.cfm?c_id=6&objectid=11553026"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1832.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_1840() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.glamour.com/fashion/2014/12/new-years-eve-outfit-ideas/1"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1840.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_2190() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://philosophy.hku.hk/think/logic/hardest.php"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_2190.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_2233() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.bbc.com/sport/0/football/35144652"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_2233.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }


  @Test
  def qivo_2675() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://ohsheglows.com/2014/02/05/my-favourite-vegan-chili-with-homemade-sour-cream/"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_2675.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }
  //

  @Test
  def qivo_2676() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://ohsheglows.com/2014/05/20/feel-good-hearty-granola-bars/"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_2676.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }
  //

  @Test
  def qivo_2747() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://news.nationalgeographic.com/2015/11/151113-datapoints-china-one-child-policy"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_2747.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  // http://firstround.com/review/this-is-how-you-design-a-lasting-brand-an-inside-look-at-gustos-reinvention
  @Test
  def qivo_2836() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://firstround.com/review/this-is-how-you-design-a-lasting-brand-an-inside-look-at-gustos-reinvention"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_2836.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }


  @Test
  def qivo_1734() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "https://www.theguardian.com/film/2015/may/03/unfriended-review-mark-kermode-blair-witch-for-the-broadband-generation"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1734.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_1805() {
//    Failing because content is wrapped by a div with id containing the word footer.

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://brightside.me/article/15-pictures-of-the-most-radiant-smiles-youve-ever-seen-38955/"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1805.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def qivo_1817() {
  //    Failing because content is wrapped by a div with id containing the word footer.

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "https://techcrunch.com/2015/11/27/cards-against-humanity-has-made-over-54k-selling-nothing-on-black-friday/#.pldh6l:5K2B"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_1817.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }


  //

  @Test
  def qivo_4127() {
  //    Failing because content is wrapped by a div with id containing the word footer.

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "https://www.nasa.gov/press-release/nasa-astronaut-scott-kelly-returns-safely-to-earth-after-one-year-mission"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_4127.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }
  // http://time.com/4099712/food-refrigeration-guide/?xid=time_socialflow_facebook

  @Test
  def qivo_4764() {
  //    Failing because content is wrapped by a div with id containing the word footer.

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://time.com/4099712/food-refrigeration-guide/?xid=time_socialflow_facebook"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_4764.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

//  @Test
//  def qivo_timeout() {
//
//   implicit val config = TestUtils.NO_IMAGE_CONFIG
//
//   val url = "http://goo.gl/Yzar67"
//   val article = TestUtils.getArticle(url = url)
//   println(article.cleanedArticleSimpleHTML)
//
//   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_timeout.html"))
//   out_html.write(article.cleanedArticleSimpleHTML)
//
//   out_html.close()
//  }


  @Test
    def qivo_7152() {

     implicit val config = TestUtils.NO_IMAGE_CONFIG

     val url = "http://www.cnn.com/videos/world/2016/07/04/justin-trudeau-toronto-pride-orig-emarticke.cnn?sr=fbCNN070416justin-trudeau-toronto-pride-orig-emarticke.cnn0710PMVideoVideo&linkId=26204814"
     val article = TestUtils.getArticle(url = url)
     println(article.cleanedArticleSimpleHTML)

     val out_html = new PrintWriter(new File("./"+output_folder+"/"+"qivo_7152.html"))
     out_html.write(article.cleanedArticleSimpleHTML)

     out_html.close()
    }
  // http://www.bbc.com/news/video_and_audio/features/magazine-36398439/36398439

  @Test
  def bbc_video() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://www.bbc.com/news/uk-england-coventry-warwickshire-36672947"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"bbc_video.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

     // http://thenextweb.com/gadgets/2016/07/01/amazon-echo-can-actually-order-things-amazon-now/

  @Test
  def tnw_echo_order() {

   implicit val config = TestUtils.NO_IMAGE_CONFIG

   val url = "http://thenextweb.com/gadgets/2016/07/01/amazon-echo-can-actually-order-things-amazon-now/"
   val article = TestUtils.getArticle(url = url)
   println(article.cleanedArticleSimpleHTML)

   val out_html = new PrintWriter(new File("./"+output_folder+"/"+"tnw_echo_order.html"))
   out_html.write(article.cleanedArticleSimpleHTML)

   out_html.close()
  }

  @Test
  def nat_geo_jupiter() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://news.nationalgeographic.com/2016/07/nasa-juno-mission-jupiter-arrives-orbit-planets-space/?utm_source=Facebook&utm_medium=Social&utm_content=link_fb20160703news-junorisks&utm_campaign=Content&sf30195484=1"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"nat_geo_jupiter.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }

  //
  @Test
    def psychologytoday() {

      implicit val config = TestUtils.NO_IMAGE_CONFIG

      val url = "https://www.psychologytoday.com/blog/singletons/201509/3-types-parents-who-get-bullied-their-own-children?utm_source=FacebookPost&utm_medium=FBPost&utm_campaign=FBPost"
      val article = TestUtils.getArticle(url = url)
      println(article.cleanedArticleSimpleHTML)

      val out_html = new PrintWriter(new File("./"+output_folder+"/"+"psychologytoday.html"))
      out_html.write(article.cleanedArticleSimpleHTML)

      out_html.close()
    }
  // http://www.futbox.com/en

  @Test
  def footbox() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.futbox.com/en"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"futbox.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }

  //
  @Test
  def ten_reason_rio() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.avenlylanetravel.com/10-reasons-why-rio-de-janeiro-is-the-best-city-to-visit-on-earth/"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"ten_reason_rio.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }

// https://topmusclecars.wordpress.com/

  @Test
  def topmusclecars() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "https://topmusclecars.wordpress.com/"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"topmusclecars.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }

  // http://dogtime.com/dog-breeds/belgian-malinois#/slide/1
  @Test
  def belgian_malinois_qivo7214() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://dogtime.com/dog-breeds/belgian-malinois#/slide/1"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"belgian_malinois_qivo7214.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }

  // http://www.forbes.com/sites/dandiamond/2015/05/11/is-crossfit-good-for-you-what-60-minutes-didnt-say/#1f5ec7e83845

  @Test
  def forbes_crossfit_q7218() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.forbes.com/sites/dandiamond/2015/05/11/is-crossfit-good-for-you-what-60-minutes-didnt-say/#1f5ec7e83845"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"forbes_crossfit_q7218.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }


  @Test
  def motorcycle_types_q7216() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "https://inthenation.nationwide.com/types-of-motorcycles/"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"motorcycle_types_q7216.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }


  @Test
  def amazon_echo_7180() {

    implicit val config = TestUtils.NO_IMAGE_CONFIG

    val url = "http://www.cnbc.com/2016/07/01/amazon-is-making-it-even-easier-to-buy-stuff-on-prime-with-just-your-voice.html"
    val article = TestUtils.getArticle(url = url)
    println(article.cleanedArticleSimpleHTML)

    val out_html = new PrintWriter(new File("./"+output_folder+"/"+"amazon_echo_7180.html"))
    out_html.write(article.cleanedArticleSimpleHTML)

    out_html.close()
  }



  "TODO: NotHtmlException: No HTML returned for url:  http://firstround.com/review/the-30-best-pieces-of-advice-for-entrepreneurs-in-2015"

}