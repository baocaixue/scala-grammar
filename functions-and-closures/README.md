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

## Placehoder-Syntax    
　　为了让函数字面量更加精简，还可以*使用下划线作为占位符*，用来表示一个或多个参数，只要满足每个参数只在函数字面量中出现一次即可。例如`_ > 0`
是一个非常短的表示法，表示一个检查某个值是否大于0的函数：`someNumbers.filter(_ > 0)`。可以将下划线当成是表达式中的需要被“填”的“空”。举例
来说，如果someNumbers被初始化成`List(-11, -10, -5, 0, 5, 10)`，filter方法首先把`_ > 0`中的空替换成-11,即`-11 > 0`，然后替换成-10,
-5,以此类推，直到List末尾。因此，函数字面量`_ > 0`跟之前`x => x > 0`是等价的。    
　　有时当使用下划线作为参数占位时，编译器可能没有足够的信息来判断确实的参数类型。例如`_ + _`，在这类情况下，可以用冒号来给出类型：`(_: Int) + (_: Int)`。
注意，`_ + _`将会展开成一个接收两个参数的函数字面量。这就是为什么只有当每个参数在函数字面量中出现不多不少正好一次的时候才能使用这样的精简写
法。多个下划线意味着多个参数，而不是对单个参数的重复使用。    

***    

## Partially-Applied-Functions    
　　虽然前面用下划线可以替换掉单独的参数，也可以用下划线替换整个参数列表。例如，对`println(_)`也可以写成`println _`。所以`someNumbers.foreach(println _)`
和`someNumbers.foreach(x => println(x))`是等价的。这里的下划线并非单个参数的占位符，它是整个参数列表的占位符。注意需要保留函数名和下划
线之间的空格。    
　　当这样使用下划线时，实际上是在编写一个*部分应用的函数*。在Scala中，当调用某个函数，出入任何需要的参数时，实际上是应用那个函数到这些参数
上。例如给定如下函数：    
```shell script
scala> def sum(a: Int, b: Int, c: Int) = a + b + c
sum: (a: Int, b: Int, c: Int)Int
scala> sum(1,2,3)
res10: Int = 6
```    
　　部分应用的函数是一个表达式，在这个表达式中，并不给出函数需要的所有参数，而是给出部分，或者完全不给。举例来说，要基于sum创建一个部分应用
的函数，假如不想给出三个参数的任何一个，可以在sum后放一个i下划线。这将返回一个函数，可以被存放到变量中：    
```shell script
scala> val a = sum _
a: (Int, Int, Int) => Int = <function3>
```    
　　有了这些代码，Scala编译器将根据部分应用函数`sum _`实例化一个接收这三个整数参数的函数值，并将指向这个新的函数值的引用赋值给变量a。当对三
个参数应用这个新的函数值时，它将转而调用sum，传入这个三个参数：    
```shell script
scala> a(1,2,3)
res11: Int = 6
```    
　　背后发生的事情是：名为a的变量指向一个函数值对象。这个函数值是一个从Scala编译器自动从`sum _`这个部分应用函数表达式生成的类的实例。由编译
器生成的这个类有一个接收三个参数的apply方法。生成的类的apply方法之所以接收三个参数，是因为表达式`sum _`缺失的参数个数为3.Scala编译器将表
达式`a(1,2,3)`翻译成对函数值的apply方法的i调用，传入这三个参数。因此，`a(1,2,3)`可以看作`a.apply(1,2,3)`。这个由Scala编译器从表达式
`sum _`自动生成的类中定义的apply方法只是简单地将三个缺失的参数转发给sum，然后返回结果。    
　　还可以从另一个角度来看待这类用下划线表示整个参数列表的表达式，即这是一种将def变成函数值的方式。举例来说，如果有一个局部函数，比如`sum(a
: Int, b: Int, c: Int): Int`，可以将它“包”在一个函数值里，这个函数值拥有相同的参数列表和结果类型。当应用这个函数值到某些参数时，它转而
应用sum到同样的参数，并返回结果。虽然不能将方法或嵌套的函数值直接赋值给某个变量，或者作为参数传递给另一个函数，可以将方法或嵌套函数打包在一个
函数值里（具体来说就是在名称后面加上下划线）来完成这样的操作。     
　　部分应用函数之所以叫做部分应用函数，是因为并没有把那个函数应用到所有入参。拿`sum _`来说，没有应用任何入参。不过，完全可以通过给出一些
必填的参数来表达一个部分应用函数。参考下面例子：    
```shell script
scala> val b = sum(1, _: Int, 3)
b: Int => Int = <funciton1>
```    
　　在本例中，提供了第一个和最后一个参数给sum，但没有给出第二个参数。由于只缺失了一个参数，Scala编译器将生成一个新的函数类，这个类的apply
方法只接收一个参数。如果，部分应用函数表达式并不给出任何参数，比如`println _`或`sum _`，可以在需要这样一个函数的地方更加精简的表示，连下
划线也可以省去：`someNumbers.foreach(println)`。这种形式只在明确需要函数的地方被允许。    

***    