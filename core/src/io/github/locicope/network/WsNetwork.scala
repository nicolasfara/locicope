package io.github.locicope.network

import io.circe.{Decoder, Encoder}
import ox.*
import ox.flow.Flow
import sttp.client4.impl.ox.ws.asSourceAndSink
import sttp.client4.ws.SyncWebSocket
import sttp.client4.{DefaultSyncBackend, UriContext, basicRequest}
import sttp.tapir.*
import sttp.tapir.server.netty.sync.OxStreams
import sttp.tapir.server.netty.sync.OxStreams.Pipe
import sttp.tapir.server.netty.sync.NettySyncServer
import sttp.ws.WebSocketFrame
import sttp.client4.circe.*
import sttp.tapir.json.circe.*
import io.circe.syntax.*
import io.circe.parser.decode
import io.github.locicope.Multitier.PlacedFunction
import io.github.locicope.Peers.PeerRepr
import io.github.locicope.network.Reference.ResourceReference
import ox.resilience.{RetryConfig, retry}

import scala.concurrent.duration.DurationInt

class WsNetwork(
    private val singleTied: Map[PeerRepr, (String, Int)],
    private val multiTied: Map[PeerRepr, Set[(String, Int)]],
    private val port: Int = 8080
) extends Network:
  private val flowResources = collection.concurrent.TrieMap[String, Flow[Any]]()
  private val valueResources = collection.concurrent.TrieMap[String, String]() // Already encoded
  private val httpEndpoint = endpoint.get
    .in("values")
    .in(query[String]("path"))
    .out(stringBody)
  private val wsEndpoint = endpoint.get
    .in("flows")
    .out(webSocketBody[String, CodecFormat.TextPlain, String, CodecFormat.Json](OxStreams))
  private def flowRequestPipe[V]: Pipe[String, V] = requestedPath =>
    requestedPath.flatMap(
      flowResources.getOrElse(_, Flow.failed(new Exception("Flow not found"))).asInstanceOf[Flow[V]]
    )
  private val wsServerEndpoint = wsEndpoint.handleSuccess(_ => flowRequestPipe)
  private val httpServerEndpoint = httpEndpoint
    .handleSuccess(path => valueResources.getOrElse(path, throw new Exception("Value not found")))
  private val backend = DefaultSyncBackend()

  override def startNetwork(): Unit = supervised:
    NettySyncServer()
      .host("localhost")
      .port(port)
      .addEndpoints(List(wsServerEndpoint, httpServerEndpoint))
      .start()

  private def useWebSocket[V: Decoder](ws: SyncWebSocket): Flow[V] = supervised:
    val (wsSource, _) = asSourceAndSink(ws)
    Flow.fromSource(wsSource.map {
      case WebSocketFrame.Text(text, _, _) => decode[V](text).getOrElse(throw Exception("Invalid JSON"))
      case _                               => throw new Exception("Invalid WebSocket frame")
    })

//  override def registerFlowResult[V](produced: ResourceReference, value: Flow[V]): Unit =
//    flowResources(produced.index) = value

  private def requestPeer[V: Decoder](ip: String, port: Int, request: ResourceReference)(using Ox): Option[V] = fork:
    retry(RetryConfig.backoff(10, 500.milliseconds)):
      val result = basicRequest
        .get(uri"http://$ip:$port/values?path=${request.resourceId}")
        .response(asJson[V])
        .send(backend)
      result.body.fold(
        error => throw Exception("Error in request: " + error),
        value => Some(value)
      )
  .join()

  override def receiveFrom[V: Decoder](from: ResourceReference): V = supervised:
    singleTied
      .find(_._1 <:< from.onPeer)
      .flatMap { case (_, (ip, port)) => requestPeer(ip, port, from) }
      .getOrElse(throw new Exception(s"Possible no tie to ${from.onPeer}"))

  override def receiveFromAll[V: Decoder](from: ResourceReference): Seq[V] = supervised:
    multiTied
      .find(_._1 <:< from.onPeer)
      .map { case (_, ips) => par(ips.map((ip, port) => () => requestPeer(ip, port, from)).toSeq).flatten }
      .getOrElse(throw new Exception(s"Possible no tie to ${from.onPeer}"))

//  override def receiveFlowFrom[V: Decoder](from: ResourceReference)(using Ox): Flow[V] =
//    singleTied
//      .get(from.peerName)
//      .map: (ip, port) =>
//        val response = basicRequest
//          .body(from.index.toString)
//          .get(uri"ws://$ip:$port/flows?path")
//          .response(asWebSocket(useWebSocket))
//          .send(backend)
//          .body
//        response match
//          case Left(error) => Flow.failed(new Exception(s"WebSocket connection failed: $error"))
//          case Right(ws)   => ws
//      .getOrElse(throw new Exception(s"Possible no tie to ${from.peerName}"))

  override def registerValue[V: Encoder](produced: ResourceReference, value: V): Unit =
    valueResources(produced.resourceId) = value.asJson.toString

  override def registerPlaced[V: Encoder](produced: ResourceReference, value: V): Unit =
    registerValue(produced, value)

  override def registerFunction[In <: Product: Encoder, Out: Encoder](function: PlacedFunction[?, In, Out]): Unit = ???

  override def callFunction[In <: Product: Encoder, Out: Decoder](inputs: In, ref: ResourceReference): Out =
    scribe.info(s"Hey, we are calling the function ${ref.resourceId} with inputs: ${inputs.asJson}")
    ???

  override def receiveFromFlow[V: Decoder](from: ResourceReference): Flow[V] = ???

  override def receiveFromFlowAll[V: Decoder](from: ResourceReference): Flow[V] = ???

  override def registerFlow[V](produced: ResourceReference, value: Flow[V]): Unit = ???
