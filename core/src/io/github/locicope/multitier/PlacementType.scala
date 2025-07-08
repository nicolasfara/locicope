package io.github.locicope.multitier

import io.github.locicope.Peers.Peer
import ox.flow.Flow

trait Placeable[Placed[_, _ <: Peer]]:
  def lift[V, P <: Peer](value: V, isLocal: Boolean): Placed[V, P]
  def unlift[V, P <: Peer](value: Placed[V, P]): V

trait Flowable[F[_, _ <: Peer]]:
  def lift[V, P <: Peer](value: Flow[V], isLocal: Boolean): F[V, P]
  def unlift[V, P <: Peer](value: F[Flow[V], P]): Flow[V]

object PlacementType:
  infix opaque type on[+V, -P <: Peer] = PlacedType[V, P]
  infix opaque type flowOn[+V, -P <: Peer] = PlacedFlowType[V, P]

  private enum PlacedType[+V, -P <: Peer]:
    case Local(value: V, remoteReference: String)
    case Remote(remoteReference: String)

  private enum PlacedFlowType[+V, -P <: Peer]:
    case Local(value: Flow[V], remoteReference: String)
    case Remote(remoteReference: String)

  given Placeable[on] with
    override def lift[V, P <: Peer](value: V, isLocal: Boolean): V on P = ???
    override def unlift[V, P <: Peer](value: V on P): V = ???

  given Flowable[flowOn] with
    override def lift[V, P <: Peer](value: Flow[V], isLocal: Boolean): V flowOn P = ???
    override def unlift[V, P <: Peer](value: Flow[V] flowOn P): Flow[V] = ???
