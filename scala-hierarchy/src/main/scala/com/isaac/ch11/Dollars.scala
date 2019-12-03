package com.isaac.ch11

class Dollars(val amount: Int) extends AnyVal {
  override def toString: String = "$" + amount
}
