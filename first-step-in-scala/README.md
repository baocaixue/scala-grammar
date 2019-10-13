# Scala入门    
- 第1步 使用Scala解释器...................................................[1](#Learn-To-Use-The-Scala-Interpreter)
- 第2步 定义变量...................................................[2](#Define-Some-Variables)
- 第3步 定义函数...................................................[3](#Define-Some-Functions)
- 第4步 编写Scala脚本...................................................[4](#Scala-Scripts)
- 第5步 用while做循环；用if做判断...................................................[5](#Loop-And-Decide)
- 第6步 用foreach和for遍历...................................................[6](#Iterate-With-Foreach-And-For)    


***    
## Learn-To-Use-The-Scala-Interpreter    
　　使用Scala解释器    
```shell script
$ scala
Welcome to Scala version 2.11.7
Type in expressions to have them evaluated.
Type :help for more information.

scala> 1 + 2
scala> res0: Int = 3
scala> res0 * 3
scala> res1: Int = 9
scala> println("Hello World!")
scala> Hello Wolrd
```    
***
## Define-Some-Variables    
　　**Scala的变量分为两种：val和var**。val跟Java中的final变量类似，一旦初始化就不能被重新赋值；而var则不同，类似于Java的非final变量，在
整个生命周期内var可以被重新赋值。    
　　这两种变量与Java声明变量方式的显著区别就是：不需要声明变量类型。这体现了Scala的*类型推断*能力，能够推断出那些不显式指定的类型。当Scala的
解释器（或编译器）能够推断类型的时候，通常来说最好让它推断类型，而不是在代码中到处写上那些不必要的、显式的类型标注。当然，也可以显式地给出类型，
有时候可能这样做是正确的选择。显式的类型标注，既可以确保Scala编译器推断出符合意图的类型，也能作为文档，方便今后的代码阅读。*跟Java不同，Scala*
*并不是在变量之前给出类型，而是在变量之后，变量名和类型之间用冒号（:）隔开*。    
```scala
val msg1 = "Hello World!"
val msg2:String = "Hello again, world!"
```    

***    
## Define-Some-Functions    
　　在Scala中：    
```scala
def max(x: Int, y: Int): Int = {
  if (x > y) x else y
}
```    
　　函数定义由**def**开始，然后是函数名（本例中为max）和圆括号中以逗号隔开的参数列表。每个参数后面都必须加上冒号开始的类型标注，因为*Scala
编译器并不会推断函数参数的类型*。在参数列表的右括号之后，还有一个“: Int”的类型标注，这里定义的是函数的返回类型（结果类型）。在函数的结果类型
后是一个等号和用花括号括起来的函数体。    
　　**注：** Scala会对函数结果类型进行推断，但是不建议这么做，因为首先如果是递归函数就必须显式给出函数的结果类型；其次，标注函数结果类型的
代码更清晰直观的表达了意图，阅读起来更轻松。    
　　Scala中的**Unit**类型跟Java的void类型类似。    

***
## Scala-Scripts    
　　`$ scala xxx.scala args...`     

*** 
## Loop-And-Decide    
　　其实这里的while和if代表了*命令式*的编程风格，在Scala中并不是很值得提倡的。Scala提倡的是*函数式*的编程风格。    
```scala
var i = 0
while (i < 10) {
  if(i != 0) print(" ")
  i += 1//++i i++在Scala中不能工作
} 
```    

## Iterate-With-Foreach-And-For    
　　就如前面所述，while循环实际上是在以*指令式（imperative）*的风格编程。指令式风格也是类似Java、C++、C这样语言的通常风格，一次给出执行指令，
通过循环来遍历，而且还经常变更被不同函数共享的状态。Scala允许以指令式的风格编程，不过更倾向于使用*函数式（functional）* 的风格。    
　　函数式编程语言的主要特征之一就是函数是一等的语法单元，Scala非常符合这个描述。举例来说，打印每个命令行参数的另一种方式是：    
　　`args.foreach(arg => println(arg))`    
　　这段代码中，对args执行foreach方法，传入一个函数。在本例中，传入的是一个*函数字面量（function literal）*，这个（匿名）函数接收一个名为
arg的参数。函数体为println(arg)。    
　　在前面示例中，Scala解释器推断出arg的类型是String，因为String是调用了foreach那个数组的元素类型。如果倾向于更明确地表达，也可以指出类型
名：    
　　`args.foreach((arg: String) => println(arg))`    
　　还有一种更为精简的表达，可以利用Scala对函数字面量的一个特殊简写规则。如果函数字面量只是一个接收单个参数的语句，可以不必给出参数名和参数
本身：    
　　`args.foreach(println)`    

　　Scala支持的指令式for语句（arg为val）：    
```scala
for (arg <- args)
    println(arg)
```
