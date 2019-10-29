package com.isaac.ch6

object RunTest extends App {
  implicit def intToRational(x: Int):Rational = new Rational(x)
  def x = new Rational(1,2)
  def y = new Rational(2,3)
  println(x + y)
  println(x - y)
  println(x * 3)
  println(3 * x)
  println(y / 2)
}
