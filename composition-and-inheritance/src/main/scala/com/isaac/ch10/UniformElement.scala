package com.isaac.ch10

class UniformElement(ch: Char, override val width: Int, override val height: Int) extends Element {
  private val line = ch.toString * width

  def contents: Array[String] = Array.fill(height)(line)
}
