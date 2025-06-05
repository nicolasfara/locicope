package io.github.locicope

import io.github.locicope.Multitier.{Placement, on}
import io.github.locicope.Multitier.Placement.*
import io.github.locicope.Peers.Peer
import io.github.locicope.Peers.Quantifier.Single
import io.github.locicope.network.WsNetwork
import ox.{ExitCode, Ox, OxApp, never}

object SimpleClientServer:
  type Node <: { type Tie <: Single[Node] }
  type Client <: Node { type Tie <: Single[Server] }
  type Server <: Node { type Tie <: Single[Client] }

  def multitierApp[P <: Peer](using Placement, Ox): Unit =
    val id: Int on Client = placed[Client]:
      12
    println(s"Client ID: $id")
    val serverValue: Int on Server = placed[Server]:
      val clientIdLocal = asLocal(id)
      clientIdLocal * 2
    placed[Client](println(s"Server processed value: ${asLocal(serverValue)}"))

object SimpleClientApp extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map("io.github.locicope.SimpleClientServer$.Server" -> ("localhost", 8080)),
      Map(),
      port = 8081
    )
    multitier[Unit, SimpleClientServer.Client](SimpleClientServer.multitierApp)
    never

object SimpleServerApp extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map("io.github.locicope.SimpleClientServer$.Client" -> ("localhost", 8081)),
      Map(),
      port = 8080
    )
    multitier[Unit, SimpleClientServer.Server](SimpleClientServer.multitierApp)
    never
