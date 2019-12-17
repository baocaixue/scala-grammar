package com.isaac.ch14

import org.scalatest.{FlatSpec, Matchers}
import Element.elem

/*
  用ScalaTest的FlatSpec描述并测试代码行为
 */
class ElementSpec extends FlatSpec with Matchers{
  "A UniformElement" should "have a width equal to the passed value" in {
    val ele = elem('x', 2, 3)
    ele.width should be (2)
  }

  it should "have a height equal to the passed value" in {
    val ele = elem('x', 2, 3)
    ele.height should be (3)
  }

  it should "throw an IAE if passed a negative width" in {
    an [IllegalArgumentException] should be thrownBy {
      elem('x', -2, 3)
    }
  }
}
