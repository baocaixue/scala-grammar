package com.isaac.ch4

object Demo extends App {
  import math.{Pi=>pi, E}

  private val str: String = E match {
    case pi => "strange math? pi = " + pi
    //case _ => "OK"
  }

  private val isIntIntMap: Any => Boolean =
    {
      case m: Map[Int, Int] => true
      case _ => false
    }

  private def isStringArray(x: Any) = x match {
    case a:Array[String] => true
    case _ => false
  }

  println(str)
  println(isIntIntMap(Map(1-> "a")))//true
  println(isStringArray(Array(1)))//false
}
