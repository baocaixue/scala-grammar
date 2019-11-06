package com.isaac.ch8

object C8Simple {
  def main(args: Array[String]): Unit = {
    val demo = (x: Int) => x + 1
    println(demo(12))

    println(test(demo))
  }

  def test(function1: (Int) => Int)= {
    function1.apply(1)
  }
}
