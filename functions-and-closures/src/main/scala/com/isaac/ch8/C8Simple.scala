package com.isaac.ch8

object C8Simple {
  def main(args: Array[String]): Unit = {
    val demo = (x: Int) => x + 1
    println(demo(12))

    println(test(demo))

    filterSimple()

    closures()
  }

  def test(function1: (Int) => Int)= {
    function1.apply(1)
  }

  def filterSimple() = {
    val list = List(1,2,3,4,5,6,7,8,9)
    list.filter(_>5).foreach(println)

    val a = sum _
    a(1,2,3)
  }

  def sum(a: Int, b: Int, c: Int) = a + b + c

  def closures()={
    val more = 1
    val c = (x: Int) => x + more
    println(c(9))
  }
}
