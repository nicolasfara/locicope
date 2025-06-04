error id: file://<WORKSPACE>/core/src/io/github/locicope/Multitier.scala:
file://<WORKSPACE>/core/src/io/github/locicope/Multitier.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 8237
uri: file://<WORKSPACE>/core/src/io/github/locicope/Multitier.scala
text:
```scala
package io.github.locicope

import io.circe.{Decoder, Encoder}
import io.github.locicope.Peers.{Peer, PlacedAt, TiedToMultiple, TiedToSingle, peerRepresentation}
import io.github.locicope.network.Network
import io.github.locicope.network.Reference.ResourceReference
import io.github.locicope.network.Reference.ValueType.Value
import ox.Ox
import ox.flow.Flow

import scala.annotation.implicitNotFound
import scala.compiletime.erasedValue
import scala.quoted.{Expr, Quotes, Type}

/**
 * Object containing definitions for multitier applications.
 */
object Multitier:
  /**
   * The value [[V]] placed at the tier [[P]].
   *
   * Model a value which lived in the context of the tier [[P]]. Specific multitier operators can be used to manipulate
   * this value across different tiers.
   *
   * {{{
   * type Client <: { type Tie <: Single[Server] }
   * type Server <: { type Tie <: Single[Client] }
   *
   * val placedValue: Int on Client = on[Client](42)
   * inline def serverComputation(input: Int)(using Placed): Int on Server = on[Server]:
   *   input * 2
   * }}}
   */
  infix opaque type on[+V, +P <: Peer] = PlacedValue[V, P]

  /**
   * An asynchronous flow of values [[V]] placed at the tier [[P]].
   *
   * Model a flow of values which lived in the context of the tier [[P]]. Specific multitier operators can be used to
   * manipulate this flow across different tiers.
   *
   * {{{
   * type Client <: { type Tie <: Single[Server] }
   * type Server <: { type Tie <: Single[Client] }
   *
   * val placedFlow: Flow[Int] flowOn Client = on[Client](Flow.fromIterable(List(1, 2, 3)))
   * inline def serverFlowComputation(input: Flow[Int])(using Placed): Int flowOn Server = on[Server]:
   *   input.map(_ * 2)
   * }}}
   */
  infix opaque type flowOn[+V, +P <: Peer] = PlacedValue[Flow[V], P]

  /**
   * Possible value a placed type can take after the endpoint-projection phase.
   *
   * @tparam V
   *   the value type.
   * @tparam P
   *   the peer type on which the value is placed.
   */
  private enum PlacedValue[+V, +P <: Peer]:
    case Remote(resourceReference: ResourceReference)
    case Local(value: V, resourceReference: ResourceReference)
    case Value(value: V, resourceReference: ResourceReference)

  /**
   * Capability to place values in a multitier application.
   */
  @implicitNotFound("No handler found for `Placement` capability. Ensure the `multitier` is used to give the handler.")
  trait Placement(using network: Network):
    /**
     * Represents the peer on which this placement is defined.
     */
    type LocalPeer <: Peer

    /**
     * Define the scope in the context of the peer [[P]].
     * @tparam P
     *   the peer type for which the scope is defined.
     */
    class PeerScope[+P]

    /**
     * Given a [[on]] value placed on a [[Remote]] peer, returns the value in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Single]] tie to the [[Remote]] peer.
     */
    def asLocal[V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](placed: V on Remote)(using
        PeerScope[Local],
        Ox
    ): V

    /**
     * Given a [[on]] value placed on [[Remote]] peers, returns the values in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Multiple]] tie to the [[Remote]] peers.
     */
    def asLocalAll[V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](placed: V on Remote)(using
        PeerScope[Local],
        Ox
    ): Seq[V]

    /**
     * Unwrap a placed value [[placed]] in the context of its own peer [[Local]].
     */
    def unwrap[V, Local <: Peer](placed: V on Local)(using PeerScope[Local]): V = placed match
      case PlacedValue.Remote(_)       => throw Exception("The value must be local but it is not")
      case PlacedValue.Local(value, _) => value
      case PlacedValue.Value(value, _) => value

    /**
     * Represents a "placed computation", i.e., a function or a value that is defined in the context of the peer [[P]].
     */
    def placed[P <: Peer](using Ox)[V](body: => V): V on P = ???

    /**
     * Represents a "placed value", i.e., a value defined in the context of the peer [[P]].
     */
    def on[P <: Peer]: ValueBuilder[P] =
      ValueBuilder[P]()

    /**
     * Builder for creating placed function in a multitier application.
     *
     * This class is not meant to be instantiated directly, but rather used through the [[placed]] method.
     */
    class PlacedBuilder[P <: Peer](using PeerScope[P]):
      inline def apply[V: Encoder](inline body: PeerScope[P] ?=> V): V on P =
        inline erasedValue[P] match
          case _: LocalPeer => createLocal(body, peerRepresentation[P])
          case _            => createRemote(peerRepresentation[P])

    /**
     * Builder for creating placed values in a multitier application.
     *
     * This class is not meant to be instantiated directly, but rather used through the [[on]] method.
     */
    class ValueBuilder[P <: Peer]:
      inline def apply[V: Encoder](value: V): V on P =
        inline erasedValue[P] match
          case _: LocalPeer => createValue(value, peerRepresentation[P])
          case _            => createRemote(peerRepresentation[P])

    /**
     * Creates a resource reference for a value placed on the peer [[peerName]].
     */
    protected def registerValue(peerName: String): ResourceReference

    /**
     * Creates a resource reference for a function placed on the peer [[peerName]].
     */
    protected def registerPlaced(peerName: String): ResourceReference

    /**
     * Returns the value from the placed value [[placed]] in the context of the local peer.
     */
    protected def returnValueFromPlaced[V: Decoder, P <: Peer](placed: V on P)(using Ox): V =
      placed match
        case PlacedValue.Remote(resourceReference) => network.receiveFrom(resourceReference)
        case PlacedValue.Local(value, _)           => value
        case PlacedValue.Value(value, _)           => value

    protected def returnAllValuesFromPlaced[V: Decoder, P <: Peer](placed: V on P)(using Ox): Seq[V] =
      placed match
        case PlacedValue.Remote(resourceReference)       => network.receiveFromAll(resourceReference)
        case PlacedValue.Local(value, resourceReference) => network.receiveFromAll(resourceReference) :+ value
        case PlacedValue.Value(value, resourceReference) => network.receiveFromAll(resourceReference) :+ value

    private def createLocal[V: Encoder, P <: Peer](value: V, peerName: String): V on P =
      val ref = registerPlaced(peerName)
      network.registerPlaced(ref, value)
      PlacedValue.Local(value, ref)

    private def createRemote[V, P <: Peer](peerName: String): V on P =
      PlacedValue.Remote(registerPlaced(peerName))

    private def createValue[V: Encoder, P <: Peer](value: V, peerName: String): V on P =
      val ref = registerValue(peerName)
      network.registerValue(ref, value)
      PlacedValue.Value(value, ref)

  object Placement:
    /**
     * Given a [[on]] value placed on a [[Remote]] peer, returns the value in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Single]] tie to the [[Remote]] peer.
     */
    def asLocal[V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](placed: V on Remote)(using
        p: Placement,
        ps: p.PeerScope[Local],
        ox: Ox
    ): V = p.asLocal(placed)

    /**
     * Given a [[on]] value placed on [[Remote]] peers, returns the values in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Multiple]] tie to the [[Remote]] peers.
     */
    def asLocalAll[V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](placed: V on Remote)(using
        p: Placement,
        ps: p.PeerScope[Local],
        ox: Ox
    ): Seq[V] = p.asLocalAll(placed)

    /**
     * Unwrap a placed value [[placed]] in the context of its own peer [[Local]].
     */
    def unwrap[V, Local <: Peer](placed: V on Local)(using p: Placement, ps: p.PeerScope[Local]): V =
      p.unwrap(placed)

    /**
     * Represents a "placed computation", i.e., a function or a value th@@at is defined in the context of the peer [[P]].
     */
    def placed[P <: Peer](using p: Placement)[V](body: => V): V on P = p.placed(body)

    /**
     * Represents a "placed value", i.e., a value defined in the context of the peer [[P]].
     */
    def on[P <: Peer](using p: Placement): p.ValueBuilder[P] = p.on

    def multitier[V, P <: Peer](application: PlacedAt[P] ?=> V)(using network: Network, ox: Ox): V =
      network.startNetwork
      application(using PlacementImpl[P]())

```


#### Short summary: 

empty definition using pc, found symbol in pc: 