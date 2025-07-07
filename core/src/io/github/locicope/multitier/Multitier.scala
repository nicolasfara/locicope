package io.github.locicope.multitier

import io.github.locicope.Peers.Quantifier.Single
import io.github.locicope.Peers.{Peer, PeerRepr, TiedToMultiple, TiedToSingle}
import ox.flow.Flow

import scala.util.NotGiven

object MultitierModule:
  trait PlacedFunction[P <: Peer, In <: Product, Out, F[_, _ <: Peer]: Placeable]:
    protected val localPeerRepr: PeerRepr
    def apply(inputs: In): F[Out, P]

  trait Multitier:
    trait MultitierLabel[+P <: Peer]

    def function[P <: Peer, In <: Product, Out, F[_, _ <: Peer]: Placeable](
        body: MultitierLabel[P] ?=> In => Out
    )(using NotGiven[MultitierLabel[P]]): PlacedFunction[P, In, Out, F]

    def placed[V, P <: Peer, F[_, _ <: Peer]: Placeable](
        deps: PlacedFunction[?, ?, ?, F]*
    )(body: MultitierLabel[P] ?=> V)(using
        NotGiven[MultitierLabel[P]],
        Network
    ): F[V, P]

    def asLocal[V, Remote <: Peer, Local <: TiedToSingle[Remote], F[_, _ <: Peer]: Placeable](
        effect: F[V, Remote]
    )(using Network, MultitierLabel[Local]): V

    def asLocalAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote], F[_, _ <: Peer]: Placeable](
        effect: F[V, Remote]
    )(using net: Network, ml: MultitierLabel[Local]): Map[net.ID, V]

    def asLocalFlow[V, Remote <: Peer, Local <: TiedToSingle[Remote], F[_, _ <: Peer]: Placeable](
        flow: F[Flow[V], Remote]
    )(using Network, MultitierLabel[Local]): Flow[V]

    def asLocalFlowAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote], F[_, _ <: Peer]: Placeable](
        flow: F[Flow[V], Remote]
    )(using net: Network, ml: MultitierLabel[Local]): Flow[(net.ID, V)]

    extension [V, Remote <: Peer, F[_, _ <: Peer]: Placeable](value: F[V, Remote])
      def ?[Local <: TiedToSingle[Remote]](using Network, MultitierLabel[Local]): V = asLocal(value)
      def ??[Local <: TiedToMultiple[Remote]](using net: Network, ml: MultitierLabel[Local]): Map[net.ID, V] =
        asLocalAll(value)

    extension [V, Remote <: Peer, F[_, _ <: Peer]: Placeable](flow: F[Flow[V], Remote])
      def ?[Local <: TiedToSingle[Remote]](using Network, MultitierLabel[Local]): Flow[V] = asLocalFlow(flow)
      def ??[Local <: TiedToMultiple[Remote]](using net: Network, ml: MultitierLabel[Local]): Flow[(net.ID, V)] =
        asLocalFlowAll(flow)

  object Multitier:
    def function[P <: Peer, In <: Product, Out, F[_, _ <: Peer]: Placeable](using
        mt: Multitier,
        ng: NotGiven[mt.MultitierLabel[P]]
    )(
        body: mt.MultitierLabel[P] ?=> In => Out
    ): PlacedFunction[P, In, Out, F] = mt.function(body)

    def placed[V, P <: Peer, F[_, _ <: Peer]: Placeable](deps: PlacedFunction[?, ?, ?, F]*)(using
        net: Network,
        mt: Multitier,
        ng: NotGiven[mt.MultitierLabel[P]]
    )(
        body: mt.MultitierLabel[P] ?=> V
    ): F[V, P] = mt.placed(deps*)(body)

    def asLocal[V, Remote <: Peer, Local <: TiedToSingle[Remote], F[_, _]: Placeable](
        effect: F[V, Remote]
    )(using
        net: Network,
        mt: Multitier,
        ml: mt.MultitierLabel[Local]
    ): V = mt.asLocal(effect)

    def asLocalAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote], F[_, _]: Placeable](
        effect: F[V, Remote]
    )(using net: Network, mt: Multitier, ml: mt.MultitierLabel[Local]): Map[net.ID, V] = mt.asLocalAll(effect)

    def asLocalFlow[V, Remote <: Peer, Local <: TiedToSingle[Remote], F[_, _]: Placeable](
        flow: F[Flow[V], Remote]
    )(using net: Network, mt: Multitier, ml: mt.MultitierLabel[Local]): Flow[V] = mt.asLocalFlow(flow)

    def asLocalFlowAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote], F[_, _]: Placeable](
        flow: F[Flow[V], Remote]
    )(using net: Network, mt: Multitier, ml: mt.MultitierLabel[Local]): Flow[(net.ID, V)] = mt.asLocalFlowAll(flow)

object Test:
  type Client <: Peer { type Tie <: Single[Server] }
  type Server <: Peer { type Tie <: Single[Client] }

  import MultitierModule.Multitier
  import MultitierModule.Multitier.*
  import PlacementType.on

  def placedFunction(using Multitier, Network) = function[Server, (Int, String), Int, on]: (inputs: (Int, String)) =>
    inputs._1 + inputs._2.length

  def foo(using Multitier, Network): Int on Client = placed():
    val a = placedFunction((12, "Hello")).?
    12
