package io.github.locicope.multiparty.serialization

trait Codec[T] extends Encoder[T], Decoder[T]
