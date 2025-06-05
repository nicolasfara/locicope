package io.github.locicope

import io.circe.Decoder
import io.github.locicope.Multitier.on
import io.github.locicope.Peers.{Peer, PeerRepr, TiedToMultiple, TiedToSingle}
import io.github.locicope.network.Network

class PlacementImpl[LP <: Peer](override protected val localPeerRepr: PeerRepr)(using
    network: Network
) extends Multitier.Placement:

  override def asLocal[V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](placed: on[V, Remote])(using
      PlacedLabel[Local]
  ): V = network.receiveFrom(placed.getReference)

  override def asLocalAll[V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](placed: on[V, Remote])(using
      PlacedLabel[Local]
  ): Seq[V] = network.receiveFromAll(placed.getReference)

// type Node <: { type Tie <: Multiple[Node] }
// type Client <: Node { type Tie <: Single[Server] }
// type Server <: Node { type Tie <: Single[Client] }

// def foo: Int on Node = ...

// def myProgram = on[Node]:
//   foo
//   foo.asLocal

// inline def getTypeOf[T]: Type[T] = ???
// def getTypeOfImpl[T: Type](using Quotes): Expr[Type[T]] = summon[Type[T]]

// run.on[Client](myProgram)
// run.on[Server](myProgram)
// run.on[Node](myProgram)
