error id: file://<WORKSPACE>/core/src/io/github/locicope/Tes.scala:
file://<WORKSPACE>/core/src/io/github/locicope/Tes.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1130
uri: file://<WORKSPACE>/core/src/io/github/locicope/Tes.scala
text:
```scala
import language.experimental.captureChecking
import caps.*
import scala.annotation.implicitNotFound
import scala.util.NotGiven

trait Ctx extends Capability

trait Foo:
  def placed[Value](using
    @implicitNotFound("Nested call are not supported for soundness reason") _ng: NotGiven[Ctx]
  )[Eff^](body: Ctx ?->{Eff} Value): Value = ???

object Foo:
  def placed[V](using
      f: Foo,
      @implicitNotFound("Nested call are not supported for soundness reason") _ng: NotGiven[Ctx]
  )[Eff^](body: Ctx ?->{Eff} V): V = f.placed(body)
  
  class Bar extends Capability:
    def bar: Unit = ()

  def bar: Unit =
    given f: Foo = new Foo() {}
    val b = Bar()
    val b1 = Bar()
    placed[Int]:
      placed:
        42.0
      println("fooo")
      b.bar
      42
    // This should work with capture checking enabled

trait Label extends Capability:
  type Fv^ // the capability set occurring freely in the `block` passed to `boundary` below.

def boundary[T, C^](block: Label ?->{C} T): T = ??? // ensure free caps of label and block match
def suspend[U](label: Label)[D^ <: {label.Fv}](handler: () ->{D} U): U = ??? // ma@@y only capture the free capabilities of label

def test =
  val x = 1
  class Bar extends Capability:
    def bar: Unit = ()
  val rt = Bar()
  boundary:
    val y = 2
    rt.bar
//    boundary: inner =>
//      val z = 3
//      rt.bar
//      val w = suspend(outer) {() => z} // ok
//      val v = suspend(inner) {() => y} // ok
//      val u = suspend(inner): () =>
//        suspend(outer) {() => w + v} // ok
//        y
//      suspend(outer): () =>
////        println(inner) // error (would leak the inner label)
//        x + y + z
```


#### Short summary: 

empty definition using pc, found symbol in pc: 