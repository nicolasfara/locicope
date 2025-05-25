package io.github.locicope

class LociMain:
  def sum(a: Int, b: Int): Int =
    a + b * 2

@main def main(): Unit =
  val lociMain = new LociMain()
  println(s"Hello ${lociMain.sum(1, 2)}")
