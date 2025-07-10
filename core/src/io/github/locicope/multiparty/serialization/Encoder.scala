package io.github.locicope.multiparty.serialization

trait Encoder[T]:
  def encode(value: T): Array[Byte]
