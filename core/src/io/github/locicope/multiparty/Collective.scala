package io.github.locicope.multiparty

import io.github.locicope.Peers.{Peer, TiedToMultiple}
import io.github.locicope.multiparty.network.Network
import io.github.locicope.multiparty.serialization.Encoder

trait Collective:
  trait NValue[+V]:
    def default: V

  def nbr[V: Encoder](value: V): NValue[V]
  def rep[V](initial: V)(evolution: V => V): V
  def branch[V](cond: Boolean)(th: NValue[V])(el: NValue[V]): NValue[V]

object Collective:
  def nbr[V: Encoder](value: V)(using coll: Collective): coll.NValue[V] = coll.nbr(value)

  def rep[V](initial: V)(evolution: V => V)(using coll: Collective): V = coll.rep(initial)(evolution)

  def branch[V](using coll: Collective)(cond: Boolean)(th: coll.NValue[V])(el: coll.NValue[V]): coll.NValue[V] =
    coll.branch(cond)(th)(el)

  def collective[V, C <: TiedToMultiple[C], F[_, _ <: Peer]: Flowable](using
      Network
  )(program: Collective ?=> V): F[V, C] = ???
