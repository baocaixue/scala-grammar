package com.isaac.ch16

object ListDemo {
  def main(args: Array[String]): Unit = {
    constructList()
  }

  def constructList() = {
    println(Nil == List())
    var fruit = "apple" :: "orange" :: "pears" :: Nil
    fruit = "lemon" :: fruit
    println(fruit)
  }
}
