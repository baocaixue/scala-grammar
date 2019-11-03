package com.isaac.ch7

import java.io.{FileNotFoundException, FileReader, IOException}

object ExceptionSimple {
  def main(args: Array[String]): Unit = {
    throwDemo(6)

    println(catchDemo("test.txt"))
  }

  @throws
  def throwDemo(n: Int) = if (n % 2 == 0) n /2 else throw new RuntimeException("n must be even")

  def catchDemo(filePath: String) = {
    try {
      val f = new FileReader(filePath)
      f
    } catch {
      case ex: FileNotFoundException => "File Not Found"
      case ex: IOException => "IO Exception"
    }
  }
}
