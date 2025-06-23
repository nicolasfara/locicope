package io.github.locicope

//import io.github.locicope.ConsumeCapability.{Placement, place}

object ConsumeCapability:
  infix type on[V, P] = (V, P)
  trait Placement
//
//  trait Label[P]
//
//  def place[V, P](using p: Placement^)[Eff^](body: Label[P] ?->{Eff} V): V on P = ???
//
//def foo(using p: Placement): Unit =
////  given pl: Placement = new Placement {}
//
//  val x = place[Double, Int]:
//    place[Double, Int]:
//      ???
//    ???

