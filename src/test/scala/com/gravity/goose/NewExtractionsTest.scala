package com.gravity.goose

import java.io.{File, PrintWriter}

import extractors.PublishDateExtractor
import org.junit.Test
import org.junit.Assert._
import utils.FileHelper
import java.text.SimpleDateFormat
import org.jsoup.select.Selector
import org.jsoup.nodes.Element
import java.util.Date

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
  def cnn1() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("cnn1.txt")
    val url = "http://www.cnn.com/2010/POLITICS/08/13/democrats.social.security/index.html"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    val title = "Democrats to use Social Security against GOP this fall"
    val content = "Washington (CNN) -- Democrats pledged "
    TestUtils.runArticleAssertions(article = article, expectedTitle = title, expectedStart = content)
  }

  @Test
  def businessWeek2() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("businessweek2.txt")
    val url: String = "http://www.businessweek.com/technology/here-comes-apples-real-tv-09132011.html"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "At Home Depot, we first realized we needed to have a real conversation with",
      expectedImage = null)
    TestUtils.printReport()
  }

  @Test
  def businessWeek3() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("businessweek3.txt")
    val url: String = "http://www.businessweek.com/management/five-social-media-lessons-for-business-09202011.html"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "Get ready, America, because by Christmas 2012 you will have an Apple TV in your living room",
      expectedImage = null)
    TestUtils.printReport()
  }

  @Test
  def techcrunch1() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("techcrunch1.txt")
    val url = "http://techcrunch.com/2011/08/13/2005-zuckerberg-didnt-want-to-take-over-the-world/"
    val content = "The Huffington Post has come across this fascinating five-minute interview"
    val title = "2005 Zuckerberg Didn’t Want To Take Over The World"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    TestUtils.runArticleAssertions(article = article, expectedTitle = title, expectedStart = content)
  }

  @Test
  def businessweek1() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("businessweek1.txt")
    val url: String = "http://www.businessweek.com/magazine/content/10_34/b4192066630779.htm"
    val title = "Olivia Munn: Queen of the Uncool"
    val content = "Six years ago, Olivia Munn arrived in Hollywood with fading ambitions of making it as a sports reporter and set about deploying"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    TestUtils.runArticleAssertions(article = article, expectedTitle = title, expectedStart = content)
  }

  @Test
  def foxNews() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("foxnews1.txt")
    val url: String = "http://www.foxnews.com/politics/2010/08/14/russias-nuclear-help-iran-stirs-questions-improved-relations/"
    val content = "Russia's announcement that it will help Iran get nuclear fuel is raising questions"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    TestUtils.runArticleAssertions(article = article, expectedStart = content)

  }

  @Test
  def aolNews() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("aol1.txt")
    val url: String = "http://www.aolnews.com/nation/article/the-few-the-proud-the-marines-getting-a-makeover/19592478"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    val content = "WASHINGTON (Aug. 13) -- Declaring \"the maritime soul of the Marine Corps\" is"
    TestUtils.runArticleAssertions(article = article, expectedStart = content)
  }

  @Test
  def huffingtonPost2() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("huffpo2.txt")
    val url: String = "http://www.huffingtonpost.com/2011/10/06/alabama-workers-immigration-law_n_997793.html"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    val content = "MONTGOMERY, Ala. -- Alabama's strict new immigration law may be backfiring."
    TestUtils.runArticleAssertions(article = article, expectedStart = content)
  }


  @Test
  def testHuffingtonPost() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val url: String = "http://www.huffingtonpost.com/2010/08/13/federal-reserve-pursuing_n_681540.html"
    val html = getHtml("huffpo1.txt")

    val title: String = "Federal Reserve's Low Rate Policy Is A 'Dangerous Gamble,' Says Top Central Bank Official"
    val content = "A top regional Federal Reserve official sharply criticized Friday"
    val keywords = "federal, reserve's, low, rate, policy, is, a, 'dangerous, gamble,', says, top, central, bank, official, business"
    val description = "A top regional Federal Reserve official sharply criticized Friday the Fed's ongoing policy of keeping interest rates near zero -- and at record lows -- as a \"dangerous gamble.\""
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    TestUtils.runArticleAssertions(article = article, expectedTitle = title, expectedStart = content, expectedDescription = description)

    val expectedTags = "Federal Open Market Committee" ::
        "Federal Reserve" ::
        "Federal Reserve Bank Of Kansas City" ::
        "Financial Crisis" ::
        "Financial Reform" ::
        "Financial Regulation" ::
        "Financial Regulatory Reform" ::
        "Fomc" ::
        "Great Recession" ::
        "Interest Rates" ::
        "Kansas City Fed" ::
        "Monetary Policy" ::
        "The Financial Fix" ::
        "Thomas Hoenig" ::
        "Too Big To Fail" ::
        "Wall Street Reform" ::
        "Business News" ::
        Nil
    assertNotNull("Tags should not be NULL!", article.tags)
    assertTrue("Tags should not be empty!", article.tags.size > 0)

    for (actualTag <- article.tags) {
      assertTrue("Each Tag should be contained in the expected set!", expectedTags.contains(actualTag))
    }
  }


  @Test
  def wallStreetJournal() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("wsj1.txt")
    val url: String = "http://online.wsj.com/article/SB10001424052748704532204575397061414483040.html"
    val article = TestUtils.getArticle(url = url, rawHTML = html)
    val content = "The Obama administration has paid out less than a third of the nearly $230 billion"
    TestUtils.runArticleAssertions(article = article, expectedStart = content)
  }

  @Test
  def usaToday() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("usatoday1.txt")
    val url: String = "http://content.usatoday.com/communities/thehuddle/post/2010/08/brett-favre-practices-set-to-speak-about-return-to-minnesota-vikings/1"
    val article = TestUtils.getArticle(url, rawHTML = html)
    val content = "Brett Favre says he couldn't give up on one more"
    TestUtils.runArticleAssertions(article = article, expectedStart = content)
  }

  @Test
  def wiredPubDate() {
    val url = "http://www.wired.com/playbook/2010/08/stress-hormones-boxing/";
    val html = getHtml("wired1.txt")
    val fmt = new SimpleDateFormat("yyyy-MM-dd")

    // example of a custom PublishDateExtractor
    implicit val config = new Configuration();
    config.enableImageFetching = false
    config.setPublishDateExtractor(new PublishDateExtractor() {
      @Override
      def extract(rootElement: Element): Date = {
        // look for this guy: <meta name="DisplayDate" content="2010-08-18" />
        val elements = Selector.select("meta[name=DisplayDate]", rootElement);
        if (elements.size() == 0) return null;
        val metaDisplayDate = elements.get(0);
        if (metaDisplayDate.hasAttr("content")) {
          val dateStr = metaDisplayDate.attr("content");

          return fmt.parse(dateStr);
        }
        null;
      }
    });

    val article = TestUtils.getArticle(url, rawHTML = html)

    TestUtils.runArticleAssertions(
      article,
      "Stress Hormones Could Predict Boxing Dominance",
      "On November 25, 1980, professional boxing");

    val expectedDateString = "2010-08-18";
    assertNotNull("publishDate should not be null!", article.publishDate);
    assertEquals("Publish date should equal: \"2010-08-18\"", expectedDateString, fmt.format(article.publishDate));
    System.out.println("Publish Date Extracted: " + fmt.format(article.publishDate));

  }

  @Test
  def espn() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("espn1.txt")
    val url: String = "http://sports.espn.go.com/espn/commentary/news/story?id=5461430"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "If you believe what college football coaches have said about sports")
  }


  @Test
  def engadget() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("engadget1.txt")
    val url: String = "http://www.engadget.com/2010/08/18/verizon-fios-set-top-boxes-getting-a-new-hd-guide-external-stor/"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "Streaming and downloading TV content to mobiles is nice")
  }

  @Test
  def msn1() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("msn1.txt")
    val expected = getHtml("msn1_result.txt")
    val url: String = "http://lifestyle.msn.com/your-life/your-money-today/article.aspx?cp-documentid=31244150"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = expected)
  }

  @Test
  def guardian1() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("guardian1.txt")
    val expected = getHtml("guardian1_result.txt")
    val url: String = "http://www.guardian.co.uk/film/2011/nov/18/kristen-wiig-bridesmaids"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = expected)

    assertNotNull("publishDate should not be null!", article.publishDate)
    val expDate = new java.util.Date(1321657238000L) // "2011-11-18T23:00:38Z"
    assertEquals(s"""Publish date should equal: "$expDate"""", expDate, article.publishDate)
    System.out.println("Publish Date Extracted: " + article.publishDate)
  }


  @Test
  def time() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("time1.txt")
    val url: String = "http://www.time.com/time/health/article/0,8599,2011497,00.html"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "This month, the federal government released",
      expectedTitle = "Invisible Oil from BP Spill May Threaten Gulf Aquatic Life")
  }

  @Test
  def time2() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("time2.txt")
    val url: String = "http://newsfeed.time.com/2011/08/24/washington-monument-closes-to-repair-earthquake-induced-crack/"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "Despite what the jeers of jaded Californians might suggest")
  }

  @Test
  def cnet() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("cnet1.txt")
    val url: String = "http://news.cnet.com/8301-30686_3-20014053-266.html?tag=topStories1"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "NEW YORK--Verizon Communications is prepping a new")
  }

  @Test
  def yahoo() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("yahoo1.txt")
    val url: String = "http://news.yahoo.com/apple-says-steve-jobs-resigning-ceo-224628633.html"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "SAN FRANCISCO (AP) — Steve Jobs, the mind behind the iPhone")
  }

  @Test
  def politico() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("politico1.txt")
    val url: String = "http://www.politico.com/news/stories/1010/43352.html"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "If the newest Census Bureau estimates stay close to form")
  }


  @Test
  def businessinsider1() {
    val url = "http://www.businessinsider.com/goldman-on-the-fed-announcement-2011-9"
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("businessinsider1.txt")
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "As everyone in the world was transfixed on the Fed")

    println(article.cleanedArticleText)
  }

  @Test
  def businessinsider2() {
    val url = "http://www.businessinsider.com/goldman-on-the-fed-announcement-2011-9"
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("businessinsider2.txt")
    val article = TestUtils.getArticle(url, html)

    TestUtils.runArticleAssertions(article = article,
      expectedStart = "From Goldman on the FOMC operation twist announcement")

  }

  @Test
  def cnbc1() {
    val url = "http://www.cnbc.com/id/44613978"
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("cnbc1.txt")
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "Some traders found Wednesday's Fed statement to be a bit gloomier than expected.")

  }

  /*
  * --------------------------------------------------------
  * Test Fixes for GitHub Issues Submitted
  * --------------------------------------------------------
  */
  @Test
  def issue24() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("issue_24.txt")
    val expected = getHtml("issue_24_result.txt")
    val url: String = "http://danielspicar.github.com/goose-bug.html"
    val article = TestUtils.getArticle(url, html)
    assertEquals("The beginning of the article text was not as expected!", expected, article.cleanedArticleText)
  }

  @Test
  def issue25() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("issue_25.txt")
    val url: String = "http://www.accountancyage.com/aa/analysis/2111729/institutes-ifrs-bang"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "UK INSTITUTES have thrown their weight behind rapid adoption of international financial reporting standards in the US.")
  }

  @Test
  def issue28() {
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("issue_28.txt")
    val url: String = "http://www.telegraph.co.uk/foodanddrink/foodanddrinknews/8808120/Worlds-hottest-chilli-contest-leaves-two-in-hospital.html"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "Emergency services were called to Kismot Restaurant's curry-eating challenge,",
      expectedImage = null)
  }

  @Test
  def issue32() {
    // this link is an example of web devs putting content not in paragraphs but embedding them in span tags with br's
    implicit val config = TestUtils.NO_IMAGE_CONFIG
    val html = getHtml("issue_32.txt")
    val url: String = "http://www.tulsaworld.com/site/articlepath.aspx?articleid=20111118_61_A16_Opposi344152&rss_lnk=7"
    val article = TestUtils.getArticle(url, html)
    TestUtils.runArticleAssertions(article = article,
      expectedStart = "Opposition to a proposal to remove certain personal data",
      expectedImage = null)
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


  // http://motherboard-images.vice.com/content-images/contentimage/no-id/1461696042952895.jpeg
  // http://www.engadget.com/2016/04/27/dyson-made-a-hair-dryer/
  // https://hbr.org/2016/04/social-media-is-too-important-to-be-left-to-the-marketing-department
  // http://www.dailyedge.ie/pizza-pizza-box-amazing-2742051-Apr2016/
  // http://www.fool.ca/2016/04/28/bombardier-inc-gets-another-order/

}