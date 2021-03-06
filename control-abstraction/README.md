# 控制抽象    
- 减少代码重复...................................................[1](#Reducing-Code-Duplication)
- 简化调用方代码...................................................[2](#Simplifying-Client-Code)
- 柯里化...................................................[3](#Currying)
- 编写新的控制结构...................................................[4](#Writing-New-Control-Structures)
- 传名参数...................................................[5](#ByName-Parameters)    

***

## Reducing-Code-Duplication  
　　所有的函数都能被分解每次函数调用都一样的公共部分和每次调用不一样的非公共部分。公共部分是函数体，而非公共部分必须通过实参传入。当把函数值
当作入参的时候，这段算法的非公共部分本身又是另一个算法！每当这样的函数被调用，都可以传入不同的函数值作为实参，被调用的函数会（在由它选择的时
机）调用传入的函数值。这些*高阶函数（higher-order function）*，即那些接收函数作为参数的函数，让我们有机会来进一步压缩和简化代码。    
　　高阶函数的好处之一是可以用来创建减少代码重复的控制抽象。例如，假定在编写一个文件浏览器，而你打算提供API给用户来查找匹配某个条件的文件。
首先，添加了一个机制用来查找文件名是以指定字符串结尾的文件。比如，这将允许用户查找所有扩展名为“.scala”的文件。可以通过在单例对象中定义一个
公共的filesEnding方法来提供这样的API：    
```scala
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles()
  
  def filesEnding(query: String) = 
    for (file <- filesHere; if file.getName.endsWith(query))
      yield file
}
```    
　　这个filesEnding方法用私有的助手方法filesHere来获取当前目录下的所有文件，然后基于文件名是否以用户给定的查询条件来结尾过滤这些文件。由于
filesHere是私有的，fileEnding方法是FileMatcher中定义的唯一一个能被访问到的方法。    
　　目前为止，还没有重复的代码。不过到了后来，要决定可以基于文件名的任意部分进行搜索。于是需要给FileMatcher API添加这个函数：    
```scala
def filesContaining(query: String) =
    for (file <- filesHere; if file.getName.contains(query))
      yield file
```    
　　这个函数跟filesEnding的运行机制没什么两样：搜索filesHere，检查文件名，如果名字匹配则返回文件。唯一的区别是这个函数用的contains而不是
endsWith。    
　　再后来，需要对某些高级用户提出的想要基于正则表达式搜索的需求进行API改进。目录可能有上千个文件，为了支持，编写了下面这个函数：    
```scala
def filesRegex(query: String) =
    for (file <- filesHere; if file.getName.matches(query))
      yield file
```    
　　会注意到这些函数中不断重复的这些代码，有没有办法将它们重构成公共的助手函数呢？按显而易见的方式来并不行。我们需要这样的效果：    
```scala
def filesMatching(query: String, method) = 
    for (file <- filesHere; if file.getName.method(query))
      yield file
```    
　　这种方式在某些动态语言中可以做到，但Scala并允许像这样在运行时将代码黏在一起。    
　　函数值提供了一种答案。虽然不能将方法名像值一样传来传去，但是可以通过传递某个帮你调用方法的函数值来达到同样的效果。在本例中，可以给方法添
加一个matcher参数，该参数的唯一目的就是检查文件名是否满足某个查询条件：    
```scala
def filesMatching(query: String, matcher: (String, String) => Boolean) =
    for (file <- filesHere; if matcher(file.getName, query))
      yield  file
```    
　　在这个版本中，if子句用matcher来检查文件名是否满足查询条件。这个检查具体做什么，取决于给定的matcher。现在，来看看matcher这个类型本身。
它首先是个函数，因此在类型声明中有个`=>`符号。这个函数接收两个字符串类型的参数（分别是文件名和查询条件），返回一个布尔值，因此这个函数的完整
类型是`(String, String) => Boolean`。    
　　有了这个新的filesMatching助手方法，可以将前面三个搜索方法简化，调用助手方法，传入合适的函数：    
```scala
def filesEnding(query: String) = 
    filesMatching(query, _.endsWith(_))

def filesContaining(query: String) = 
    filesMatching(query, _.contains(_))

def filesRegex(query: String) = 
    filesMatching(query, _.matches(_))
```    
　　这里对占位符的使用做一下说明：filesEnding方法里的函数字面量`_.endsWith(_)`的含义跟这段代码是一样的`(fileName: String, query: String) => fileName.endsWith(query)`。
由于filesMatching接收一个要求两个String入参的函数，并不需要显式地给出入参类型，可以直接写成`(fileName, query) => fileName.endsWith(query)`。
因为这两个参数在函数体*分别只用到一次*（地一个参数fileName先被用到，然后是第二个参数query），可以用占位符语法来写：`_.endsWith(_)`。第
一个下划线是第一个参数（即文件名）的占位符，而第二个下划线是第二个参数（即查询字符串）的占位符。    
　　这段代码已经很简化了，不过实际上还能更短。注意这里的查询字符串被传入fileMatching后，fileMatching并不对它做任何处理，只是将它传入mather
函数。这样的来回传递是不必要的，因为调用者已经知道这个查询字符串了。完全可以将query参数从filesMatching和mather中移除：    
```scala
object FileMather {
  private def filesHere = (new java.io.File(".")).listFiles()

  def filesEnding(query: String) =
        filesMatching(_.endsWith(query))

  def filesContaining(query: String) =
      filesMatching(_.contains(query))

  def filesRegex(query: String) =
      filesMatching(_.matches(query))

  def filesMatching(matcher: String  => Boolean) =
    for (file <- filesHere; if matcher(file.getName))
      yield file
}
```    
　　这个例子展示了一等函数是如何帮助消除代码重复的，没有它们，就很难做到这样。比如在Java中，可能会写一个接口，这个接口包含了一个接收String
返回Boolean的方法，然后创建并传入了一个实现了这个接口的匿名内部类（ps：现在有lambda了）的实例给filesMatching。虽然这种做法能够消除重复
的代码，但同时也增加了不少甚至更多新的代码。因此，这样的投入带来的收益并不大。    
　　不仅如此，这个示例中还展示了闭包是如何帮助我们减少代码重复的。前一例中用到了函数字面量，比如`_.endsWith(_)`是在运行时被实例化成函数值
的，它们并不是闭包，因为它们并不捕获任何自由变量。在表达式`_.endsWith(_)`中用到的两个变量都是由下划线表示的，这意味着它们取自该函数的入参。
因此，`_endsWith(_)`使用了两个绑定变量，并没有使用任何自由变量。相反，在最新的这个例子中，函数字面量`_.endsWith(query)`包含了一个绑定
变量，和一个名为query的自由变量。正因为Scala支持闭包，才能将query参数从filesMatching中拿掉，从而进一步简化代码。    

***    

## Simplifying-Client-Code    
　　高阶函数的另一个重要的用处是将高阶函数本身放在API当中让调用方代码更加精简。Scala集合类型提供的特殊用途的循环方法是一个很好的例子。exists
这个方法用于判定某个集合是否包含传入的值。当然可以通过如下方式来查找元素：初始化一个var为false，用循环遍历整个集合检查每一项，如果发现要找
的内容，就把var设为true。参数下面代码，判定传入的List是否包含负数：    
```scala
def containsNeg(nums: List[Int]): Boolean = {
  var exist = false
  for (num <- nums; if num < 0) exist = true
  exist
}
```    
　　不过更精简的定义方式是对传入的List调用高阶函数exists，就像这样：    
```scala
def containsNeg(nums: List[Int]) = nums.exists(_ < 0)
```    
　　这个exists方法代表了一种控制抽象。这是Scala类库提供的一个特殊用途的循环结构，并不是像while或for那样是语言内建的。与前面提供的函数filesMatching
类似，exists也帮助哦我们减少了代码的重复，不过由于exists是Scala集合API中的公共函数，它减少的是API使用方的代码重复。    

***    

## Currying    
　　Scala允许创建新的控制抽象，“感觉就像语言原生支持的那样”。为了搞清楚如何做出那些用起来感觉像是语言扩展的控制抽象，首先需要理解一个函数式
编程技巧，那就是**柯里化（currying）**。    
　　一个经过柯里化的函数在应用时支持多个参数列表，而不是只有一个。这个示例展示了一个没有经过柯里化的函数，对两个Int参数x和y做加法：`def 
plainOldSum(x: Int, y: Int) = x + y`。将这个函数经过柯里化：`def curriesSum(x: Int)(y: Int) = x + y`，跟使用一个包含两个Int参数
列表不同，应用这个函数到两个参数列表。当使用`curriedSum(1)(2)`时，实际上是连着做了两次传统的函数调用。地一个调用接收了一个名为x的Int参数，
返回一个用于第二次调用的函数值，这个函数接收一个Int参数y。可以参考下面这个名为first的函数（这是一个传统的未经过柯里化的函数）：    
```scala
def first(x: Int) = (y: Int) => x + y
```    
　　从原理上讲它和经过柯里化的carriedSum是一样的。把first应用到1：`val second = fist(1)`这时second的类型是`Int => Int = <function1>`。
应用第二个函数到2：`second(2)`这将得到结果`Int = 3`。这里的first和second函数只是对柯里化过程的示意，它们和curriedSum函数并不直接相关。
尽管如此，还是可以获取到指向curriedSum的“第二个”函数的引用：    
```shell script
val onePlus = curriedSum(1)_
onePlus: Int => Int = <function1>
```    

***    
## Writing-New-Control-Structures    
　　 在拥有一等函数的语言中，可以有效地制作出新的控制i接口，尽管语言语法是固定的。需要做的就是*创建接收函数作为入参的方法*。例如下面这个
“twice”控制结构，它重复某个操作两次，并返回结果：    
```scala
def twice(op: Double => Double, x: Double) = op(op(x))

twice(_ + 1, 6) == 8.0
```    
　　每当发现某个控制模式在代码中多处出现，就应该考虑将这个模式实现为新的控制结构。前面看到fileMatching这个非常特殊的控制模式，现在来看一个
更加常用的编码模式：打开某个资源，对它进行操作，然后关闭这个资源。可以用类似如下的方法，将这个模式捕获成一个控制抽象：    
```scala
def withPrintWriter(file: java.io.File, op: java.io.PrintWriter => Unit) = {
  val writer = new java.io.PrintWriter(file)
  try {
    op(writer)
  } finally {
    writer.close()
  }
}

//use
withPrintWriter(
  new java.io.File("data.txt"),
  writer => writer.println(new java.util.Date())
)
```    
　　使用这个方法的好处是，确保文件最后被关闭的是withPrintWriter而不是用户代码。因此不可能出现使用者忘记关闭文件的情况。这个技巧被称做*贷出模式（loan pattern）*，
因为是某个控制抽象函数，，比如withPrintWriter，打开某个资源并将这个资源“贷出”给函数。当函数完成时，它会表明自己不再需要这个“贷入”的资源。
这时这个资源就在finally代码块中被关闭了，这样能确保不论函数是正常返回还是抛出异常，资源都会被正常关闭。    
　　可以用花括号而不是圆括号来表示参数列表，这样调用方的代码看上去是在使用内建的控制结构。在Scala中，只要有那种只传入*一个参数*的方法调用，都
可以选择使用花括号将入参包起来：`println {"Hello World!"}`。    
　　Scala允许用花括号替代圆括号来传单个入参的目的是为了让调用方程序员在花括号中编写函数字面量。而对于多个参数的函数，可以通过将函数进行柯里化
来达到这个目的：     
```scala
def withPrintWriter(file: java.io.File)(op: java.io.PrintWriter => Unit) = {
  val writer = new java.io.PrintWriter(file)
  try {
    op(writer)
  } finally {
    writer.close()
  }
}

//use
withPrintWriter(new java.io.File("data.txt")){ writer =>
  writer.println(new java.util.Date())
}
```    

***    
## ByName-Parameters
```scala
var assertionsEnabled = true

def myAssert(predicate: () => Boolean) = 
    if (assertionsEnabled && !predicate()) throw new AssertionError()

def myAssert(predicate: => Boolean) = 
    if (assertionsEnabled && !predicate) throw new AssertionError()

def boolAssert(predicate: Boolean) = 
    if (assertionsEnabled && !predicate) throw new AssertionError()

//
assertionsEnabled = false
myAssert(5/0 == 0) //nothing
myAssert(() => 5/0 == 0) //noting
boolAssert(5/0 == 0) //by zero exception
```
