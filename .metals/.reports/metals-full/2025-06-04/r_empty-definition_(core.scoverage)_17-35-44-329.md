error id: file://<WORKSPACE>/core/src/io/github/locicope/macros/AStHashing.scala:
file://<WORKSPACE>/core/src/io/github/locicope/macros/AStHashing.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 478
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
  inline def astHash: String = ${ astHashImpl }

  private def astHashImpl(using Quotes): Expr[String] =
    import quotes.reflect.*
    // val pos = Position.ofMacroExpansion
    // s"${pos.sourceFile.name}:${pos.startColumn}:${pos.endColumn}"
    '{
        @@val pos = Position.ofMacroExpansion
        s"${pos.sourceFile.name}:${pos.startColumn}:${pos.endColumn}"
    }

```


#### Short summary: 

empty definition using pc, found symbol in pc: 