# 类和对象    
- 类、字段和方法...................................................[1](#Classes-Fields-Methods)
- 分号推断...................................................[2](#Semicolon-Inference)
- 单例对象...................................................[3](#Singleton-Objects)
- Scala应用程序...................................................[4](#Scala-Application)
- App特质...................................................[5](#App-Trait)    

***
## Classes-Fields-Methods    
　　类是对象的蓝本。一旦定义好一个类，就可以用new关键字从这个蓝本创建对象。例如，有了下面这个类的定义：    
```scala
class ChecksumAccumulator {
  //这里是类的定义
}
```    
　　就可以用这样的代码创建ChecksumAccumulator的对象：`new ChecksumAccumulator`    

　　在类的定义中，你会填入字段（field）和方法（method），这些被统称为*成员*。通过val或var定义的字段是指向对象的变量，通过def定义的方法则
包含了可执行的代码。字段保留了对象的状态，或者说数据，而方法用这些数据来对对象执行计算。当实例化一个类，运行时会指派一些内存来保存对象的状态
图（即它的变量内容）。例如，定义了一个ChecksumAccumulator类并给它一个名为sum的var字段：    
```scala
class ChecksumAccumulator {
  var sum = 0
}
```    
　　然后用如下代码实例化两次：    
```scala
val acc = new ChecksumAccumulator
val csa = new ChecksumAccumulator
```    

　　这要注意的一点是一共有两个sum变量，一个位于acc指向的对象里，而另一个位于csa指向的对象里。字段又叫做*实例变量（instance variable）*，
因为每个实例都有自己的变量。这些实例变量合在一起，构成了对象在内存中的映像。上面例子中，不光有两个sum变量，而且当改变其中一个值的时候，另一个
不会受到影响。    

　　本例中另一个值得注意的是可修改acc指向的对象。尽管acc本身是val，由于acc和csa都是val而不是var，不能做的是将它们重新赋值指向别的对象。因此
能够确信的是，acc永远指向初始化的时候用到ChecksumAccumulator对象，但随着时间推移这个对象中包含的字段是有可能改变的。    

　　追求健壮性的一个重要手段是*确保对象的状态（它的实例变量的值）在整个生命周期都是有效的*。首先是通过将字段标记为*私有（private）*来防止外
部直接访问字段。因为私有字段只能被定义在同一个类中的方法访问，所有对状态的更新操作的代码都在类的内部。要将某个字段声明为私有，可以在字段前加上
private这个访问修饰符，如：    
```scala
class ChecksumAccumulator {
  private var sum = 0
}
```    

　　有了ChecksumAccumulator的定义，任何试图通过外部访问sum的操作都会失败。    
　　**注意：**    
　　在Scala中，使得成员允许公共访问（public）的方式是不在成员前面显式地给出任何访问修饰符。换句话说，对于那些在Java中可能会用“public”的地
方，到了Scala中，什么都不说就对了。*公共访问是Scala的默认访问级别*。    

　　由于sum是私有的，唯一能访问sum的代码都定义在类自己里面。因此，ChecksumAccumulator对于别人来说没什么用处，除非给它定义一些方法：    
```scala
class ChecksumAccumulator {
  private var sum = 0

  def add(b: Byte): Unit = {
    sum += b
  }

  def checksum(): Int = {
    return ~(sum & 0xFF) + 1
  }
}
```    

　　ChecksumAccumulator现在有两个方法，add和checksum，都是函数定义的基本形式。传递给方法的任何参数都能在方法内部使用。Scala方法参数的一个
重要特征是**它们都是val而不是var**。因此，Scala方法参数不能被重新赋值。    

　　虽然当前版本的ChecksumAccumulator中，add和checksum正确地实现了预期的功能，还可以用更精简的风格来表达。首先，checksum方法最后的
return是多余的，可以去掉。在没有任何显式的return语句时，Scala方法返回的是该方法计算出的最后一个（表达式）的值。    

　　事实上推荐的方法风格是避免使用任何显式的return语句，尤其是多个return语句。与此相反，尽量将每个方法当做是一个最终交出某个值的表达式。这样
的哲学鼓励你编写短小的方法，将大的方法拆成小的。另一方面，设计中的选择也是取决于上下文的，Scala也允许你方便地编写有多个显式return的方法，如
果那确实是你想要的。    

　　由于checksum所做的全部就是计算一个值，它并不需要显式的return。另一种方法简写的方式是，当一个方法只会计算一个返回结果的表达式时，可以不
写花括号。如果这个表达式很短，它甚至可以被放置在def的同一行。为了极致的精简，还可以省略掉结果类型，Scala会帮你推断出来。做完这些修改后，代码
如下所示：    
```scala
class ChecksumAccumulator {
  private var sum = 0
  def add(b: Byte) = sum += b
  def checksum() = ~(sum & 0xFF) + 1
}
```    

　　在前面的示例中，虽然Scala能够正确地推断出add和checksum这两个方法的结果类型，这段代码的读者也需要通过研读方法体中的代码才能推断出结果
类型。正因如此，通常更好的做法是对类中声明为共有的方法显式地给出结果类型，哪怕编译器能够帮你推断出来。下面代码展示了这种风格：    
```scala
class ChecksumAccumulator {
  private var sum = 0
  def add(b: Byte): Unit = {sum += b}
  def checksum(): Int = ~(sum & 0xFF) + 1
}
```    

　　结果类型为Unit的方法，如ChecksumAccumulator的add方法，执行它们的目的是为了它们的副作用。副作用通常来说指改变方法外部的某种状态或者执行
I/O的动作。对本例的add而言，其副作用是给sum重新赋值。那些仅仅因为其副作用而被执行的方法被称作*过程（procedure）*。    

***    
## Semicolon-Inference    
　　在Scala程序中，每条语句最后的分号通常是可选的。可以选择键入分号，但如果当前行只有这条语句，分好并不是必须的。另一方面，如果想在同一行包含
多条语句，那么分号就有必要了：`val s = "hello"; println(s)`    
　　如果想要一条跨多行的语句，大多数情况下直接换行即可，Scala会帮助你在正确的地方断句。例如，如下代码会被当作一条四行的语句处理：    
```scala
if (x < 2)
    println("too small")
else 
    println("ok")
```    
　　不过偶尔Scala也会背离你的意图，在不该断句的地方断句：    
```scala
x
+ y
```     
　　这段代码会被解析成两条语句x和+y。如果希望编译器解析成单条语句x + y ，可以把语句包在圆括号里：    
```scala
(x
+ y)
```    
　　或者也可以将+放在行尾。正是由于这个原因，当用中缀（infix）操作符比如+来串接表达式时，一个常见的Scala风格是将操作符放在行尾而不是行首：    
```scala
x +
y +
z
```    
　　**分号推断规则：**    
　　相比分号推断的效果，（自动）分隔语句的精确规则简单得出人意料。概括地说，除非以下任何一条为true，代码行末尾就会被当做分号处理：    
　　1. 当前行以一个不能作为语句结尾的词结尾，比如英文句点或中缀操作符。    
　　2. 下一行以一个不能作为语句开头的词开头。    
　　3. 当前行的行尾出现在圆括号(...) 或方括号 \[...\]内。    

***    
## Singleton-Objects    
　　Scala比Java更面向对象的一点，是Scala类不允许有静态（static）成员。对此类使用场景，Scala提供了*单例对象（singleton object）*。单例
对象的定义看上去跟类定义很像，只不过class关键字换成了**object**关键字。参考示例如下：    
```scala
//位于ChecksumAccumulator.scala文件中
import scala.collection.mutable
object ChecksumAccumulator {
  private val cache = mutable.Map.empty[String, Int]

  def calculate(s: String): Int = {
    if (cache.contains(s))
      cache(s)
    else {
      val acc = new ChecksumAccumulator
      for (c <- s)
        acc.add(c.toByte)
      val cs = acc.checksum()
      cache += (s -> cs)
      cs
    } 
  }
}
```    
　　示例中单例对象名叫ChecksumAccumulator，跟前面一个例子中的类名一样。**当单例对象跟某个类共用同一个名字时，它被乘坐这个类的伴生对象（
companion object）**。必须在同一个源码文件中定义类和类的伴生对象。同时，类又叫做这个单例对象的伴生类（companion class）。类和它的伴生对象
可以互相访问对方的私有成员。    

　　ChecksumAccumulator单例对象有一个名为calculate的方法，接收一个String，计算这个String的所有字符的校验和（checksum）。它同样有一个
私有的字段，cache，这是一个缓存了之前已计算过的校验和。    

　　可以把单例对象当做是用于安置那些用Java时打算编写的静态方法。可以用类似的方式来访问单例对象的方法：单例对象名、英文句点和方法名。例如：`
ChecksumAccumulator.calculate("Every value is an object.")`    

　　不过，单例对象并不仅仅是用来存放静态方法。它是一等的对象。可以把单例对象的名称想象成附加在对象身上的“名字标签”。    
　　定义单例对象并不会定义类型（在Scala的抽象层级上是这样的）。当只有ChecksumAccumulator的对象定义时，并不能定义一个类型为ChecksumAccumulator
的变量。确切的说，名为ChecksumAccumulator的类型是由这个单例对象的伴生类来定义的。不过，单例对象可以扩展自某个超类，还可以混入特质，可以通过
这些类型来调用它的方法，用这些类型的变量来引用它，还可以将它传入那些预期这些类型的入参的方法当中。    

　　类和单例对象的一个区别是单例对象不接收参数，而类可以。由于没法用new实例化单例对象，也就没有任何手段向它传参。每个单例对象都是通过一个静态
变量引用合成类（synthetic class）的实例来实现的，因此单例对象从初始化的语义上和静态成员是一致的。尤其体现在，单例对象代码首次访问时才被初始
化。    

　　没有同名的伴生类的单例对象称为*孤立对象（standalone object）*。孤立对象有很多用途，包括将工具方法归集在一起，或定义Scala应用程序的入口
等。    

*** 