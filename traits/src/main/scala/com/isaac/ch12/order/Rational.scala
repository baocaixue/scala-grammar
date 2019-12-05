package com.isaac.ch12.order

class Rational(n: Int, d: Int) extends Ordered[Rational]{
  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer: Int = n / g
  val denom: Int = d / g

  def this(n:Int) = this(n, 1)//auxiliary constructor

  override def toString: String = numer + "/" + denom

  def +(that: Rational): Rational =
    new Rational(
      this.numer * that.denom + this.denom * that.numer,
      this.denom * that.denom
    )

  def +(i: Int): Rational = this + new Rational(i)

  def -(that: Rational): Rational =
    new Rational(
      numer * that.denom - that.numer * denom,
      denom * that.denom
    )

  def -(i: Int): Rational = this - new Rational(i)

  def *(that: Rational): Rational =
    new Rational(this.numer * that.numer, this.denom * that.denom)

  def *(i: Int): Rational = this * new Rational(i)

  def /(that: Rational): Rational = new Rational(numer * that.denom, denom * that.numer)

  def /(i: Int): Rational = this / new Rational(i)

  def lessThan(that: Rational): Boolean = this.numer * that.denom < that.numer * this.denom

  def max(that: Rational): Rational = if (this.lessThan(that)) that else this

  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

  override def compare(that: Rational): Int = (this.numer * that.denom) - (that.numer * this.denom)
}
