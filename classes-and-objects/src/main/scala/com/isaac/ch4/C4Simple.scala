package com.isaac.ch4

import com.isaac.ch4.class_and_object.ChecksumAccumulator

object C4Simple extends App {
  val result = ChecksumAccumulator.calculate("test")
  println(result)
}
