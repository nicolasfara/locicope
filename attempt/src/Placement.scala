import Peer.{Peer, TiedToMultiple, TiedToSingle}
import Placement.on

import scala.util.NotGiven

trait Placement[-P <: Peer]:
  trait PlacedScope

  def asLocal[V, Remote <: Peer, Local <: P & TiedToSingle[Remote]](using PlacedScope)(placed: V on Remote): V
  def asLocalAll[V, Remote <: Peer, Local <: P & TiedToMultiple[Remote]](using PlacedScope)(placed: V on Remote): Seq[V]
  def placed[V](body: PlacedScope ?=> V)(using NotGiven[PlacedScope]): V on P

object Placement:
  infix opaque type on[+V, -P <: Peer] = PlacedValue[V, P]

  inline given remotePlacedTag: [P <: Peer] => NotGiven[Placement[P]] => Placement[P] =
    new PlacementImpl[P](isRemote = true)

  private enum PlacedValue[+V, -P <: Peer]:
    case Remote(resourceReference: String)
    case Local(value: V, resourceReference: String)

  def asLocal[V, Remote <: Peer, Local <: TiedToSingle[Remote]](using
      p: Placement[Local],
      ps: p.PlacedScope
  )(placed: V on Remote): V =
    p.asLocal[V, Remote, Local](placed)

  def asLocalAll[V, Remote <: Peer, Local <: TiedToMultiple[Remote]](using
      p: Placement[Local],
      ps: p.PlacedScope
  )(placed: V on Remote): Seq[V] =
    p.asLocalAll[V, Remote, Local](placed)

  def placed[Local <: Peer](using
      p: Placement[Local],
      n: NotGiven[p.PlacedScope]
  )[V](body: p.PlacedScope ?=> V): V on Local =
    p.placed(body)

  def multitier[P <: Peer, V](program: Placement[P] ?=> V): V =
    given Placement[P] = new PlacementImpl[P](isRemote = false)
    ???

  private class PlacementImpl[-P <: Peer](private val isRemote: Boolean) extends Placement[P]:
    def asLocal[V, Remote <: Peer, Local <: P & TiedToSingle[Remote]](using PlacedScope)(placed: V on Remote): V = ???
    def asLocalAll[V, Remote <: Peer, Local <: P & TiedToMultiple[Remote]](using
        PlacedScope
    )(placed: V on Remote): Seq[V] = ???
    def placed[V](body: PlacedScope ?=> V)(using NotGiven[PlacedScope]): V on P = ???
