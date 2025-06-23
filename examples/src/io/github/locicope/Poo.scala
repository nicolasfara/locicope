package io.github.locicope

import io.github.locicope.Peers.Quantifier.Single
import io.github.locicope.Peers.{Peer, TiedToMultiple, TiedToSingle}

object Poo:
  class Foo extends caps.Capability
//  class Label
//
//  def foo(elems: Foo*)(body: Label^ ->{elems*} Int): Int = elems.size
//
//  val t =
//    val p = Foo()
//    val p1 = Foo()
//    foo(p, p1): l =>
//      val _ = p
//      val _ = p1
//      12

//  opaque infix type on[+V, P <: Peer] = Place[V, P]
//  trait PlacedFunction[P <: Peer] extends caps.Capability:
//    type In <: Product
//    type Out
//    def apply(in: In): Out on P
//
//  enum Place[+V, -P <: Peer]:
//    case Remote(ref: String)
//    case Local(value: V, ref: String)
//
//  trait Placement:
//    trait Label[+P <: Peer]
//
////    def function[P <: Peer, In <: Tuple, Out, Eff^]: PlacedFunction[P]
//    def placed[V, P <: Peer](deps: Any*)(body: Label[P] => V): V on P
//    def asLocal[V, Remote <: Peer, Local <: TiedToSingle[Remote]](placed: V on Remote): V
//    def asLocalAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote]](placed: V on Remote): Seq[V]
//
//  object Placement:
////    def function[P <: Peer](using p: Placement)[
////      In <: Tuple, Out, Eff^
////    ](bodyFunction: In ->{Eff} Out): PlacedFunction[P] = p.function
//
//    def placed[P <: Peer](using p: Placement)(d: Any^)[C^ >: {d} <: {d}, V](body: p.Label[P] ->{C} V): V on P =
//      p.placed[V, P](d)(body)
//
//    def placed[P <: Peer](using p: Placement)(d: Any^, d1: Any^)[C^ >: {d, d1} <: {d, d1}, V](body: p.Label[P] ->{C} V): V on P =
//      p.placed[V, P](d, d1)(body)
//
////    def placed[P <: Peer](using p: Placement)[V](body1: p.Label[P] ?-> V): V on P =
////      p.placed[V, P]()(body1)
//
//    def asLocal[V, Remote <: Peer, Local <: TiedToSingle[Remote]](using
//      p: Placement,
//      l: p.Label[Local]
//    )(placed: V on Remote): V = p.asLocal(placed)
//
//    def asLocalAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote]](using
//      p: Placement,
//      l: p.Label[Local]
//    )(placed: V on Remote): Seq[V] = p.asLocalAll(placed)
//
//  class Foo extends caps.Capability
//
//  type Client <: { type Tie <: Single[Client] }
//  def foo(using Placement): Int on Client = {
//    val foo1 = Foo()
//    val foo2 = Foo()
//    Placement.placed[Client](foo1, foo2): l =>
//      val _ = foo1
//      val _ = foo2
//      12
//  }