package com.isaac.ch17

import scala.collection.mutable

object Sample {
  def main(args: Array[String]): Unit = {
    useMutable()
    println(distinctWordsSize("See Spot run. Run, Spot. Run!"))
    println(countWords("See Spot run. Run, Spot. Run!"))
  }

  def useMutable() = {
    import scala.collection.mutable.{Set=>MSet}
    import scala.collection.immutable.{Set=>ImSet}
    val mset = MSet(1,2,3)
    val imset = ImSet(1, 2, 3)
    mset += 4
    val newSet = imset + 4
    println(mset)
    println(imset + "\t" + newSet)
  }

  def distinctWordsSize(text: String): Int = {
    val wordsArray = text.split("[ ! , .]+")
    val uniqueWords = mutable.Set.empty[String]
    wordsArray.foreach(uniqueWords += _.toLowerCase())
    uniqueWords.size
  }

  def countWords(text: String): mutable.Map[String, Int] = {
    val container = mutable.Map.empty[String, Int]
    text.split("[ ! , .]+").map(_.toLowerCase).foreach(word => {
      if (container.contains(word)) container(word) = container(word) + 1 else container(word) = 1
    })
    container
  }
}
