package com.isaac.ch5

object C5Simple extends App {
  stringInterpolation()
  operationMethod()

  def stringInterpolation(): Unit = {
    val name = "Isaac"
    println(s"Hello, $name !")

    println(s"No \\\\smoking")
    println(raw"No \\\\smoking")
    println(f"${math.Pi}%.2f")
  }

  def operationMethod(): Unit = {
    val obj = new C5Simple
    println(~obj)
    println(obj + ("1", "2"))
    println(obj - )
  }
}
class C5Simple{
  def unary_~(): String = "prefix operator"//+ - ! ~
  def +(arg: String *): String = "infix operator"
  def -(): String = "suffix operator"
}