error id: XWBiUkWYsQEYYlEKvEAm3g==
### Bloop error:

Unexpected error when compiling core: java.lang.AssertionError: assertion failed: non-empty constraint at end of inlining:  uninstantiated variables: V, P, V, P
 constrained types:
  [V, P <: io.github.locicope.Peers.Peer]
    (value: V, peerName: String)(using evidence$1: io.circe.Encoder[V]): V on P
    ,
  [V, P <: io.github.locicope.Peers.Peer]
    (value: V, peerName: String)(using evidence$1: io.circe.Encoder[V]): V on P
 bounds:
     V
     P <: io.github.locicope.Peers.Peer
     V
     P <: io.github.locicope.Peers.Peer
 ordering:
 co-deps:
 contra-deps:
, ownedVars = V, P, V, P
	at scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:8)
	at dotty.tools.dotc.transform.TreeChecker.check(TreeChecker.scala:119)
	at dotty.tools.dotc.transform.TreeChecker.run(TreeChecker.scala:111)
	at dotty.tools.dotc.core.Phases$Phase.runOn$$anonfun$1(Phases.scala:383)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	at scala.collection.immutable.List.foreach(List.scala:334)
	at dotty.tools.dotc.core.Phases$Phase.runOn(Phases.scala:376)
	at dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:368)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	at scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1324)
	at dotty.tools.dotc.Run.runPhases$1(Run.scala:361)
	at dotty.tools.dotc.Run.compileUnits$$anonfun$1$$anonfun$2(Run.scala:408)
	at dotty.tools.dotc.Run.compileUnits$$anonfun$1$$anonfun$adapted$1(Run.scala:408)
	at scala.Function0.apply$mcV$sp(Function0.scala:42)
	at dotty.tools.dotc.Run.showProgress(Run.scala:470)
	at dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:408)
	at dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:420)
	at dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	at dotty.tools.dotc.Run.compileUnits(Run.scala:420)
	at dotty.tools.dotc.Run.compileSources(Run.scala:307)
	at dotty.tools.dotc.Run.compile(Run.scala:292)
	at dotty.tools.dotc.Driver.doCompile(Driver.scala:37)
	at dotty.tools.xsbt.CompilerBridgeDriver.run(CompilerBridgeDriver.java:141)
	at dotty.tools.xsbt.CompilerBridge.run(CompilerBridge.java:22)
	at sbt.internal.inc.AnalyzingCompiler.compile(AnalyzingCompiler.scala:91)
	at sbt.internal.inc.bloop.internal.BloopHighLevelCompiler.compileSources$1(BloopHighLevelCompiler.scala:148)
	at sbt.internal.inc.bloop.internal.BloopHighLevelCompiler.$anonfun$compile$9(BloopHighLevelCompiler.scala:181)
	at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.java:23)
	at sbt.internal.inc.bloop.internal.BloopHighLevelCompiler.$anonfun$compile$1(BloopHighLevelCompiler.scala:73)
	at bloop.tracing.NoopTracer$.trace(BraveTracer.scala:53)
	at sbt.internal.inc.bloop.internal.BloopHighLevelCompiler.timed$1(BloopHighLevelCompiler.scala:72)
	at sbt.internal.inc.bloop.internal.BloopHighLevelCompiler.$anonfun$compile$8(BloopHighLevelCompiler.scala:181)
	at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.java:23)
	at monix.eval.internal.TaskRunLoop$.startFull(TaskRunLoop.scala:81)
	at monix.eval.internal.TaskRestartCallback.syncOnSuccess(TaskRestartCallback.scala:101)
	at monix.eval.internal.TaskRestartCallback.onSuccess(TaskRestartCallback.scala:74)
	at monix.eval.internal.TaskExecuteOn$AsyncRegister$$anon$1.run(TaskExecuteOn.scala:71)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1423)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188)
#### Short summary: 

Unexpected error when compiling core: java.lang.AssertionError: assertion failed: non-empty constrai...