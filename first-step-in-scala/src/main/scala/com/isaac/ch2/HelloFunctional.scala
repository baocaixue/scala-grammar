package com.isaac.ch2

object HelloFunctional {
  def main(args: Array[String]): Unit ={
    val arr = Array(1,2,3,4,5)
    arr.foreach(number=> println(number))
    arr.foreach(println)
    for (a <- arr)
      println(a)
  }
}
