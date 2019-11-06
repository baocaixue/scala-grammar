# 函数和闭包    
- 方法...................................................[1](#Methods)
- 局部函数...................................................[2](#Local-Functions)
- 一等函数...................................................[3](#FirstClass-Functions)
- 函数字面量的简写形式...................................................[4](#Function-Literals)
- 占位符语法...................................................[5](#Placehoder-Syntax)
- 部分应用的函数...................................................[6](#Partially-Applied-Functions)
- 闭包...................................................[7](#Closures)
- 特殊函数调用形式...................................................[8](#Special-Function-Call-Forms)
- 尾递归...................................................[9](#Tail-Recursion)    

***    
## Methods    
　　定义函数最常用的方式是作为某个对象的成员，这样的函数被称为*方法（method）*。例如下面示例展示了两个方法，合在一起读取给定名称的文件并打印
出所有超过指定长度的行。    
```scala
import scala.io.Source

object LongLines {
  def processFile (filename: String, width: Int) = {
    val source = Source.fromFile(filename)
    for (line <- source.getLines())
      processLine(filename, width, line)
  }

  private def processLine(filename: String, width: Int, line: String) = {
    if (line.length > width)
      println(filename + ": " + line.trim)
  }
}
```    

***    

## Local-Functions    
　　前面的processFile方法的构建展示了函数式编程风格的一个重要设计原则：*程序应该被分解成许多小函数，每个函数都只做明确定义的任务*。单个函数
通常都很小。这种风格的好处是可以灵活地将许多构建单元组装起来，完成更复杂的任务。每个构建单元都应该足够简单，简单到能够单独理解的程度。    
　　这种方式的一个问题是助手函数的名称会污染整个程序的命名空间。在解释器中，这并不是太大的问题，不过一旦函数打包进可复用的类和对象当中，我们
通常都希望类的使用者不要直接看到这些函数。它们离开了类和对象单独存在时通常都没有什么意义，而且通常希望在后续采用其他的方式重写该类时，保留删除
助手函数的灵活性。    
　　在Java中，帮助达到此目的的主要工具是私有方法。这种私有方法的方式在Scala中同样有效，不过Scala还提供了另一种思路：可以在某个函数内部定义
函数。就像局部变量一样，这样的局部函数只在包含它的代码块中可见，例如：    
```scala
import scala.io.Source

def processFile(filename: String, width: Int) = {
  def processLine(filename: String, width: Int, line: String) {
    if (line.length > width) println(filename + ": " + line.trim)
  }
  
  val source = Source.fromFile(filename)
  for (line <- source)
    processLine(filename, width, line)
}
```    
　　注意到filename和width被直接传给助手函数，这里的传递不是必须的，因为局部函数可以访问包含它们的函数的参数，修改如下：    
```scala
import scala.io.Source

def processFile(filename: String, width: Int) = {
  def processLine(line: String) {
    if (line.length > width) println(filename + ": " + line.trim)
  }
  
  val source = Source.fromFile(filename)
  for (line <- source)
    processLine(line)
}
```    

***    

## FirstClass-Functions    
　　Scala支持*一等函数*。不仅可以定义函数并调用它们，还可以用匿名的字面量来编写函数并将它们作为值进行传递。函数字面量被编译成类，并在运行时
实例化成*函数值*（每个函数值都是某个扩展自scala包的FunctionN系列当中的一个特质的类的实例，比如Function0表示不带参数的函数，Function1表示
带一个参数的函数，等等。每一个FunctionN特质都有一个apply方法用来调用该函数）。因此，函数字面量和函数值的区别在于，函数字面量存在于源码，而
函数值以对象的形式存在于运行时。这跟类（源码）与对象（运行时）的区别很相似。    
　　这是一个对某个数加1的函数字面量的简单示例：`(x: Int) => x + 1`。`=>`表示该函数将左侧的内容（任何Int类型的数x）转换成右侧的内容（x + 1）。
因此，这是一个将任何整数x映射成x+1的函数。    
　　函数值是对象，所以可以将它们存放在变量中。它们同时也是函数，所以可以用常规的圆括号来调用。示例如下：    
```shell script
scala> var increase = (x: Int) => x + 1
scala> Int => Int = <function1>
scala> increase(10)
scala> Int = 11

scala> increase = (x: Int) => x + 9999
scala> Int => Int = <function1>
scala> increase(10)
scala> Int = 10009
```    
　　很多Scala类库都会用到函数字面量（函数值）。例如，所有的集合类都提供了foreach方法。它接收一个函数作为入参，并对它的每个元素调用这个函数。
如下是使用该方法打印列表所有元素的例子：    
```scala
val someNumbers = List(-11, -10, -5, 0, 5, 10)
someNumbers.foreach(num => println(num))
val result = someNumbers.filter((x: Int) => x >5)
```    

***    

## Function-Literals    
　　Scala提供了多个省去冗余信息，更简要地编写函数的方式。一种让代码变得更就爱你要的方式是略去参数类型声明。如前面例子中filter示例可以这样：
`someNumbers.filter((x) => x > 5)`。Scala编译器知道参数是整数，因为它看到用这个函数来过滤一个由整数组成的列表（someNumbers）。这被称作
*目标类型（target typing）*，因为一个表达式的目标使用场景（本例中它是传递给`someNumbers.filter()`的参数）可以影响该表达式的类型（本例
决定了x参数的类型）。目标类型这个机制的细节不重要，可以不需要指明参数的类型，直接使用函数字面量，当编译器报错时再加上类型声明;另一个去除源码
中无用字符的方式是省去某个靠类型推断（而不是显式给出）的参数两侧的圆括号：`someNumbers.filter(x => x > 5)`。    

***    

