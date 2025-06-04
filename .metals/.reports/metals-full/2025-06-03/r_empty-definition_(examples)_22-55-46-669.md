file://<WORKSPACE>/examples/src/io/github/locicope/SimpleClientServer.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 764
uri: file://<WORKSPACE>/examples/src/io/github/locicope/SimpleClientServer.scala
text:
```scala
package io.github.locicope

import io.github.locicope.Multitier.on
import io.github.locicope.Multitier.Placement.*
import io.github.locicope.Peers.{Peer, PlacedAt}
import io.github.locicope.Peers.Quantifier.Single
import io.github.locicope.network.WsNetwork
import ox.{ExitCode, Ox, OxApp, never}

object SimpleClientServer:
  type Client <: { type Tie <: Single[Server] }
  type Server <: { type Tie <: Single[Client] }

  inline def multitierApp[P <: Peer](using PlacedAt[P], Ox) =
    val id = on[Client](12)
    println(s"Client ID: $id")
    val serverValue = placed[Server]:
      val clientIdLocal = asLocal(id)
      clientIdLocal * 2
    placed[Client](println(s"Server processed value: ${asLocal(serverValue)}"))

object SimpleClientApp extends OxApp:
  @@override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map("io.github.locicope.SimpleClientServer.Server" -> ("localhost", 8080)),
      Map(),
      port = 8081
    )
    multitier[Unit, SimpleClientServer.Client](SimpleClientServer.multitierApp)
    never

object SimpleServerApp extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map("io.github.locicope.SimpleClientServer.Client" -> ("localhost", 8081)),
      Map(),
      port = 8080
    )
    multitier[Unit, SimpleClientServer.Server](SimpleClientServer.multitierApp)
    never

```


#### Short summary: 

empty definition using pc, found symbol in pc: 