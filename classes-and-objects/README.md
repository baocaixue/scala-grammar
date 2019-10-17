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
