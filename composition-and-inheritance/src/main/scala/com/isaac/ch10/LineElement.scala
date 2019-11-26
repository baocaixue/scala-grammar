package com.isaac.ch10

class LineElement(s: String) extends Element {
  override def width: Int = s.length
  override def height: Int = 1
  val contents: Array[String] = Array(s)
}
