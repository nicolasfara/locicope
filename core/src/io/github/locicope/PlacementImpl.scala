package io.github.locicope

import io.circe.Decoder
import io.github.locicope.Multitier.on
import io.github.locicope.Peers.{Peer, TiedToMultiple, TiedToSingle}
import io.github.locicope.network.Reference.ValueType.Value
import io.github.locicope.network.{Network, Reference}
import ox.Ox

import scala.collection.mutable
import scala.quoted.{Expr, Quotes, Type}

class PlacementImpl[LP <: Peer](using network: Network) extends Multitier.Placement:
  private val registeredValueCounter = mutable.Map[String, Int]().withDefaultValue(0)
  private val registeredPlacedCounter = mutable.Map[String, Int]().withDefaultValue(0)

  override type LocalPeer = LP

  override def asLocal[V: Decoder, Remote <: Peer, Local <: TiedToSingle[Remote]](
      placed: on[V, Remote]
  )(using PeerScope[Local], Ox): V = returnValueFromPlaced(placed)

  override def asLocalAll[V: Decoder, Remote <: Peer, Local <: TiedToMultiple[Remote]](
      placed: on[V, Remote]
  )(using PeerScope[Local], Ox): Seq[V] = returnAllValuesFromPlaced(placed)

  override protected def registerValue(peerName: String): Reference.ResourceReference =
    val index = getAndIncrement(registeredValueCounter, peerName)
    Reference.ResourceReference(peerName, index, Value)

  override protected def registerPlaced(peerName: String): Reference.ResourceReference =
    val index = getAndIncrement(registeredPlacedCounter, peerName)
    Reference.ResourceReference(peerName, index, Value)

  private def getAndIncrement(counter: mutable.Map[String, Int], key: String): Int =
    val currentValue = counter(key)
    counter.update(key, currentValue + 1)
    currentValue
