package com.gravity.goose.network

import java.nio.charset.StandardCharsets.ISO_8859_1
import java.nio.charset.StandardCharsets.UTF_16BE
import java.nio.charset.StandardCharsets.UTF_16LE
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CodingErrorAction
import java.util.Arrays


import EnsureEncoding._
//remove if not needed
import scala.collection.JavaConversions._

object EnsureEncoding {

  val TRY_ENC_UTF8_ISO88591_UTF16LE_UTF16BE = Array(UTF_8, ISO_8859_1, UTF_16LE, UTF_16BE)

  val TRY_ENC_UTF16LE_UTF16BE_UTF8_ISO88591 = Array(UTF_16LE, UTF_16BE, UTF_8, ISO_8859_1)

  trait ContentCheck {

    def isValidContent(content: String): Boolean
  }

  class NoContentCheck extends ContentCheck {

    override def isValidContent(content: String): Boolean = true
  }

  private class ContainsStringContentCheck(val stringToCheck: String) extends ContentCheck {

    override def isValidContent(content: String): Boolean = content.contains(stringToCheck)
  }
}

class EnsureEncoding(private val encodingsToTry: Array[Charset]) {

  def this() {
    this(TRY_ENC_UTF8_ISO88591_UTF16LE_UTF16BE)
  }

  def decode(chars: Array[Byte]): String = decode(chars, new NoContentCheck())

  def decode(chars: Array[Byte], hasToContain: String): String = {
    decode(chars, new ContainsStringContentCheck(hasToContain))
  }

  def decode(chars: Array[Byte], check: ContentCheck): String = {
    for (encodingToTry <- encodingsToTry) {
      try {
        val content = decode(chars, encodingToTry)
        if (check.isValidContent(content)) {
          return content
        }
      } catch {
        case e: CharacterCodingException =>
      }
    }
    throw new IllegalStateException("was not able to encode string using these encodings" )
  }

  protected def decode(chars: Array[Byte], encodingToTry: Charset): String = {
    val decoder = encodingToTry.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
    val byteBuffer = ByteBuffer.wrap(chars)
    val decoded = decoder.decode(byteBuffer)
    decoded.toString
  }
}
