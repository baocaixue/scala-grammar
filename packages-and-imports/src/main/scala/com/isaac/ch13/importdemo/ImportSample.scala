package com.isaac.ch13.importdemo

import com.isaac.ch13.importdemo.Fruits.{menu,Orange,Apple => McIntosh, Pear => _}

object ImportSample {
  val printFruitInfoTest: Fruit => Unit = fruit => println(fruit.color)

  def main(args: Array[String]): Unit = {
    printFruitInfoTest(menu.head)

    printFruitInfo(McIntosh)
  }

  def printFruitInfo(fruit: Fruit) = {
    import fruit._
    println(name + "s are " + color)
  }
}
