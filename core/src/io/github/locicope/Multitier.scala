package io.github.locicope

import io.circe.{Decoder, Encoder}
import io.github.locicope.Peers.{Peer, PeerRepr, TiedToMultiple, TiedToSingle, peerRepr}
import io.github.locicope.network.Network
import io.github.locicope.network.Reference.ResourceReference
import io.github.locicope.network.Reference.ValueType.Value
import io.github.locicope.macros.ASTHashing.*

import scala.annotation.implicitNotFound
import scala.util.NotGiven

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
  infix opaque type on[+V, -P <: Peer] = PlacedValue[V, P]

  /**
   * Represents a function that is placed in the context of a specific peer [[P]].
   */
  sealed trait PlacedFunction[-P <: Peer, Input <: Product: Encoder, Output: {Encoder, Decoder}]:
    val peerRepr: PeerRepr
    def apply(inputs: Input): Output on P

  private[locicope] class PlacedFunctionImpl[-P <: Peer, In <: Product: Encoder, Out: {Encoder, Decoder}](
      pRepr: PeerRepr
  )(
      body: In => Out on P
  ) extends PlacedFunction[P, In, Out]:
    val peerRepr: PeerRepr = pRepr
    override def apply(inputs: In): Out on P = body(inputs)

  /**
   * Possible value a placed type can take after the endpoint-projection phase.
   *
   * @tparam V
   *   the value type.
   * @tparam P
   *   the peer type on which the value is placed.
   */
  private enum PlacedValue[+V, -P <: Peer]:
    case Remote(resourceReference: ResourceReference)
    case Local(value: V, resourceReference: ResourceReference)

  extension [V, P <: Peer](placed: V on P)
    protected[locicope] def getReference: ResourceReference = placed match
      case PlacedValue.Remote(resourceReference)   => resourceReference
      case PlacedValue.Local(_, resourceReference) => resourceReference

  extension [V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](value: V on Remote)
    /**
     * Returns the value in the context of the peer [[P]].
     */
    def asLocal(using p: Placement, ps: p.PlacedLabel[Local]): V = p.asLocal(value)

  extension [V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](value: V on Remote)
    /**
     * Returns the value in the context of the peer [[P]].
     */
    def asLocalAll(using p: Placement, ps: p.PlacedLabel[Local]): Seq[V] = p.asLocalAll(value)

  extension [V, Local <: Peer](value: V on Local)
    /**
     * Unwraps the placed value in the context of its own peer [[Local]].
     */
    def unwrap(using p: Placement, ps: p.PlacedLabel[Local]): V = p.unwrap(value)

  /**
   * Capability to place values in a multitier application.
   */
  @implicitNotFound("No handler found for `Placement` capability. Ensure the `multitier` is used to give the handler.")
  trait Placement(using Network):
    /**
     * Define the scope in the context of the peer [[P]].
     * @tparam P
     *   the peer type for which the scope is defined.
     */
    class PlacedLabel[+P]

    protected val localPeerRepr: PeerRepr

    /**
     * Given a [[on]] value placed on a [[Remote]] peer, returns the value in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Single]] tie to the [[Remote]] peer.
     */
    def asLocal[V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](placed: V on Remote)(using
        PlacedLabel[Local]
    ): V

    /**
     * Given a [[on]] value placed on [[Remote]] peers, returns the values in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Multiple]] tie to the [[Remote]] peers.
     */
    def asLocalAll[V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](placed: V on Remote)(using
        PlacedLabel[Local]
    ): Seq[V]

    /**
     * Unwrap a placed value [[placed]] in the context of its own peer [[Local]].
     */
    def unwrap[V, Local <: Peer](placed: V on Local)(using PlacedLabel[Local]): V = placed match
      case PlacedValue.Remote(_)       => throw Exception("The value must be local but it is not")
      case PlacedValue.Local(value, _) => value

    /**
     * Represents a "placed computation", i.e., a function or a value that is defined in the context of the peer [[P]].
     */
    inline def placed[V: Encoder, P <: Peer, I <: Product: Encoder, O: Encoder](
        deps: PlacedFunction[?, I, O]*
    )(inline body: PlacedLabel[P] ?=> V)(using
        NotGiven[PlacedLabel[?]]
    ): V on P =
      given PlacedLabel[P]()
      val placementType = peerRepr[P]
      val resourceReference = ResourceReference(hashBody(body), placementType.baseTypeRepr, Value)
      if placementType <:< localPeerRepr then
        val bodyValue = body
        summon[Network].registerValue(resourceReference, bodyValue)
        PlacedValue.Local(bodyValue, resourceReference)
      else
        deps.filter(_.peerRepr <:< localPeerRepr).foreach(summon[Network].registerFunction(_))
        PlacedValue.Remote(resourceReference)

    inline def function[P <: Peer, In <: Product: Encoder, Out: {Encoder, Decoder}](
        body: In => Out
    ): PlacedFunction[P, In, Out] =
      val resourceReference = ResourceReference(hashBody(body), peerRepr[P].baseTypeRepr, Value)
      PlacedFunctionImpl[P, In, Out](peerRepr[P]) { inputs =>
        if peerRepr[P] <:< localPeerRepr then
          val bodyValue = body(inputs)
          PlacedValue.Local(bodyValue, resourceReference)
        else
          val result = summon[Network].callFunction[In, Out](inputs, resourceReference)
          PlacedValue.Local(result, resourceReference) // Here the value is local to the peer due to the remote call
      }

  object Placement:
    /**
     * Given a [[on]] value placed on a [[Remote]] peer, returns the value in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Single]] tie to the [[Remote]] peer.
     */
    def asLocal[V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](placed: V on Remote)(using
        p: Placement,
        ps: p.PlacedLabel[Local]
    ): V = p.asLocal(placed)

    /**
     * Given a [[on]] value placed on [[Remote]] peers, returns the values in the [[Local]] peer's context.
     *
     * This method can only be used if the [[Local]] peer has a [[Multiple]] tie to the [[Remote]] peers.
     */
    def asLocalAll[V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](placed: V on Remote)(using
        p: Placement,
        ps: p.PlacedLabel[Local]
    ): Seq[V] = p.asLocalAll(placed)

    /**
     * Unwrap a placed value [[placed]] in the context of its own peer [[Local]].
     */
    def unwrap[V, Local <: Peer](placed: V on Local)(using p: Placement, ps: p.PlacedLabel[Local]): V =
      p.unwrap(placed)

    /**
     * Represents a "placed computation", i.e., a function or a value that is defined in the context of the peer [[P]].
     */
    inline def placed[P <: Peer](using
        p: Placement,
        ng: NotGiven[p.PlacedLabel[?]]
    )[
        V: Encoder,
        I <: Product: Encoder,
        O: Encoder
    ](deps: PlacedFunction[?, I, O]*)(inline body: p.PlacedLabel[P] ?=> V): V on P = p.placed[V, P, I, O](deps*)(body)

    inline def function[P <: Peer](using
        p: Placement
    )[In <: Product: Encoder, Out: {Encoder, Decoder}](
        body: In => Out
    ): PlacedFunction[P, In, Out] = p.function[P, In, Out](body)

    inline def multitier[V, P <: Peer](application: Placement ?=> V)(using network: Network): V =
      network.startNetwork()
      application(using PlacementImpl[P](peerRepr[P]))
