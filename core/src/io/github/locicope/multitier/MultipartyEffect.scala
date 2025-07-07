package io.github.locicope.multitier

trait MultipartyEffect
//
//import io.github.locicope.multitier.Cardinality.{Multiple, Single}
//
//trait Node:
//  type Tie <: Cardinality
//
//enum Cardinality[+N <: Node]:
//  case Single()
//  case Multiple()
//
//trait Placed[V, L <: Node]:
//  def pure: V
//  def flatMap[B, R <: Node](f: V => Placed[B, R]): Placed[B, L]
//  def map[B](f: V => B): Placed[B, L]
//
//trait Monad[F[_]]:
//  def pure[A](value: A): F[A]
//  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
//  def map[A, B](fa: F[A])(f: A => B): F[B]
//
//extension [F[_]: Monad as m, A](fa: F[A])
//  def flatMap[B](f: A => F[B]): F[B] = m.flatMap(fa)(f)
//  def map[B](f: A => B): F[B] = m.map(fa)(f)
//  def pure(value: A): F[A] = m.pure(value)
//
////trait MonadP[F[_, _ <: Node]]:
////  def pure[A, P <: Node](value: A): F[A, P]
////  def flatMap[A, B, P1 <: Node, P2 <: Node](fa: F[A, P1])(f: A => F[B, P2]): F[B, P2]
////  def map[A, B, P <: Node](fa: F[A, P])(f: A => B): F[B, P]
//
////extension [F[_, _ <: Node]: MonadP, A, P <: Node](fa: F[A, P])
////  def flatMap[B, P2 <: Node](f: A => F[B, P2]): F[B, P2] = summon[MonadP[F]].flatMap(fa)(f)
////  def map[B](f: A => B): F[B, P] = summon[MonadP[F]].map(fa)(f)
////  def pure(value: A): F[A, P] = summon[MonadP[F]].pure(value)
//
//trait Placement[F[_]: Monad]:
//  def on[V, Local <: Node](value: V): Placed[F[V], Local]
//
//object Placement:
//  def on[V, Local <: Node, F[_]](using placement: Placement[F])(value: => V): Placed[F[V], Local] =
//    placement.on(value)
//
//trait Multitier[F[_]]:
//  def placed[V, Local <: Node, Remote <: Node](body: => F[V]): Placed[F[V], Local]
//  def asLocal[V, Remote <: Node, Local <: Node { type Tie <: Single[Remote] }](
//      effect: Placed[F[V], Remote]
//  ): Placed[F[V], Local]
//  def asLocalAll[V, Remote <: Node, Local <: Node { type Tie <: Multiple[Remote] }](
//      effect: Placed[F[V], Remote]
//  ): Placed[F[Seq[V]], Local]
//
//object Multitier:
//  def placed[V, Local <: Node, Remote <: Node, F[_]](
//      body: => F[V]
//  )(using multitier: Multitier[F]): Placed[F[V], Remote] = multitier.placed(body)
//
//  def asLocal[V, Remote <: Node, Local <: Node { type Tie <: Single[Remote] }, F[_]](
//      effect: Placed[F[V], Remote]
//  )(using multitier: Multitier[F]): Placed[F[V], Local] = multitier.asLocal(effect)
//
//  def asLocalAll[V, Remote <: Node, Local <: Node { type Tie <: Multiple[Remote] }, F[_]](
//      effect: Placed[F[V], Remote]
//  )(using multitier: Multitier[F]): Placed[F[Seq[V]], Local] = multitier.asLocalAll(effect)
//
//  def multitier[V, F[_]](body: Multitier[F] ?=> V): F[V] = ???
//
//trait Choreography[F[_, _ <: Node]]:
//  def comm[V, Remote <: Node, Local <: Node { type Tie <: Cardinality[Remote] }](effect: F[V, Remote]): F[V, Local]
//
//object Choreography:
//  def comm[V, Remote <: Node, Local <: Node { type Tie <: Cardinality[Remote] }, F[_, _ <: Node]](
//      effect: F[V, Remote]
//  )(using choreography: Choreography[F]): F[V, Local] = choreography.comm(effect)
//
//  def choreography[V, F[_, _ <: Node]](body: Choreography[F] ?=> V): F[V, Node] = ???
//
//object Test:
//  type Client <: Node { type Tie <: Single[Server] }
//  type Server <: Node { type Tie <: Single[Client] }
//
//  import Placement.*
//  import Multitier.*
//
//  def foo[P[_]: {Monad, Placement, Multitier}](rem: P[Double]): Placed[P[Double], Client] =
//    val g: Placed[P[Int], Client] = on(12)
//    val t: Placed[P[Int], Client] = placed:
//      for r <- on(12)
//      yield r + 1
//    val p: Placed[P[Double], Client] = asLocal(rem)
//    ???
