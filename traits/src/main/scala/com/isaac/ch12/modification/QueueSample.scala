package com.isaac.ch12.modification

object QueueSample {

  def basicQueue(): Unit = {
    val queue = new BasicIntQueue
    queue.put(10)
    queue.put(20)

    println("basicQueue: " + queue.get() + "\t" + queue.get())
  }

  def doublingQueue() = {
    val queue = new BasicIntQueue with Doubling
    queue.put(10)
    println("doublingQueue: " + queue.get())
  }

  def incAndFilterAndDouble(): Unit = {
    val queue = new BasicIntQueue with Doubling with Filtering with Incrementing
    queue.put(-2)
    queue.put(-1)
    queue.put(0)

    println("-2 -1 0 inc filter doubling: " + queue.get() + "\t" + queue.get() + "\t" )
  }

  def main(args: Array[String]): Unit = {
    basicQueue()
    doublingQueue()
    incAndFilterAndDouble();
  }
}
