package io.github.locicope

import io.github.locicope.Multitier.{PlacedFunction, Placement, on}
import io.github.locicope.Multitier.Placement.*
import io.github.locicope.Peers.{Peer, peer}
import io.github.locicope.Peers.Quantifier.Single
import io.github.locicope.network.WsNetwork
import ox.{ExitCode, Ox, OxApp, never}

import scala.io.StdIn.readLine

object SimpleClientServer:
  type Node <: { type Tie <: Single[Node] }
  type Client <: Node { type Tie <: Single[Server] }
  type Server <: Node { type Tie <: Single[Client] }

  private def doubleItServer(using Placement) = function[Server][Int *: EmptyTuple, Int]:
    case input *: EmptyTuple => input * 2

  private def tripleItServer(using Placement) = function[Node][Int *: EmptyTuple, Int]:
    case input *: EmptyTuple => input * 3

  def multitierApp[P <: Peer](using p: Placement): Unit =
    placed(doubleItServer, tripleItServer)[Client]:
      val userInput = readLine("Enter an integer: ").toInt
      val result = doubleItServer(userInput *: EmptyTuple).asLocal
      println(s"Doubled on server: $result")
      val triple = tripleItServer(userInput *: EmptyTuple).asLocal
      println(s"Tripled on server: $triple")

object SimpleClientApp extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map(peer[SimpleClientServer.Server] -> ("localhost", 8080)),
      Map(),
      port = 8081
    )
    multitier[Unit, SimpleClientServer.Client](SimpleClientServer.multitierApp)
    never

object SimpleServerApp extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map(peer[SimpleClientServer.Client] -> ("localhost", 8081)),
      Map(),
      port = 8080
    )
    multitier[Unit, SimpleClientServer.Server](SimpleClientServer.multitierApp)
    never
