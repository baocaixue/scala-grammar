package com.isaac.ch15.pattern_everywhere

object PatternDemo extends App {
  //pattern in variable definitions
  val myTuple = (123, "abc")
  val (number, string) = myTuple
  println(number + " " + string)

  //case sequence as partial function
  val withDefault: Option[Int] => Int = {
    case Some(x) => x
    case None => 0
  }
  withDefault(Option(1))
  var list = List(1)
  val second: PartialFunction[List[Int], Int] = {
    case _ :: y :: _ => y
  }
  if (second.isDefinedAt(list)) {
    println(second(list))
  }

  //pattern in for expressions
  val results = List(Some("apple"), None, Some("orange"))
  for (Some(fruit) <- results) println(fruit)
}
