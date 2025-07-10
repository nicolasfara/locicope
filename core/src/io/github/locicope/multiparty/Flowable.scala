package io.github.locicope.multiparty

import io.github.locicope.Peers.Peer
import ox.flow.Flow

trait Flowable[F[_, _ <: Peer]]:
  def lift[V, P <: Peer](value: Flow[V], isLocal: Boolean): F[V, P]
  def unlift[V, P <: Peer](value: F[Flow[V], P]): Flow[V]
