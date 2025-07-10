package io.github.locicope.multiparty

import io.github.locicope.Peers.Peer

trait Placeable[Placed[_, _ <: Peer]]:
  def lift[V, P <: Peer](value: V, isLocal: Boolean): Placed[V, P]
  def unlift[V, P <: Peer](value: Placed[V, P]): V