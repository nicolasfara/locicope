package io.github.locicope

import io.github.locicope.Multitier.Placement
import io.github.locicope.Multitier.Placement.*
import io.github.locicope.Peers.{Peer, PlacedAt}
import io.github.locicope.Peers.Quantifier.Single
import ox.*
import io.circe.generic.auto.*
import io.github.locicope.network.WsNetwork

object EmailFetching:
  type Client <: { type Tie <: Single[Server] }
  type Server <: { type Tie <: Single[Client] }

  case class ClientId(id: String)
  case class TimeStamp(time: Long)
  case class Attachment(name: String)

  inline def clientId(using Placement) = on[Client](ClientId("12"))
  inline def lastClientTimeStamp(using Placement) = on[Client](TimeStamp(System.currentTimeMillis()))

  inline def savedEmailsForUsers(using Placement) = on[Server]:
    Map(
      "10" -> List((TimeStamp(124555L), "email1"), (TimeStamp(4854495L), "email2")),
      "12" -> List((TimeStamp(124543534534534555L), "email3"), (TimeStamp(485449534534543534L), "email4"))
    )

  inline def clientsWithFlatRateConnection(using Placement) = on[Server](List(ClientId("12")))

  private def since(emails: List[(EmailFetching.TimeStamp, String)], timeStamp: TimeStamp): List[String] =
    emails.filter { case (ts, _) => ts.time > timeStamp.time }.map(_._2)

  inline def emailFetchingProtocol[P <: Peer](using PlacedAt[P], Ox) =
    val tokenAndLastCheckout = placed[Client]((unwrap(clientId), unwrap(lastClientTimeStamp)))

    val unreadEmails = placed[Server]:
      val (clientId, lastClientTimeStamp) = asLocal(tokenAndLastCheckout)
      val serverDb = unwrap(savedEmailsForUsers)
      since(serverDb(clientId.id), lastClientTimeStamp)

    placed[Client](println(s"Client ${unwrap(clientId)} received: ${asLocal(unreadEmails)}"))

    val isOnFlatConnection = placed[Server]:
      val isOnLocal = unwrap(clientsWithFlatRateConnection).contains(asLocal(tokenAndLastCheckout)._1)
      val attachments = if isOnLocal then List(Attachment("attachment1"), Attachment("attachment2")) else List()
      (isOnLocal, attachments)

    placed[Client]:
      val (clientOnFlat, attachments) = asLocal(isOnFlatConnection)
      if clientOnFlat then println(s"Attachments: $attachments") else println("Not on a flat rate connection")
    ()

object EmailFetchingClient extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map("io.github.locicope.EmailFetching.Server" -> ("localhost", 8080)),
      Map(),
      port = 8081,
    )
    multitier[Unit, EmailFetching.Client](EmailFetching.emailFetchingProtocol)
    never

object EmailFetchingServer extends OxApp:
  override def run(args: Vector[String])(using Ox): ExitCode =
    given WsNetwork(
      Map("io.github.locicope.EmailFetching.Client" -> ("localhost", 8081)),
      Map(),
      port = 8080,
    )
    multitier[Unit, EmailFetching.Server](EmailFetching.emailFetchingProtocol)
    never