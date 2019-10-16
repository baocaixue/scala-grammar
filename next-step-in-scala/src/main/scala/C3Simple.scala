import scala.io.Source

object C3Simple {
  def main(args: Array[String]): Unit = {
    parameterize()

    useList()

    println("tuple._1 >> " + useTuple()._1)

    useSetAndMap()

    printArgs(Array("hello", ",", "world", "!"))

    readTextFile("/home/isaac/dev/code/IdeaProjects/scala-grammar/next-step-in-scala/target/classes/test.txt")
  }

  def parameterize(): Unit = {
    println(">>>>>>>>>>>>>>>>")
    val greetStrings = new Array[String](3)
    greetStrings(0) = "Hello"
    greetStrings(1) = ", "
    greetStrings(2) = "world!\n"
    print("array show>> ")
    greetStrings.foreach(print)

    val test = new MyTest()
    //只接收一个参数的方法，支持这样的调用
    test myTest "hello"
    "1" :: test//与上面不一样，方法名以冒号结尾，方法的调用发生在右操作元，即test.::("1")

    print("test.apply(1,2,3)>> ");test(1,2,3);println()//test.apply(1,2,3)

    println("test.update(0,\"OK\")>> " + (test(0) = "Ok"))//test.update(0, "OK")

    val greetStrings1 = Array("Hello", ",", "world!\n")
    print("array simply>> ")
    greetStrings1.foreach(print)
    println()
  }

  def useList(): Unit = {
    println(">>>>>>>>>>>>>>>>")
    val oneTwo = 1 :: 2 :: Nil
    val threeFour = List(3, 4)
    val oneTwoThreeFour = oneTwo ::: threeFour
    println("List(1, 2) ::: List(3, 4) >> " + oneTwoThreeFour)
    println("one two three four forall \"<5\" >> " + oneTwoThreeFour.forall(item => item < 5))
    println("oneTwo init >> " + oneTwo.init)
    println("threeFour mkString >> " + threeFour.mkString("<"))
    println("oneTwoThreeFour sortWith desc >> " + oneTwoThreeFour.sortWith((a,b) => a > b))
    println()
  }

  def useTuple(): (String, Int)= {
    println(">>>>>>>>>>>>>>>>")
    ("test",1)
  }

  def useSetAndMap(): Unit = {
    println(">>>>>>>>>>>>>>>>")
    var jetSet = Set("Boeing", "Airbus")
    jetSet += "Lear"//jetSet = jetSet + "Lear"
    println("immutable.Set += is jetSet = jetSet + \"Lear\" >> " + jetSet)
    val movieSet = scala.collection.mutable.Set("Hitch", "Poltergeist")
    movieSet += "shrek"//movieSet.+=("shrek")
    println("mutable.Set += is movieSet.+=(\"shrek\") >> " + movieSet)

    val treasureMap = scala.collection.mutable.Map[Int, String]()
    treasureMap += (1 -> "Go to island.")
    treasureMap.+=(2 -> "Find big X on ground.")
    treasureMap += (3 -> "Dig.")
    println("scala.collection.mutable.Map(2) >> " + treasureMap(2))

    println("call \"key\".->(1) >> " + ("key" -> 1))
    println()
  }

  def printArgs(args: Array[String]) = args.mkString("\n")

  def readTextFile(fileName: String): Unit = {
    assert(!fileName.isEmpty)
    def widthOfLength(line: String) = line.length.toString.length
    val source = Source.fromFile(fileName)
    val lines = source.getLines().toList
    val maxWidth = widthOfLength(lines.reduceLeft((a,b) => if(a.length>b.length)a else b))
    lines.foreach(line => {
      val padding = " " * (maxWidth - widthOfLength(line))
      println(padding + line.length + " | " + line)
    })
    source.close()
  }

}

class MyTest{
  def myTest(arg: String): Unit = println("MyTest.myTest()>> " + arg)
  def apply(args:Int*): Unit = args.foreach(print)
  def update(arg1: Int, arg2: String): String = arg2 + arg1
  def ::(arg: String): Unit = {}
}
