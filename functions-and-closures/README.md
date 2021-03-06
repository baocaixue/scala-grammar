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

## Closures    
　　目前为止，所有的函数字面量示例，都只是引用了传入的参数。如，在`(x: Int) => x > 0`中，唯一在函数体`x > 0`中用到的变量是x，即这个函数
的唯一参数。不过，也可以引用其他地方定义的变量：`(x: Int) => x + more`。这个函数将“more”也作为入参，不过more是哪里来的？从这个函数的角
度看，more是一个*自由变量*，因为函数字面量本身并没有给more任何含义。相反，x是一个*绑定变量*，因为它在该函数的上下文里有明确的含义：它被定义
为该函数的唯一参数，一个Int。如果单独使用这个函数字面量，而并没有在任何处于作用域内的地方定义more，编译会报错。    

　　另一方面，只要能找到名为more的变量，同样的函数字面量就能正常工作：    
```shell script
scala> var more = 1
more: Int = 1
scala> val addMore = (x: Int) => x + more
addMore:Int => Int = <function1>
scala> addMore(10)
res16: Int = 11
```    
　　运行时从这个函数字面量创建出来的函数值（对象）被称作**闭包（closure）**。该名称源于“捕获”其自由变量从而“闭合”该函数字面量的动作。没有
自由变量的函数字面量，比如`(x: Int) => x + 1`，称为*闭合语（closed term）*，这里的语指的是一段源代码。因此，运行时从这个函数字面量创建
出来的函数值并不是一个闭包，因为`(x: Int) => x + 1`按照目前这个写法已经是闭合的了。而运行时从任何带有自由变量的函数字面量，比如`(x: Int)
 => x + more`，创建的函数值，按照定义，要求捕获到它的自由变量more的绑定。相应的函数值结果（包含指向被捕获的more变量的引用）就称作闭包，因
 为函数值是通过闭合这个开放语的动作产生的。    
 　　如果more在闭包创建以后被改变会发生什么？在Scala中，答案是闭包能够看到这个改变。参考下面的例子：    
 ```shell script
scala> more = 9999
more: Int = 9999
scala> addMore(10)
res17: Int = 10009
```    
　　很符合直觉的是，Scala的闭包捕获的是变量本身，而不是变量引用的值。正如前面示例所展示的，为`(x: Int) => x + more`创建的闭包能够看到闭
包外对more的修改。反过来也是成立的：闭包对捕获到的变量的修改也能在闭包外被看到。    

***    

## Special-Function-Call-Forms    
　　由于函数调用在Scala编程中的核心地位，对于某些特殊的需求，一些特殊形式的函数定义和调用方式也被加到语言当中。Scala支持重复参数、带名字的
参数和缺省的参数。    

### 重复参数    
　　Scala允许标识出函数的*最后一个参数*可以被重复。让我们可以对函数传入一个可变长度的参数列表。要表示这样一个重复参数，需要在参数的类型之后加
上星号（\*）。例如：`def echo(args: String*)`。这样定义以后，echo可以用零到多个String参数调用。    
　　在函数内部，这个重复参数的类型是一个所声明的参数类型的Array。因此，在echo函数内部，args的类型实际是`Array[String]`。尽管如此，如果有
一个合适类型的数组，并尝试将它作为重复参数传入时，将得到一个编译错误。要完成这样的操作，需要在数组的实参的后面加上一个冒号和一个`_*`符号：    
```scala
val arr = Array("What's", "up","doc")
echo(arr: _*)
```    
　　这种表示法告诉编译器将arr的每个元素作为参数传递给echo，而不是将所有元素放在一起作为单个实参传入。    

### 带名字的参数    
　　在一个普通的函数调用中，实参是根据被调用的函数的参数定义，逐个匹配起来的：`def speed(distance: Float, time: Float) = distance / time`。
现在进行函数调用：`speed(100, 10)`，在这个函数调用中，100被匹配给了distance而10被匹配给了time。100和10两个实参是按照形参被列出的顺序
匹配起来的。带名字的参数可以用不同的顺序将参数传给函数。其语法是简单地在每个实参前加上参数名和等号。例如：`speed(time=10, distance = 100)`。
带名字的参数最常见的场合就是跟缺省的参数值一起使用。    

### 缺省的参数值    
　　Scala允许给函数参数指定缺省值。这些有缺省值的参数可以不出现在函数调用中，对应的参数将会被填充为缺省值。例如：    
```scala
def printTime(out: java.io.PrintStream = Console.out) = 
    out.println("time = " + System.currentTimeMillis())
```    
　　如果用`printTime()`来调用这个函数，也就是不指定用于out的实参。那么out将会被设置为缺省值Console.out。也可以用一个显式给出的输出流来
调用这个函数。例如，可以用`printTime(Console.err)`来将日志发送到标准错误输出。    
    缺省参数和带名字的参数放在一起时尤为有用：    
