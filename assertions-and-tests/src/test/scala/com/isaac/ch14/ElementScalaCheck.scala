package com.isaac.ch14

import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks
import org.scalatest.MustMatchers._
import Element.elem

class ElementScalaCheck extends WordSpec with PropertyChecks{
  "elem result" must {
    "have passed width" in {
      forAll { w: Int =>
        whenever(w > 0) {
          elem('x', w, 3).width must equal (w)
        }
      }
    }
  }
}
