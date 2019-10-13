
object C3Simple {
  def main(args: Array[String]): Unit = {
    parameterize()

  }

  def parameterize(): Unit = {
    val greetStrings = new Array[String](3)
    greetStrings(0) = "Hello"
    greetStrings(1) = ", "
    greetStrings(2) = "world!\n"
    print("array show>> ")
    greetStrings.foreach(print)

    val test = new MyTest()
    //只接收一个参数的方法，支持这样的调用
    test myTest "hello"

    println("test.apply(1)>> " + test(1))//test.apply(1)

    println("test.update(0,\"OK\")>> " + (test(0) = "Ok"))//test.update(0, "OK")

    val greetStrings1 = Array("Hello", ",", "world!\n")
    print("array simply>> ")
    greetStrings1.foreach(print)
  }

}

class MyTest(){
  def myTest(arg: String): Unit = println("MyTest.myTest()>> " + arg)
  def apply(arg: Int): Int = arg
  def update(arg1: Int, arg2: String): String = arg2 + arg1
}
