package io.github.locicope.multiparty.serialization

trait Decoder[T]:
  def decode(data: Array[Byte]): Either[String, T]
