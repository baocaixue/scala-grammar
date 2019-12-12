package com.isaac.ch9

import java.io.{File, PrintWriter}

/**
 * 新的控制结构
 * 贷出模式
 */
object LoanPatternDemo {
  def withPrintWriter1(file: File, op: PrintWriter => Unit) = {
    withPrintWriter(file){
      op//客户端代码调用，这里应该是定义的函数字面量，这里完全用于演示目的
    }
  }

  def withPrintWriter(file: File)(op: PrintWriter => Unit) = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

}
