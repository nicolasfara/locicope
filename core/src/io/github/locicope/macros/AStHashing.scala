package io.github.locicope.macros

import scala.quoted.*
import java.nio.charset.StandardCharsets

object AStHashing:
  inline def astHash(body: Any): String = ${ astHashImpl('body) }

  private def astHashImpl(body: Expr[Any])(using Quotes): Expr[String] =
    import quotes.reflect.*
    val pos = Position.ofMacroExpansion
    val hashed = fletcher16Checksum(s"${pos.sourceFile.name}:${pos.startColumn}:${pos.endColumn}").toHexString
    report.error(s"Macro expansion position: ${hashed}")
    Expr(s"${pos.sourceFile.name}:${pos.startColumn}:${pos.endColumn}")

  def fletcher16Checksum(input: String): Int =
    val bytes = input.getBytes(StandardCharsets.UTF_8)
    var sum1 = 0
    var sum2 = 0

    for (byte <- bytes)
        sum1 = (sum1 + (byte & 0xFF)) % 255
        sum2 = (sum2 + sum1) % 255

    (sum2 << 8) | sum1