```scala
def printTime2(out: java.io.PrintStream = Console.out, divisor: Int = 1) = 
    out.println("time = " + System.currentTimeMillis() / divisor)
```       
　  函数printTime2可以用printTime2()来调用，这样两个参数都被填充为缺省值。通过带名字的参数，这两个参数的任何一个都可以被显式地给出，而另
一个将被填充为缺省值：`printTime2(out = Console.err)`、`printTime2(divisor = 1000)`。    

***    

## Tail-Recursion
　　前面提到，如果要将一个不断更新的var的while循环改写成只使用val的更加函数式风格，可能需要用到递归。参考下面这个递归的函数的例子，它通过
反复改进猜测直到结果足够好的方式来取近似值：    
```scala
def approximate(guess: Double): Double = 
    if (isGoodEnough(guess)) guess
    else approximate(improve(guess))
```    
　　有了合适的isGoodEnough和improve的实现，像这样的函数通常被用于搜索。如果希望approximate函数跑的更快，你可能会想用while循环来尝试加快
它的速度：    
```scala
def approximateLoop(initialGuess: Double): Double = {
  var guess = initialGuess
  while (!isGoodEnough(guess)) guess = improve(guess)
  guess
}
```    
　　这两个版本的approximate到底哪一个更好呢？从代码的简洁和避免使用var的角度，第一个函数式的版本胜出。不过指令式的方式是不是真的更高效呢？
事实上，如果我们测量执行时间，这两个版本几乎完全一样。    
　　这听上去有些出乎意料，因为递归调用看上去比简单地从循环的末尾跳到开始更“膨胀”。不过，在上面这个approximate的例子中，Scala编译器能够执行
一个重要的优化。注意递归调用是approximate函数体在求值过程中的最后一步。像approximate这样在最后一步调用自己的函数，被称为*尾递归*函数。Scala
编译器能够检测到尾递归并将它替换成跳转到函数的最开始，并在跳转之前将参数更新为新的值。    
　　这背后的意思是不应该回避使用递归算法来解决问题。通常，递归算法比基于循环的算法更加优雅、精简。如果解决方案是尾递归的，那么我们不需要支付
任何（额外的）运行时开销。    

### 跟踪尾递归函数    
　　尾递归函数并不会在每次调用时构建一个新的栈帧，所有的调用都会在同一个栈帧中执行。这一点可能会出乎检查某个失败程序的栈跟踪信息的程序员的意料。
例如，下面这个函数调用自己若干次之后抛出异常：    
```scala
def boom(x: Int): Int = 
    if (x == 0) throw new Exception("boom!")
    else boom(x -1) + 1
```    
　　该函数不是尾递归的，因为它在递归调用之后还执行了一个递增操作。在执行这段代码时，将看到预期的效果：    
```
Exception in thread "main" java.lang.Exception: boom!
	at com.isaac.ch8.C8Simple$.boom(C8Simple.scala:54)
	at com.isaac.ch8.C8Simple$.boom(C8Simple.scala:55)
	at com.isaac.ch8.C8Simple$.boom(C8Simple.scala:55)
	at com.isaac.ch8.C8Simple$.boom(C8Simple.scala:55)
	at com.isaac.ch8.C8Simple$.main(C8Simple.scala:20)
	at com.isaac.ch8.C8Simple.main(C8Simple.scala)
```    
　　如果把boom改成尾递归：    
```scala
def bang(x: Int): Int = 
    if (x == 0) throw new Exception("bang!")
    else bang(x - 1)
```    
　　将得到这样的结果：    
```
Exception in thread "main" java.lang.Exception: bang!
	at com.isaac.ch8.C8Simple$.bang(C8Simple.scala:60)
	at com.isaac.ch8.C8Simple$.main(C8Simple.scala:22)
	at com.isaac.ch8.C8Simple.main(C8Simple.scala)
```    
　　这次只看到一个bang的栈帧，这不是bang在调用自己之前就崩溃了。如果觉得尾递归优化后的栈帧跟踪信息会困惑，可以把它关掉，做法是给scala命令
或scalac编译器如下参数：`-g:notailcalls`。有了这个参数，将会得到更长的栈跟踪信息。    

### 尾递归的局限    
　　两个相互递归的函数，Scala没法优化。     
　　最后一步调用的是一个函数值（而不是发起调用的那个函数自己），也无法享受到尾递归的优化。
