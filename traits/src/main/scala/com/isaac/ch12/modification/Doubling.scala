package com.isaac.ch12.modification

trait Doubling extends IntQueue {
  abstract override def put(x: Int) = super.put(2 * x)
}
