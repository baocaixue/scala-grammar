package com.isaac.ch7

object MultiTable {
  def main(args: Array[String]): Unit = {
    printMultiTable()

    println()

    println(multiTable())
  }

  def printMultiTable() = {
    var i = 1
    //只有i在作用域内
    while (i <= 10) {
      var j = 1
      //i和j在作用域内
      while (j <= 10) {
        val prod = (i * j).toString
        //i、j和prod在作用域内
        var k = prod.length()
        //i、j、prod和k在作用域内
        while (k < 4) {
          print(" ")
          k += 1
        }
        print(prod)
        j += 1
      }
      //i和j仍在作用域内，prod和k超出了作用域
      println()
      i += 1
    }
    //i仍在作用域内，j、prod和k超出了作用域
  }

  def makeRowSeq(row: Int) =
    for (col <- 1 to 10) yield {
      val prod = (row * col).toString
      val padding = " " * (4 - prod.length)
      padding + prod
    }

  def makeRow(row: Int) = makeRowSeq(row).mkString

  def multiTable() = {
    val tableSeq =
      for (row <-1 to 10)
        yield makeRow(row)
    tableSeq.mkString("\n")
  }
}
