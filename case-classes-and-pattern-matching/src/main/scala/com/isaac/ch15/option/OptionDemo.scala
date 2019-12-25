package com.isaac.ch15.option

object OptionDemo {
  def main(args: Array[String]): Unit = {
    val capitals = Map("France" -> "Paris", "China" -> "Beijing", "test" -> null)
    println(capitals get "USA")
    println(capitals get "test")
    println(capitals get "China")

    println(show(capitals get "USA"))
    println(show(capitals get "test"))
    println(show(capitals get "China"))
  }

  def show(x: Option[String]) = x match {
    case Some(s) => s
    case None => "?"
  }
}
