package io.github.locicope

import io.github.locicope.Multitier.Placement
import io.github.locicope.Peers.Quantifier.{Multiple, Single}

import scala.quoted.{Expr, Quotes, Type}

/**
 * Object containing definitions for peers in a network.
 *
 * Peers are the basic building blocks of a network, representing entities that can communicate with each other.
 */
object Peers:

  /**
   * Prototype of a peer in the network.
   */
  type Peer = { type Tie }

  /**
   * Multitier application placed at a specific peer [[P]].
   */
  type PlacedAt[P <: Peer] = Placement { type LocalPeer = P }

  /**
   * Prototype of a peer in the network which is tied to a single other [[P]] peer.
   *
   * {{{
   * type Client <: { type Tie <: Single[Server] }
   * type Server <: { type Tie <: Single[Client] }
   * }}}
   */
  type TiedToSingle[P <: Peer] = { type Tie <: Single[P] }

  /**
   * Prototype of a peer in the network which is tied to multiple other [[P]] peers.
   *
   * {{{
   * type Client <: { type Tie <: Multiple[Client] }
   * }}}
   */
  type TiedToMultiple[P <: Peer] = { type Tie <: Multiple[P] }

  /**
   * Tie cardinality of a peer.
   *
   * {{{
   * type Client <: { type Tie <: Single[Server] }
   * type Server <: { type Tie <: Multiple[Client] }
   * }}}
   *
   * @tparam P
   *   the other peer type on which the quantifier is defined.
   */
  enum Quantifier[+P <: Peer]:
    case Single()
    case Multiple()

  /**
   * Returns the string representation of the peer type [[P]].
   */
  inline def peerRepresentation[P <: Peer]: String = ${ peerRepresentationImpl[P] }

  private def peerRepresentationImpl[P: Type](using quotes: Quotes): Expr[String] =
    import quotes.reflect.*
    val re = TypeRepr.of[P].show
    Expr(re)
