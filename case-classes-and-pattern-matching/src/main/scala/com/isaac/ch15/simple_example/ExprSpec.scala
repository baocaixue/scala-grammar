package com.isaac.ch15.simple_example

object ExprSpec {
  def main(args: Array[String]): Unit = {
    val v = Var("x")//default have apply function
    println(v.name)
    //v.name = "y"//compile error final field

    val op = BinOp("+", Number(1), v)//default toString hashCode and equals function
    op.copy(operator = "-")//default copy function

    println(simplifyTop(UnOp("-", UnOp("-", Number(1)))))

  }

  def simplifyTop(expr: Expr): Expr = expr match {
    case UnOp("-", UnOp("-", e)) => e
    case BinOp("+", e, Number(0)) => e
    case BinOp("*", e, Number(1)) => e
    case _ => Number(0)
  }
}
