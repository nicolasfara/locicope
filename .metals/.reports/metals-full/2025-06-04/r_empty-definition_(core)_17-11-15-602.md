file://<WORKSPACE>/core/src/io/github/locicope/macros/AStHashing.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -scala/quoted/`using`.
	 -scala/quoted/`using`#
	 -scala/quoted/`using`().
	 -`using`.
	 -`using`#
	 -`using`().
	 -scala/Predef.`using`.
	 -scala/Predef.`using`#
	 -scala/Predef.`using`().
offset: 331
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
  inline def astHash(value: Any): String = ${ astHashImp('value) }

  private def astHashImpl(value: Expr[Any])(u@@sing Quotes): Expr[String] =
    import quotes.reflect.*
    val pos = Position.ofMacroExpansion
    val startLine = pos.startLine
    val endLine = pos.endLine
    report.error(s"Macro expansion at line $startLine to $endLine")
    '{
      val srtLine = ${ Expr(startLine) }
      val edLine = ${ Expr(endLine) }
      s"$srtLine:$edLine"
    }

```


#### Short summary: 

empty definition using pc, found symbol in pc: 