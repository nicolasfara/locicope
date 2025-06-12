package io.github.locicope.network

import io.circe.{Decoder, Encoder}
import io.github.locicope.Multitier.PlacedFunction
import io.github.locicope.network.Reference.ResourceReference
import ox.Ox
import ox.flow.Flow

trait Network:
  def receiveFrom[V: Decoder](from: ResourceReference): V
  def receiveFromAll[V: Decoder](from: ResourceReference): Seq[V]
//  def receiveFlowFrom[V: Decoder](from: ResourceReference)(using Ox): Flow[V]
  def registerPlaced[V: Encoder](produced: ResourceReference, value: V): Unit
  def registerValue[V: Encoder](produced: ResourceReference, value: V): Unit
  def registerFunction[In <: Product: Encoder, Out: Encoder](function: PlacedFunction[?, In, Out]): Unit
  def callFunction[In <: Product: Encoder, Out: Decoder](inputs: In, ref: ResourceReference): Out
//  def registerFlowResult[V](produced: ResourceReference, value: Flow[V]): Unit
  def startNetwork(): Unit = ()
