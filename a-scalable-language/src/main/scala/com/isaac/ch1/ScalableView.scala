package com.isaac.ch1

import akka.actor.{Actor, ActorSystem, Props}

object ScalableView {
  def main(args: Array[String]): Unit = {
    //associative map
    var capital = Map("US" -> "Washington", "France" -> "Paris")
    capital += ("China" -> "Beijing")
    println(capital("China"))

    println(factorial(30))

    val str = "heLLo"
    println(str.exists(_.isUpper))

    val system = ActorSystem("hello")
    val helloActor = system.actorOf(Props[HelloActor], name = "helloActor")
    helloActor ! "hello"
    helloActor ! ">>>"

  }

  def factorial(x: BigInt): BigInt = if (x == 0) 1 else x * factorial(x - 1)
}

class HelloActor extends Actor{
  override def receive: Receive = {
    case "hello" => println("你好")
    case _ => println("...")
  }
}
