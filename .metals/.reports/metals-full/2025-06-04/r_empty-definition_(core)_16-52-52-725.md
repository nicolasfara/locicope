file://<WORKSPACE>/core/src/io/github/locicope/macros/AStHashing.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 374
uri: file://<WORKSPACE>/core/src/io/github/locicope/macros/AStHashing.scala
text:
```scala
package io.github.locicope.macros

import scala.quoted.*

object AStHashing:

  /**
   * Computes a hash for the given value.
   *
   * @param value The value to hash.
   * @return The computed hash as a String.
   */
  inline def astHash(inline value: Any): String = ${ astHashImpl('value) }

  private def astHashImpl(value: Expr[Any])(using Quotes): Expr[String] =
    im@@port quotes.reflect.*
    val pos = Position.ofMacroExpansion
    val startLine = pos.startLine
    val endLine = pos.endLine
    '{
      val srtLine = ${ Expr(startLine) }
      val edLine = ${ Expr(endLine) }
      s"$srtLine:$endLine"
    }

```


#### Short summary: 

empty definition using pc, found symbol in pc: 