package com.isaac.ch9

object CurryingDemo {
  def plainOldSum(x: Int, y: Int) = x + y

  def curriedSum(x: Int)(y: Int) = x + y

  def main(args: Array[String]): Unit = {
    assert(plainOldSum(1,2) == curriedSum(1)(2))
  }
}
