package com.isaac.ch7

object C7Simple extends App {
  println(ifTest(1))

  def ifTest(i: Int): String = if(i == 1) i.toString else "Default Value"
}
