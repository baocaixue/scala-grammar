package com.isaac.ch6

class Rational(n: Int, d: Int) {
  require(d != 0)
  val numer: Int = n
  val denom: Int = d
  override def toString: String = n + "/" + d
  def add(that: Rational): Rational =
    new Rational(
      this.numer * that.denom + this.denom * that.numer,
      this.denom * that.denom
    )
}
