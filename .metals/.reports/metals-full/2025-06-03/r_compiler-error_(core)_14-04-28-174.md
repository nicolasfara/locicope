error id: F208A9652793FD1DB9DA9E4DD8AB067B
file://<WORKSPACE>/core/src/io/github/locicope/Tes.scala
### java.lang.AssertionError: assertion failed: attempt to parse java.lang.Object from classfile

occurred in the presentation compiler.



action parameters:
offset: 298
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
  )[Eff^@@](body: Ctx ?->{Eff} Value): Value = ???

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
    placed:
      42.0
    placed[Int]:
      println("fooo")
      b.bar
      42
    // This should work with capture checking enabled

trait Label extends Capability:
  type Fv^ // the capability set occurring freely in the `block` passed to `boundary` below.

def boundary[T, C^](block: Label ?->{C} T): T = ??? // ensure free caps of label and block match
def suspend[U](label: Label)[D^ <: {label.Fv}](handler: () ->{D} U): U = ??? // may only capture the free capabilities of label

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


presentation compiler configuration:
Scala version: 3.7.2-RC1-bin-20250528-457a463-NIGHTLY-nonbootstrapped
Classpath:
<WORKSPACE>/.bloop/out/core/bloop-bsp-clients-classes/classes-Metals-aBkgk10OQ4aVu7Wb8IiTqQ== [exists ], <HOME>/.cache/bloop/semanticdb/com.sourcegraph.semanticdb-javac.0.10.4/semanticdb-javac-0.10.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.7.2-RC1-bin-20250528-457a463-NIGHTLY/scala3-library_3-3.7.2-RC1-bin-20250528-457a463-NIGHTLY.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala2-library-cc-tasty-experimental_3/3.7.2-RC1-bin-20250528-457a463-NIGHTLY/scala2-library-cc-tasty-experimental_3-3.7.2-RC1-bin-20250528-457a463-NIGHTLY.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/ox/core_3/0.5.13/core_3-0.5.13.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/client4/ox_3/4.0.7/ox_3-4.0.7.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/client4/circe_3/4.0.7/circe_3-4.0.7.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-core_3/1.11.29/tapir-core_3-1.11.29.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-json-circe_3/1.11.29/tapir-json-circe_3-1.11.29.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-netty-server-sync_3/1.11.29/tapir-netty-server-sync_3-1.11.29.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.16/scala-library-2.13.16.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/jox/channels/0.4.0/channels-0.4.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/client4/core_3/4.0.7/core_3-4.0.7.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/client4/json-common_3/4.0.7/json-common_3-4.0.7.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-core_3/0.14.13/circe-core_3-0.14.13.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-parser_3/0.14.13/circe-parser_3-0.14.13.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/model/core_3/1.7.14/core_3-1.7.14.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/shared/core_3/1.5.0/core_3-1.5.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/shared/ws_3/1.5.0/ws_3-1.5.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/magnolia1_3/magnolia_3/1.3.16/magnolia_3-1.3.16.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-generic_3/0.14.13/circe-generic_3-0.14.13.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-netty-server_3/1.11.29/tapir-netty-server_3-1.11.29.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-numbers_3/0.14.13/circe-numbers_3-0.14.13.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-core_3/2.13.0/cats-core_3-2.13.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-jawn_3/0.14.13/circe-jawn_3-0.14.13.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-server_3/1.11.29/tapir-server_3-1.11.29.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-all/4.2.0.Final/netty-all-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/playframework/netty/netty-reactive-streams-http/3.0.4/netty-reactive-streams-http-3.0.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-kernel_3/2.13.0/cats-kernel_3-2.13.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/jawn-parser_3/1.6.0/jawn-parser_3-1.6.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-buffer/4.2.0.Final/netty-buffer-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-base/4.2.0.Final/netty-codec-base-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec/4.2.0.Final/netty-codec-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-dns/4.2.0.Final/netty-codec-dns-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-haproxy/4.2.0.Final/netty-codec-haproxy-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-compression/4.2.0.Final/netty-codec-compression-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-http/4.2.0.Final/netty-codec-http-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-http2/4.2.0.Final/netty-codec-http2-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-memcache/4.2.0.Final/netty-codec-memcache-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-mqtt/4.2.0.Final/netty-codec-mqtt-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-redis/4.2.0.Final/netty-codec-redis-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-smtp/4.2.0.Final/netty-codec-smtp-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-socks/4.2.0.Final/netty-codec-socks-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-stomp/4.2.0.Final/netty-codec-stomp-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-xml/4.2.0.Final/netty-codec-xml-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-protobuf/4.2.0.Final/netty-codec-protobuf-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-codec-marshalling/4.2.0.Final/netty-codec-marshalling-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-common/4.2.0.Final/netty-common-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-handler/4.2.0.Final/netty-handler-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-native-unix-common/4.2.0.Final/netty-transport-native-unix-common-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-handler-proxy/4.2.0.Final/netty-handler-proxy-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-handler-ssl-ocsp/4.2.0.Final/netty-handler-ssl-ocsp-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-resolver/4.2.0.Final/netty-resolver-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-resolver-dns/4.2.0.Final/netty-resolver-dns-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport/4.2.0.Final/netty-transport-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-rxtx/4.2.0.Final/netty-transport-rxtx-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-sctp/4.2.0.Final/netty-transport-sctp-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-udt/4.2.0.Final/netty-transport-udt-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-classes-epoll/4.2.0.Final/netty-transport-classes-epoll-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-classes-kqueue/4.2.0.Final/netty-transport-classes-kqueue-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-resolver-dns-classes-macos/4.2.0.Final/netty-resolver-dns-classes-macos-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/netty/netty-transport-classes-io_uring/4.2.0.Final/netty-transport-classes-io_uring-4.2.0.Final.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/playframework/netty/netty-reactive-streams/3.0.4/netty-reactive-streams-3.0.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/reactivestreams/reactive-streams/1.0.4/reactive-streams-1.0.4.jar [exists ], <WORKSPACE>/core/compile-resources [missing ], <WORKSPACE>/.bloop/out/core/bloop-bsp-clients-classes/classes-Metals-aBkgk10OQ4aVu7Wb8IiTqQ==/META-INF/best-effort [missing ]
Options:
-Werror -rewrite -indent -unchecked -explain -Xcheck-macros -Xprint:cc -Ycheck:all -Ycc-debug -experimental -feature -language:implicitConversions -language:experimental.captureChecking language:experimental.pureFunctions -Xsemanticdb -sourceroot <WORKSPACE> -Ywith-best-effort-tasty




