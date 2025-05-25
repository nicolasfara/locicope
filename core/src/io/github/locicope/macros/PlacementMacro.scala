package io.github.locicope.macros

import io.circe.Encoder
import io.github.locicope.Multitier.{Placement, on}
import io.github.locicope.Peers.Peer
import ox.Ox

import scala.quoted.*

object PlacementMacro:
  inline def plcMacro[V, LP <: Peer, P <: Peer](inline body: => V)(using Ox, Encoder[V]): V on P =
    ${ plcMacroImpl[V, LP, P]('body) }

  def plcMacroImpl[V: Type, LP <: Peer: Type, P <: Peer: Type](body: Expr[V])(using quotes: Quotes): Expr[V on P] =
    import quotes.reflect.*
    val isLocalPlacement = TypeRepr.of[LP] =:= TypeRepr.of[P]
    if isLocalPlacement then report.errorAndAbort("SAME PLACEMENT")
    else ???
