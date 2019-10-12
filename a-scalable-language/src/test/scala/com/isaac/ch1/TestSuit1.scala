package com.isaac.ch1

import org.scalatest.{FunSuite, Matchers}

class TestSuit1 extends FunSuite with Matchers{
  test("first test") {
    val xs = 1 to 3
    val it = xs.iterator
    //    eventually{ it.next() shouldBe 3}
  }
}
