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