#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:8)
	dotty.tools.dotc.core.classfile.ClassfileParser$AbstractConstantPool.getSuperClass(ClassfileParser.scala:175)
	dotty.tools.dotc.core.classfile.ClassfileParser.parseParents$1(ClassfileParser.scala:380)
	dotty.tools.dotc.core.classfile.ClassfileParser.parseClass(ClassfileParser.scala:389)
	dotty.tools.dotc.core.classfile.ClassfileParser.$anonfun$1(ClassfileParser.scala:302)
	dotty.tools.dotc.core.classfile.ClassfileParser.run(ClassfileParser.scala:297)
	dotty.tools.dotc.core.ClassfileLoader.doComplete(SymbolLoaders.scala:471)
	dotty.tools.dotc.core.SymbolLoader$$anon$1.doComplete(SymbolLoaders.scala:378)
	dotty.tools.dotc.core.SymbolLoader.complete(SymbolLoaders.scala:402)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeOnce(SymDenotations.scala:385)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.isAbsent(SymDenotations.scala:615)
	dotty.tools.dotc.interactive.Completion$.isValidCompletionSymbol(Completion.scala:338)
	dotty.tools.dotc.interactive.Completion$Completer.dotty$tools$dotc$interactive$Completion$Completer$$include(Completion.scala:654)
	dotty.tools.dotc.interactive.Completion$Completer$$anon$5.applyOrElse(Completion.scala:683)
	dotty.tools.dotc.interactive.Completion$Completer$$anon$5.applyOrElse(Completion.scala:682)
	scala.collection.immutable.List.collect(List.scala:276)
	scala.collection.immutable.List.collect(List.scala:79)
	dotty.tools.dotc.interactive.Completion$Completer.accessibleMembers(Completion.scala:684)
	dotty.tools.dotc.interactive.Completion$Completer.scopeCompletions$lzyINIT1$$anonfun$1(Completion.scala:410)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	dotty.tools.dotc.core.Contexts$Context$$anon$2.foreach(Contexts.scala:135)
	dotty.tools.dotc.interactive.Completion$Completer.scopeCompletions$lzyINIT1(Completion.scala:400)
	dotty.tools.dotc.interactive.Completion$Completer.scopeCompletions(Completion.scala:390)
	dotty.tools.dotc.interactive.Completion$.scopeContext(Completion.scala:59)
	dotty.tools.pc.IndexedContext$LazyWrapper.<init>(IndexedContext.scala:91)
	dotty.tools.pc.IndexedContext$.apply(IndexedContext.scala:80)
	dotty.tools.pc.HoverProvider$.hover(HoverProvider.scala:52)
	dotty.tools.pc.ScalaPresentationCompiler.hover$$anonfun$1(ScalaPresentationCompiler.scala:436)
```
#### Short summary: 

java.lang.AssertionError: assertion failed: attempt to parse java.lang.Object from classfile