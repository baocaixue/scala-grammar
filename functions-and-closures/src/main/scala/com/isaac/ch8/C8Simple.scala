package com.isaac.ch8

object C8Simple {
  def main(args: Array[String]): Unit = {
    val demo = (x: Int) => x + 1
    println(demo(12))

    println(test(demo))

    filterSimple()

    closures()

    echo("1", "2")

    println(speed(distance = 128, time = 10))

    printTime()

//    boom(3)

    bang(3)
  }

  def test(function1: (Int) => Int)= {
    function1.apply(1)
  }

  def filterSimple(list: List[Int]=List(1,2,3,4,5,6,7,8,9)) = {
    //val list = List(1,2,3,4,5,6,7,8,9)
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

  def echo(args: String*)= {
    args.foreach(arg => print(arg + "\t"))
    println()
  }

  def speed(distance: Float, time: Float): Float = distance / time

  def printTime(out: java.io.PrintStream = Console.out) = out.println("time = " + System.currentTimeMillis())


  def boom(x: Int): Int =
    if (x == 0) throw new Exception("boom!")
    else boom(x -1) + 1

  def bang(x: Int): Int =
    if (x == 0) throw new Exception("bang!")
    else bang(x - 1)
}
