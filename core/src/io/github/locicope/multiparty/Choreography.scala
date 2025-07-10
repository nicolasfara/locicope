package io.github.locicope.multiparty

import io.github.locicope.Peers.{Peer, Quantifier}
import io.github.locicope.multiparty.network.Network
import io.github.locicope.multiparty.serialization.{Codec, Decoder, Encoder}

import scala.util.NotGiven

trait Choreography:
  trait ChoreographyLabel[+P <: Peer]

  def at[P <: Peer, V: Encoder, F[_, _ <: Peer]: Placeable](body: ChoreographyLabel[P] ?=> V)(using
      NotGiven[ChoreographyLabel[P]],
      Network
  ): F[V, P]

  def unwrap[V: Decoder, P <: Peer, F[_, _ <: Peer]](
      effect: F[V, P]
  )(using net: Network, cl: ChoreographyLabel[P]): Map[net.ID, V]

  def comm[V: Codec, Remote <: Peer, Local <: { type Tie <: Quantifier[Remote] }, F[_, _ <: Peer]](
      effect: F[V, Remote]
  )(using Network): F[V, Local]

  extension [V: Decoder, P <: Peer, F[_, _ <: Peer]](value: F[V, P])
    def ?(using net: Network, cl: ChoreographyLabel[P]): Map[net.ID, V] = unwrap(value)

object Choreography:
  def at[P <: Peer, V: Encoder, F[_, _ <: Peer]: Placeable](using
      choreo: Choreography,
      net: Network,
      ng: NotGiven[choreo.ChoreographyLabel[P]]
  )(
      body: choreo.ChoreographyLabel[P] ?=> V
  ): F[V, P] = choreo.at[P, V, F](body)

  def unwrap[V: Decoder, P <: Peer, F[_, _ <: Peer]](effect: F[V, P])(using
      choreo: Choreography,
      net: Network,
      label: choreo.ChoreographyLabel[P]
  ): Map[net.ID, V] = choreo.unwrap[V, P, F](effect)

  def comm[V: Codec, Remote <: Peer, Local <: { type Tie <: Quantifier[Remote] }, F[_, _ <: Peer]](
      effect: F[V, Remote]
  )(using
      choreo: Choreography,
      net: Network
  ): F[V, Local] = choreo.comm[V, Remote, Local, F](effect)
