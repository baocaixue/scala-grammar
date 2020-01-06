package com.isaac.ch16

object ListDemo {
  def main(args: Array[String]): Unit = {
    constructList()
    basicOperationOnList()
    listPattern()
    firstOrderMethods()
    highOrderMethods()
    multipleList()
  }

  def constructList() = {
    println(Nil == List())
    var fruit = "apple" :: "orange" :: "pears" :: Nil
    fruit = "lemon" :: fruit
    println(fruit)
  }

  def basicOperationOnList() = {
    def sort(list: List[Int]): List[Int] = {
      if (list.isEmpty) Nil
      else insert(list.head, sort(list.tail))
    }

    def insert(head: Int, sortedTail: List[Int]): List[Int] = {
      if (sortedTail.isEmpty || head <= sortedTail.head) head :: sortedTail
      else sortedTail.head :: insert(head, sortedTail.tail)
    }
    val list = List(1, 4, 3, 2, 7, 5, 6, 8, 9, 0)
    val sorted = sort(list)
    println(sorted)
  }

  def listPattern() = {
    def sort1(list: List[Int]): List[Int] = list match {
      case List() => List()
      case head :: tail => insert1(head, sort1(tail))
    }

    def insert1(head: Int, sortedTail: List[Int]): List[Int] = sortedTail match {
      case List() => List(head)
      case sortedTailHead :: sortedTailTail => if (head < sortedTailHead) head :: sortedTail else sortedTailHead :: insert1(head, sortedTailTail)
    }
    val list = List(1, 4, 3, 2, 7, 5, 6, 8, 9, 0)
    val sorted = sort1(list)
    println(sorted)
  }


  def firstOrderMethods() = {
    //bad rev O(n) = (1 + n) * n / 2
    def rev[T](xs: List[T]): List[T] = xs match {
      case List() => xs
      case head :: tail => rev(tail) ::: List(head)
    }

    val list = List(1, 2, 3, 4, 5)
    println(rev(list))
    println(list drop 2)
    println(list take 2)
    println(list splitAt 2)
    println(list indices)

    val fruits = List("apple", "orange", "pears")
    val flatten = fruits.flatMap(_.toCharArray)//map(_.toCharArray).flatten
    println(flatten)

    println(list zip fruits)

    val string = fruits mkString ("fruit: ", "\t", " end")
    println(string)

    val buf = new StringBuilder
    val result = fruits addString(buf, "(", ";", ")")
    println(result)

    val li = List(1, 4, 3, 2, 7, 5, 6, 8, 9, 0)
    val sorted = msort((x: Int, y: Int) => x < y)(li)
    println(sorted)
  }

  def highOrderMethods()= {
    //1 <= j < i <5 (i,j)
    val tuple = List.range(1, 5).flatMap(i => List.range(1, i).map(j => (j, i)))
    println(tuple)

    val nums = List(1, 2, -1, 3, 4, 5, 0)
    val positives = nums takeWhile (_ > 0)
    println(positives)
    println(nums span (_ > 0))
    println(reverseLeft(nums))
  }

  def multipleList(): Unit = {
    val list1 = List(1, 2, 3)
    val list2 = List(10, 20)
    println((list1, list2).zipped.map(_ * _))
    println((list1 zip list2).map(item => item._1 * item._2))
  }
}
