package com.isaac.ch7

import java.io.File

import scala.io.Source

object C7Simple extends App {
  println(ifTest(1))

  val files = new File(".").listFiles
  for (file <- files if file.getName.endsWith("xml")) {
    println(file.getName)
  }

  def ifTest(i: Int): String = if (i == 1) i.toString else "Default Value"

  //noinspection SourceNotClosed
  def fileLines(file: File): List[String] = Source.fromFile(file).getLines().toList

  def grep(pattern: String) =
    for {
      file <- files
      if file.getName.endsWith("xml") //notice me
      line <- fileLines(file)
      trimmed = line.trim
      if trimmed.matches(pattern)
    } println(file + ":" + trimmed)

  grep(".*scala.*")

  val li = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
  val result = for {
    l <- li
    if l > 5
  } yield {
    l + 1
  }
  println(result)
}
