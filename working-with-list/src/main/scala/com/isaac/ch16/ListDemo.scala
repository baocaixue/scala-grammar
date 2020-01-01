package com.isaac.ch16

object ListDemo {
  def main(args: Array[String]): Unit = {
    constructList()
    basicOperationOnList()
    listPattern()
  }

  def constructList() = {
    println(Nil == List())
    var fruit = "apple" :: "orange" :: "pears" :: Nil
    fruit = "lemon" :: fruit
    println(fruit)
  }

  def sort(list: List[Int]): List[Int] = {
    if (list.isEmpty) Nil
    else insert(list.head, sort(list.tail))
  }

  def insert(head: Int, sortedTail: List[Int]): List[Int] = {
    if (sortedTail.isEmpty || head <= sortedTail.head) head :: sortedTail
    else sortedTail.head :: insert(head, sortedTail.tail)
  }

  def basicOperationOnList() = {
    val list = List(1, 4, 3, 2, 7, 5, 6, 8, 9, 0)
    val sorted = sort(list)
    println(sorted)
  }

  def listPattern() = {
    val list = List(1, 4, 3, 2, 7, 5, 6, 8, 9, 0)
    val sorted = sort1(list)
    println(sorted)
  }

  def sort1(list: List[Int]): List[Int] = list match {
    case List() => List()
    case head :: tail => insert1(head, sort1(tail))
  }

  def insert1(head: Int, sortedTail: List[Int]): List[Int] = sortedTail match {
    case List() => List(head)
    case sortedTailHead :: sortedTailTail => if (head < sortedTailHead) head :: sortedTail else sortedTailHead :: insert1(head, sortedTailTail)
  }
}
