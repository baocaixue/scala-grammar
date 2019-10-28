# 函数式对象    
- Rational类的规格定义...................................................[1](#A-Specification-For-Class-Rational)
- 构建Rational...................................................[2](#Construction-A-Rational)
- 重新实现toString方法...................................................[3](#Reimplement-The-ToString-Method)
- 检查前置条件...................................................[4](#Checking-Preconditions)
- 添加字段...................................................[5](#Adding-Fields)
- 自引用...................................................[6](#Self-Reference)
- 辅助构造方法...................................................[7](#Auxiliary-Constructors)
- 私有字段和方法...................................................[8](#Private-Fields-And-Methods)
- 定义操作符...................................................[9](#Defining-Operators)
- Scala中的标识符...................................................[10](#Identifiers-In-Scala)
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
## Checking-Preconditions    
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

***    
## Adding-Fields    
　　现在主构造器已经正确地保证了它的前置条件，我们将注意力转向如何支持加法。将给Rational类定义一个add方法，接收另一个Rational作为参数。为
了保持Rational不可变，这个add方法不能将传入的有理数加到自己身上，它必须创建并返回一个新的持有这两个有理数的和的Rational对象。但是，像如下
这段代码是不能编译的：    
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  override def toString: String = n + "/" + d
  def add(that: Rational): Rational = new Rational(n * that.d + that.n * d, d * that.d)
}
```    
　　虽然类add方法中参数n和d都在作用域内，但是只能访问执行add调用的那个对象上的n和d的值。因此，在add实现中用到n或d时，编译器会提供这些类参
数对应的值，但它不允许使用that.n或that.d，因为that并非指向执行add调用的那个对象。要访问 that的分子和分母，需要将它们做成字段：    
```scala
class Rational(n: Int, d: Int) {
  require(n !=0)
  val numer: Int = n
  val denom: Int = d
  override def toString: String = n + "/" + d
  def add(that: Rational): Rational = new Rational(this.numer * that.denom + that.numer * this.denom, this.denom * that.denom)
}
```    

***    

## Self-Reference    
　　关键字this指向当前执行方法的调用对象，当被用在构造方法里的时候，指向被构造的对象实例。举例来说，可以添加一个lessThan方法，来测试给定的
Rational是否小于某个传入的参数：    
```scala
def lessThan(that: Rational) = this.numer * that.denom < that.numer * this.denom
```    
　　在这里，this.numer和numer是等效的，但是下面的例子中this是不能省略的：添加max方法返回给定的有理数和参数之间较大的那个：    
```scala
def max(that: Rational): Rational = if (this.lessThan(that)) that else this
```    
　　在这里，第一个this是冗余的，完全可以不写this，直接写lessThan(that)。但第二个this代表了当测试返回false时该方法的结果;如果不写this，
就没有可返回的结果了。    

***    

## Auxiliary-Constructors    
　　有时需要给某个类定义多个构造方法。在Scala中，主构造方法之外的构造方法称为*辅助构造方法（auxiliary constructors）*。例如，一个分母为
1的有理数可以直接用分子表示，如5/1可以写成5。因此，Rational的使用方需要可以直接写成Rational(5)的支持。代码如下：    
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  val numer: Int = n
  val denom: Int = d

  def this(n:Int) = this(n, 1)//auxiliary constructor

  override def toString: String = n + "/" + d

  def add(that: Rational): Rational =
    new Rational(
      this.numer * that.denom + this.denom * that.numer,
      this.denom * that.denom
    )

  def lessThan(that: Rational): Boolean = this.numer * that.denom < that.numer * this.denom

  def max(that: Rational): Rational = if (this.lessThan(that)) that else this

}
```    
　　Scala的辅助构造方法以*def this(...)* 开始。在Scala中，每个辅助构造方法都必须首先调用同一个类的另一个构造方法。换句话说，Scala的每个
辅助构造方法的第一条语句都必须是这样的形式：“this(...)”。被调用的这个构造方法要么是主构造方法，要么是另一个出现在发起调用的构造方法之前的另
一个辅助构造方法。这个规则的净效应是Scala的每个构造方法最终都会调用到该类的主构造方法。这样一来，主构造方法就是这个类的单一入口。     

***    

## Private-Fields-And-Methods    
　　现在，我们定义一个最大公约数的私有方法，以做到分数的正规化：    
```scala
class Rational(n: Int, d: Int) {
  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer: Int = n / g
  val denom: Int = d / g

  def this(n:Int) = this(n, 1)//auxiliary constructor

  override def toString: String = numer + "/" + denom

  def add(that: Rational): Rational =
    new Rational(
      this.numer * that.denom + this.denom * that.numer,
      this.denom * that.denom
    )

  def lessThan(that: Rational): Boolean = this.numer * that.denom < that.numer * this.denom

  def max(that: Rational): Rational = if (this.lessThan(that)) that else this

  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}
```    
　　这个版本的Rational中，添加了一个私有字段g，并修改了numer和denom的初始化器。由于g是私有的，只能从类定义内部访问它。还添加了一个私有方
法gcd，计算传入的两个Int参数的最大公约数。    
　　Scala编译器会把Rational的三个字段的初始化代码按照它们在代码中出现的先后顺序依次编译进主构造方法中。也就是说，g的初始化器，gcd(n.abs,
d.abs)，会在另两个初始化器之前执行。    

***    

## Defining-Operators    
　　定义操作符    

***    

## Identifiers-In-Scala    
　　
