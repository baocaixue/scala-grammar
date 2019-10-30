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


