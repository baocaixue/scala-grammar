package com.isaac.ch14

import org.scalatest.{DiagrammedAssertions, FunSuite}
import Element.elem

class ElementSuite extends FunSuite with DiagrammedAssertions {
  test("elem result should have passed width") {
    val ele = elem('x', 2, 3)
    assert(ele.width == 2)
    //assert(List(1,2,3).contains(4))
    assertResult(2){
      ele.width
    }

    assertThrows[IllegalArgumentException] {
      elem('x', -2, 3)
    }
    val caught =
      intercept[ArithmeticException] {
        1 / 0
      }
    assert(caught.getMessage == "/ by zero")
  }
}
