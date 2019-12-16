package com.isaac.ch14

import org.scalatest.FunSuite
import Element.elem

class ElementSuite extends FunSuite{
  test("elem result should have passed width") {
    val ele = elem('x', 2, 3)
    assert(ele.width == 2)
  }
}
