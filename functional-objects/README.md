# 函数式对象    
- Rational类的规格定义...................................................[1](#A-Specification-For-Class-Rational)
- 构建Rational...................................................[2](#Construction-A-Rational)
- 重新实现toString方法...................................................[3](#Reimplement-The-ToString-Method)
- 检查前置条件...................................................[4](#Checking-Preconditions)
- 添加字段...................................................[5](#Adding-Fields)
- 自引用...................................................[6](#Self-Reference)
- 辅助构造方法...................................................[7](#Auxiliary-Constuctors)
- 私有字段和方法...................................................[8](#Private-Fields-And-Methods)
- 定义操作符...................................................[9](#Defining-Operators)
- Scala中的标识符...................................................[10](#Indentifiers-In-Scala)
- 方法重载...................................................[11](#Method-Overloading)
- 隐式转换...................................................[12](#Implicit-Conversions)
- 注意事项...................................................[13](#A-Word-Of-Caution)    

　　本部分重点是那些定义函数式对象的类，或者那些没有任何可变状态的对象。作为例子，将创建一个以不可变对象对有理数建模的类的若干版本。     

***    
## A-Specification-For-Class-Rational
　　有理数（rational number）是可以用比例n/d表示的数，其中n和d都是整数，但d不能为0.跟浮点书相比，有理数的优势是小数是精确展现的，而不会
舍入或取近似值。    
　　这里我们要设计的类将对有理数的各项行为进行建模，包括允许它们被加、减、乘、除。数学中有理数是没有可变状态的。可以将一个有理数和另一个相加，
但结果是一个新的有理数，原始的有理数并不会“改变”。这里要设计的Rational类也要满足这个性质。每一个有理数都会有一个Rational对象来表示。    

***    

## Construction-A-Rational    
　　要定义Rational类，首先可以考虑一下使用者如何创建新的Rational对象。由于已经决定Rational对象是不可变的，将要求使用者在构造Rational实
例的时候就提供所有需要的数据（也就是分子和分母）。因此，可以从如下设计开始：`class Rational(n: Int, d: Int)`    
　　关于这段代码，首先要注意的一点是如果一个类没有定义体，并不需要给出空的花括号（也可以给）。类名Rational后圆括号中的标识符n和d称作类参数。
Scala编译器将会采集到这两个类参数，并且创建一个主构造方法，接收同样的这两个参数。    

***    
## Reimplement-The-ToString-Method    
　　当在前一例中创建Rational实例时，解释器打印了“Rational@90110a”。解释器是通过对Rational对象调用toString来获取到的。Rational默认继承
了java.lang.Object类的toString实现，这个实现只是简单地打印出类名、@符和一个十六进制的数字。所以我， 可以重写toString方法：    
```scala
class Rational(n: Int, d: Int) {
  override def toString: String = n + "/" + d
}
```    

***
#Checking-Preconditions    
　　现在，对主构造方法定义一个*前置条件（precondition）*——分母不能为0。前置条件是对传入方法或构造方法的值的约束，这是方法调用者必须要满足
的。实现这个的一种方式是用require（require方法定义在Predef这个独立对象中。所有的Scala源文件都会自动引入Predef的成员），就像这样：    
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  override def toString: String = n + "/" + d
}
```    
　　require方法接收一个boolean的参数。如果传入的参数为true，require会正常返回。否则，require将会抛出IllegalArgumentException来阻止
对象的构建。    

