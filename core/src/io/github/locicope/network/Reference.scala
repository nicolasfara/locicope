package io.github.locicope.network

import io.github.locicope.Peers.PeerRepr

object Reference:
  /**
   * Type representing the value type of placed values.
   */
  enum ValueType:
    case Flow
    case Value

  /**
   * Identifier for a resource in the multitier application.
   *
   * @param resourceId
   *   the name of the peer where the resource is located.
   * @param valueType
   *   description of the value type this resource holds, either a flow or a simple value.
   */
  final case class ResourceReference(resourceId: String, onPeer: PeerRepr, valueType: ValueType):
    override def toString: String = s"$resourceId@$onPeer:${valueType.toString}"
