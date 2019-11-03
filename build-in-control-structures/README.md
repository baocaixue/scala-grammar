# 内建的控制结构 
- if表达式...................................................[1](#If-Expressions)
- while循环...................................................[2](#While-Loops)
- for表达式...................................................[3](#For-Expressions)
- 用try表达式实现异常处理...................................................[4](#Exception-Handling)
- match表达式...................................................[5](#Match-Expressions)
- 没有break和continue的日子...................................................[6](#Without-Break-And-Contine)
- 变量作用域...................................................[7](#Variable-Scope)
- 对指令式代码进行重构...................................................[8](#Refactoring-ImperativeStyleCode)

***    

## If-Expressions    
　　Scala的if和很多其他语言一样，首先测试某个条件，然后根据条件是否满足来执行两个不同代码分支中的一个。下面给出一个以指令式风格编写的常见
例子:    
```scala
var filename = "default.txt"
if (!args.isEmpty)
    filename = args(0)
```    
　　这段代码还可以更精简：    
```scala
val filename = if (!args.isEmpty) args(0) else "default.txt"
```    
　　这段代码的优势在于它用的是val而不是var。使用val是函数式的风格，就像Java的final变量那样，有助于编写出好的代码。它也告诉读这段代码的人，
这个变量一旦初始化就不会变化，省去了扫描该变量整个作用域的代码来搞清楚它会不会变的必要;使用val而不是var的另一个好处是*等式推理*的支持。引入
的变量等于计算出它的值的表达式（假定这个表达式没有副作用）。因此，在任何打算写变量名的地方，都可以直接用表达式来代替。    
　　*只要有机会，尽可能使用val，它们会让代码更易读也更易于重构*。    

***    

## While-Loops    
　　Scala的while循环和其他语言用起来没有多大差别。它包含了一个条件检查和一个循环体，只要条件检查为真，循环体就会一遍接着一遍地执行。示例如
下：    
```scala
def gcdLoop(x: Long, y: Long): Long = {
  var a = x
  var b = y 
  while (a != 0) {
    val temp = a
    a = b % a
    b = temp
  }
}
```    
　　Scala也有do-while循环，它跟while循环类似，只不过它是在循环体之后执行条件检查而不是在循环体之前。下面的示例给出了一段用do-while来从标
准输入读取的问本行，知道读到空行为止的Scala脚本：    
```scala
var line = ""
do {
  line = readLine()
  println("Read: " + line)
} while (line != "")
```    
　　while和do-while这样的语法结构，称之为“循环”而不是表达式，因为它们并不会返回一个有意义的值。返回值的类型为Unit。实际上有且仅有这个一个
Unit类型的值，这个值叫作单元值，写作`()`。存在这么一个值，是Scala的Unit跟Java的void的不同。    
　　另一个相关的返回单元的语法结构是对var的赋值。例如，当尝试在Scala中像Java（或C/C++）的while循环惯用那样使用while循环时，会遇到问题：    
```scala
var line = ""
while ((line = readLine()) != "") //并不可行
    println("Read: " + line)
```    
　　这段代码在编译时，Scala编译器会给出警告：用!=对类型为Unit的值和string做比较永远返回true。在Java中，复制语句的结果是被赋上的值，而在
Scala中赋值语句的结果永远为单元值`()`。因为while循环是没有返回值的，所以纯函数式编程语言通常不支持。    
　　对于求x、y的最大公约数函数式风格的方式如下（递归）：    
```scala
def gcd(x: Long, y: Long): Long = if (y == 0) x else gcd(y, x % y)
```    

***    

## For-Expressions    
　　Scala的for表达式让你以不同的方式组合一些简单的因子来表达各式各样的迭代。它可以帮助我们处理诸如便利整数序列等常见任务，也可以通过更高级
的表达式来遍历多个不同种类的集合，根据任意条件过滤元素，产出新的集合。    

### 遍历集合    
　　如示例所示，使用for表达式遍历当前目录的所有文件：    
```scala
import java.io.File
  val files = new File(".").listFiles
  for (file <- files) {
    println(file.getName)
  }
```    
　　通过`file <- files`这样的生成器语法，将遍历files的元素。每一次迭代，一个新的名为file的val都会被初始化成一个元素的值。编译器推断出文件
的类型为File，这是因为files是个Array\[File\]。    
　　for表达式的语法可以用于任何种类的集合，而不仅仅是数组。Range是一类特殊的用例，可以用`1 to 5`这样的语法来创建Range，如果不想包含区间的
上界，可以用`1 until 5`这样。需要注意的是，虽然例如`0 to files.length - 1`这样也是支持的，但是没必要将问题复杂化，直接使用` file <- files`
可以避免不必要的麻烦。    

### 过滤    
　　有时并不用完整的遍历集合，需要的是满足某些条件的一个子集。这时可以给for表达式添加*过滤器*，过滤器是for表达式的圆括号中的一个if字句。例
如：    
```scala
import java.io.File
  val files = new File(".").listFiles
  for (file <- files if file.getName.endsWith("xml")) {
    println(file.getName)
  }
```    

### 嵌套迭代    
　　如果添加多个`<-`字句，将得到嵌套的“循环”。示例如下，需要注意的是内循环和外循环的生成器（和迭代器）用了分号隔开（圆括号不会自动推断分号），
可以使用花括号替代圆括号，可以省去分号。    
```scala
import java.io.File
import scala.io.Source
  val files = new File(".").listFiles
  //noinspection SourceNotClosed
  def fileLines(file: File): List[String] = Source.fromFile(file).getLines().toList

  def grep(pattern: String) =
    for (
      file <- files
      if file.getName.endsWith("xml");//notice me
      line <- fileLines(file)
      if line.trim.matches(pattern)
    ) println(file + ":" + line.trim)

  grep(".*scala.*")
```    

### 中途变量绑定    
　　可以看到前面的`line.trim`重复了两次，可以将结果绑定到新的变量上，被绑定的这个变量引入和使用起来都和val一样：    
```scala
import java.io.File
import scala.io.Source
  val files = new File(".").listFiles
  //noinspection SourceNotClosed
  def fileLines(file: File): List[String] = Source.fromFile(file).getLines().toList

  def grep(pattern: String) =
    for {
      file <- files
      if file.getName.endsWith("xml")
      line <- fileLines(file)
      trimmed = line.trim
      if trimmed.matches(pattern)
    } println(file + ":" + trimmed)

  grep(".*scala.*")
```    

### 产出一个新的集合    
　　目前为止所有示例都是对遍历到的值进行操作然后忘掉它们，也完全可以在每次迭代中生成一个可以被记住的值。具体做法是在for表达式的代码体之前加
上关键字`yield`。例如：    
```scala
def scalaFiles = 
    for{
      file <- files
      if file.getName.endsWith(".scala")
    } yield file
```    
　　for表达式的代码体每次被执行，都会产出一个值（本例是file）。当for表达式执行完毕后，其结果将包含所有交出的值，包含在一个集合当中。结果集合
的类型基于迭代子句中处理的集合种类。需要注意的是`yield`的位置，语法如下： **for 子句 yield 代码体**    

***    

## Exception-Handling    
　　Scala的异常处理跟其他语言类似。方法除了正常地返回某个值之外，也可以通过抛出异常来终止执行。方法的调用方要么捕获并处理这个异常，要么自我
终止，让异常传播到更上层的调用方。异常通过这种方式传播，逐个展开调用栈，直到某个方法处理该异常或者没有更多方法了为止。    

### 抛出异常    
　　在Scala中抛出异常跟Java看上去一样。需要创建一个异常对象然后用`throw`关键字将它抛出：`throw new IllegalArgumentException`。虽然看
上去有些自相矛盾，在Scala中throw是一个有结果类型的表达式，如下是一个带有结果类型的示例：    
```scala
val n = 14
val half = if (n %2 == 0) n / 2 else throw new RuntimeException("n must be even")
```    
　　在这段代码中，如果n是偶数，half将被初始化为n的一半。如果n不是偶数，那么在half被初始化之前，就会有异常被抛出。因此，我们可以安全地将抛出
的异常当作任何类型的值来对待。任何想要使用throw给出的这个返回值的上下文都没有机会真正使用它，也就不必担心有其他的问题。    
　　技术上讲，抛出异常这个表达式的类型是Nothing。哪怕表达式从不实际被求值，也可以用throw。在这个例子中，if的分支计算出某个值，而另一个分支
抛出异常并计算出Nothing。整个if表达式的类型就是计算出来某个值的分支的类型。    

### 捕获异常    
　　可以用下面示例的语法来捕获异常。*catch*子句的语法之所以是这样，为的是与Scala的一个重要组成部分，*模式匹配（pattern matching）*，保持
一致。    
```scala
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

try {
  val f = new FileReader("input.txt")
} catch {
  case ex: FileNotFoundException => //处理找不到文件的情况 
  case ex: IOException => //处理其他I/O错误
}
```    
　　执行这个try-catch表达式跟其他带有异常处理的语言一样。首先代码体会被执行，如果抛出异常，则会依次尝试每个catch子句。本例中，如果异常的类
型是FileNotFoundException，第一个子句将被执行。如果异常的类型是IOException，那么第二个子句将被执行。而如果异常既不是FileNotFoundException
也不是IOException，try-catch将会终止，异常将向上继续传播。注意到Scala跟Java的区别，Scala并不要求捕获受检查异常（checked exception）
或在throws子句里声明。可以选择用`@throws`注解来声明一个throws子句。    

### finally    
　　可以将那些不论是否抛出异常都想执行的代码以表达式的形式包含在finally子句里。例如，可能想要确保某个打开的文件要被正确地关闭，哪怕某个方法
因为抛出了异常而退出：    
```scala
import java.io.FileReader
//贷出模式 可以更精简地达到相同的目的
val file = new FileReader("input.txt")
try {
  //使用文件
} finally {
  file.close()
}
```    

### 交出值    
　　跟Scala的大多数其他控制结构一样，try-catch-finally最终返回一个值。例如下面的示例展示了如何做到解析URL，但当URL格式有问题时返回一个
默认值。如果没有异常抛出，整个表达式的结果就是对应的catch子句的结果;而如果有异常抛出但没有被捕获，整个表达式就没有结果。如果有finally子句，
该子句计算出来的值会被丢弃。finally子句一般都是执行清理工作，比如关闭文件。通常来说，它们不应该改变代码主体或catch子句中计算出来的值。    
　　需要注意的是Scala的行为和Java不同，仅仅是因为Java的try-finally并不返回某个值。跟Java一样，当finally子句包含一个显式的返回语句，或
抛出某个异常，那么这个返回值或异常将会“改写”任何在之前的try代码块或catch子句中产生的值。    
```scala
import java.net.URL
import java.net.MalformedURLException

def urlFor(path: String) = 
    try {
      new URL(path)
    } catch {
      case e: MalformedURLException =>
        new URL("http://www.google.com")
    } 
```    

***    

## Match-Expressions    
　　Scala的match表达式让你从若干可选项中选择，就像其他语言中的switch语句那样。一般而言，match表达式让你用任意的模式来选择。来看一个例子，
先吗的脚本从参数列表中读取食物名称并打印出这个食物的搭配食材。这个match表达式首先检查firstArg，这个变量对应的是参数列表的首个参数。如果是
字符串“salt”，打印“pepper”;如果是“chips”，打印“salsa”;以此类推。缺省的样例以下划线（_）表示，这个通配符在Scala中经常被用来表示某个完全
不知道的值。    
```scala
val args = Array()
val firstArg = if (args.length > 0) args(0) else ""
firstArg match {
  case "salt" => println("pepper")
  case "chips" => println("salsa")
  case "eggs" => println("bacon")
  case _ => println("huh?")
}
```    
　　Scala的match表达式跟Java的switch相比，有一些重要的区别。其中一个区别是任何常量、字符串等都可以用作样例，而不仅限于Java的case语句支
持的整型、枚举和字符串常量。在上面示例中，可选项是字符串。另一个区别是在每个可选项的最后并没有break。在Scala中break是隐含的，并不会出现某
个可选项执行完继续执行下一个可选项的情况。    
　　不过Scala的match表达式和Java的switch相比最显著的不同，在于match表达式会返回值。在前一例中，match表达式的每个可选项都打印出一个值。如
果将打印语句换成交出某个值，相应的代码依然能工作。    

***    

