package io.github.locicope.network

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
   * @param peerName
   *   the name of the peer where the resource is located.
   * @param index
   *   the index of the resource in the multitier application, used to differentiate between multiple resources of the
   *   same type on the same peer.
   * @param valueType
   *   description of the value type this resource holds, either a flow or a simple value.
   */
  final case class ResourceReference(peerName: String, index: Int, valueType: ValueType):
    override def toString: String = s"$peerName@$index[$valueType]"
