# 特质
- 特质如何工作...................................................[1](#How-Traits-Work)
- 瘦接口和富接口...................................................[2](#Thin-Versus-Rich-Interfaces)
- 示例：矩形对象...................................................[3](#Rectangular-Objects)
- Ordered特质...................................................[4](#Order-Trait)
- 作为可叠加修改的特质...................................................[5](#Traits-As-Stackable-Modifications)
- 为什么不用多重继承...................................................[6](#Why-Not-Multiple-Inheritance)
- 要特质还是不要特质...................................................[7](#To-Trait-Or-Not-To-Trait)    
    
　　特质是Scala代码复用的基础单元。特质将方法和字段定义封装起来，然后通过将它们混入（mix in）类的方式来实现复用。它不同于类继承，类继承要求
每个类都继承自一个（明确）的超类，而类可以同时混入任意数量的特质。这里将展示特质的工作原理并给出两种最常见的适用场景：将“瘦”接口拓宽为“富”接
口，以及地难以可叠加的修改。以及使用Ordered特质，特质和其他语言中多重继承的对比。    

***    
## How-Traits-Work    
　　特质的定义和类定义很像，除了关键字**trait**：    
```scala
trait Philosophical {
  def philosophize() = {
    println("I consume memory, therefore I am!")
  }
}
```    
　　该特质名为Philosophical。它并没有声明一个超类，因此跟类一样，有一个默认的超类AnyRef。它定义一个名为philosophize的方法，这个方法是具
体的。这是一个简单的特质，只是为了展示特质的工作原理。    
　　一旦特质被定义好，就可以用extends或with关键字将它**混入**到类中。Scala中是*混入（mix in）* 特质，而不是从特质继承，因为混入特质跟其
他许多编程语言中的多重继承有重要的区别。如下，用extends混入了Philosophical特质：    
```scala
class Frog extends Philosophical {
  override def toString: String = "green"
}
```    
　　可以用extends关键字来混入特质，在这种情况下隐式地继承了特质的超类。例如，在上面代码中，Frog类是AnyRef的子类（因为AnyRef是Philosophical
的超类），并且混入了Philosophical。从特质继承的方法跟从超类继承的方法用起来一样：    
```shell script
scala> val frog = new Frog
frog: Frog = green
scala> frog.philosophize()
I consume memory, therefore I am!
```    
　　特质同时也定义了一个类型。以下是Philosophical被用作类型的例子：    
```shell script
scala> val phil: Philosophical = frog
phil: Philosophical = green
scala> phil.philosophize()
I consume memory, therefore I am!
```    
　　这里phil的类型是Philosophical，这是一个特质。因此，变量phil可以由任何混入了Philosophical的类的对象初始化。    
　　如果想要将特质混入一个显式继承自某个超类的类，可以用extends来给出这个超类，并用with来混入特质。如果想要混入多个特质，可以用with子句进
行添加。    
```scala
class Animal
trait  Philosophical
trait HasLegs

class Frog1 extends Animal with Philosophical {
  override def toString = "green"
}

class Frog2 extends Animal with Philosophical with HasLegs {
  override def toString = "green"
}
```    
　　目前为止，Frog类从Philosophical特质继承了philosophize的实现。Frog也可以重写philosophize。重写的语法跟重写超类中声明的方法看上去一
样。    
```scala
class Animal
trait Philosophical {
  def philosophize() = {
    println("I consume memory, therefore I am!")
  }
}
class Frog extends Animal with Philosophical {
  override def toString: String = "green"
  override def philosophize() = {
    println("It ain't easy being" + toString + "!")
  }
}
```    
　　由于这个新的Frog定义依然混入了Philosophical特质，仍然可以用同一个类型的变量使用它。不过由于Frog重写了Philosophical的philosophize
实现，当调用这个方法时，将得到新的行为。    
　　至此，感觉特质很像是拥有具体方法的Java接口，不过它们能做的实际上远不止这些。比方说，特质可以声明字段并保持状态。事实上，在特质定义中可以
做任何在类定义中做的事情，语法也完全相同，除了以下两种情况：    
　　首先，特质不能有任何“类”参数（即那些传入类的主构造方法的参数）。    
　　另一个类和特质的区别在于类中的super调用是静态绑定的，而在特质中super是动态绑定的。如果在类中编写`super.toString`这样的代码，可以确切
知道实际调用的是那一个实现。在定义特质的时候并没有被定义。具体是哪个实现被调用，在每次该特质被混入到某个具体的类时，都会重新判定。这里的super
看上去有些奇特的行为是特质能实现*可叠加修改（stackable modification）* 的关键。    

***    
## Thin-Versus-Rich-Interfaces    
　　特质的一个主要用途是自动给类添加基于已有方法的新方法。也就是说，特质可以丰富一个*瘦*接口，让它成为*富*接口。瘦接口和富接口代表了我们在
面向对象设计中经常面临的取舍，在接口实现者和使用者之间的权衡。富接口有很多方法，对于调用方而言十分方便。使用者可以选择完全匹配他们需求的功能
的方法。而瘦接口的方法较少，因而实现起来更容易。不过瘦接口的使用方需要编写更多的代码。由于可供选择的方法较少，他们可能被迫选择一个不那么匹配
需求的方法，然后编写额外的代码来使用它。    
　　Java接口通常比较瘦。例如，Java 1.4引入的CharSequence接口就是一个对所有包含一系列字符的类似字符串的类的通用瘦接口。如下是以Scala的视
角看到的定义：    
```scala
trait CharSequence {
  def charAt(index: Int): Char
  def length: Int
  def subSequence(start: Int, end: Int): CharSequence
  def toString: String
}
```    
　　虽然String类的大部分方法都适用于CharSequence，Java的CharSequence接口仅声明了四个方法。而如果CharSequence接口包括了完整的String
接口方法，又势必会给CharSequence的实现者带来巨大的负担。每个用Java实现CharSequence的程序员又要多实现数十个方法。由于Scala的特质能包含具
体方法，这让编写富接口变得方便得多。    
　　给特质添加具体方法让瘦接口和富接口之间的取舍变得严重倾向于富接口。不同于Java，给Scala特质添加具体的方法是一次性的投入。只需要在特质中实
现这些方法一次，而不需要在每个混入该特质的类中重新实现一遍。因此，跟其他没有特质的语言相比，Scala中实现富接口的代价更小。    
　　要用特质来丰富某个接口，只需要定义一个拥有为数不多的抽象方法（接口中瘦的部分）和可能数量很多的具体方法（这些具体方法基于那些抽象方法编写）
的特质。然后，就可以将这个增值特质混入到某个类，在类中实现接口中瘦的部分，最终得到一个拥有完整富接口实现的类。    

***    
## Rectangular-Objects    
　　图形类库通常有许多不同的类来表示矩形。例如窗体、位图图片，以及用鼠标圈定的区域等。为了让这些矩形对象更加易于使用，我们的类库最好能提供一
些坐标相关的查询，比如width、height、left、right、toLeft等。不过，存在很多这样的方法是有很多好处，但对于类库编写者而言，在Java类库中为
所有矩形对象提供全部方法是个巨大的负担。作为对比，如果这样的类库是Scala编写的，类库作者就可以用特质来轻松地对所有想要这些功能的类加上这些
便利方法。    
　　首先，可以设想一下不用特质的情况，代码会什么样子。应该会有某种基本的几何类，比如Point和Rectangle：    
```scala
class Point(val x: Int, val y: Int)

class Rectangle(val topLeft: Point, val bottomRight: Point) {
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
  //以及更多几何方法...
}
```    
　　图形库可能还会有另一个类是2D图形组件：    
```scala
abstract class Component {
  def topLeft: Point
  def bottomRight: Point 
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
}
```    
　　注意两个类的left、right和width的定义完全一致。对于任何其他表示矩形对象的类，处理细微差异外，这些方法也会是相同的。这些重复的代码可以用
特质来消除。这个特质会包含两个抽象方法：一个返回对象左上角的坐标，另一个返回右下角的坐标。然后它可以提供所有其他集合查询相关方法的具体实现。    
```scala
trait Rectangular {
  def topLeft: Point 
  def bottomRight: Point 
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
} 
```     

***    
## Order-Trait    
　　比较（对象大小）是另一个富接口带来便捷的领域。当需要比较两个对象来对它们排序时，如果有这么一个方法可以调用来明确你要的比较，就会很方便。
如果你要的是“小于”，可以说<，而“小于等于”可以说<=。如果用一个瘦的比较接口，可能只能用<方法，而有时可能需要编写类似`(x < y) || (x == y)`
这样的代码。而一个富接口可以提供所有常用的比较操作，这样就可以直接写下如同`x <= y`这样的代码。    
　　在看Order具体实现之前，我们完成比较操作可能作出类似这样的代码（使用Rational类）：    
```scala
class Rational(n: Int, d: Int) {
  //...
  def <(that: Rational) = this.numer * that.denom < that.numer * this.denom
  def >(that:Rational) = that < this
  def <=(that: Rational) = (this < that) || (this == that)
  def >=(that: Rational) = (this > that) || (this == that)
}
```    
　　这个类定义了四个比较操作符，这是个经典的展示出定义富接口代价的例子。首先，注意其中的三个比较操作符都是基于地一个来定义的，注意所有的
这三个方法对于任何其他可以被比较的类来说都是一样的。对于有理数而言，在`<=`的语义方面，没有任何的不同。在比较的上下文中，<=总是被用来表示
“小于或等于”。总体来说，这个类里有相当多的样板代码，在其他实现了比较操作的类中不会与此有什么不同。    
　　这个问题如此普遍，Scala提供了专门的特质来解决。这个特质叫做**Ordered**。使用的方式是将所有单独的比较方法替换成*compare*方法。Ordered
特质定义了<、>、<=和>=，这些方法都是基于提供的compare来实现的。因此，Ordered允许只实现一个compare方法来增强某个类，让它拥有完整的比较操作。    
　　以下是用Ordered特质来对Rational定义比较操作的代码：    
```scala
class Rational(n: Int, d: Int) extends Ordered[Rational] {
  override def compare(that:  Rational): Int =  (this.numer * that.denom) - (that.numer * this.denom)
}
```    
　　你只需要做两件事。首先，这个版本的Rational混入了Ordered特质。与之前的其他特质不同，Ordered要求在混入时传入一个*类型参数*;需要做的第
二件事是定义一个用来比较两个对象的compare方法，该方法应该比较接收者，即this，和作为参数传入该方法的对象。如果两个对象相同，它应该返回0，如
果接收者比入参小，应该返回负值，如果接收者比入参大，则返回正值。    
　　要小心Ordered特质并不会帮你定义equals方法，因为它做不到。这当中的问题在于用compare来实现equals需要检查传入对象的类型，而由于（Java的）
类型擦除机制，Ordered特质自己无法完成这个检查。因此需要定义equals方法。    

***    
## Traits-As-Stackable-Modifications    
　　前面介绍的是特质的一个主要用途：*将瘦接口转化成富接口*。现在我们将转向另一个主要用途：*为类提供可叠加的修改*。特质让你修改类的方法，而它
们的实现方式允许你将这些修改叠加起来。    
　　考虑这样一个例子，对于某个整数队列叠加修改。这个队列有两个操作：put，将整数放入队列;get，将它们取出来。队列是先进先出的，所以get应该按
照整数被放入队列的顺序返回这些整数。    
　　给定一个实现了这样一个队列的类，可以定义特质来执行如下这些修改：    
* Doubling：将所有放入队列的整数翻倍
* Incrementing：将所有放入队列的整数加一
* Filtering：从队列中去除负数    
　　这三个特质代表了*修改（modification）*，因为他们修改底下的队列类，而不是自己定义完整的队列类。这三个特质也是*可叠加的（stackable）*。
可以从这三个特质中任意选择，将它们混入类，并得到一个带上你选择的修改的新的类。    
　　示例中给出了一个抽象的IntQueue类。而BasicIntQueue使用ArrayBuffer对IntQueue实现：    
```scala
abstract class IntQueue {
  def get(): Int
 
  def put(x: Int)
}

import scala.collection.mutable.ArrayBuffer
class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  
  def get() = buf.remove(0)

  def put(x: Int) = buf += x
}
```    
　　现在来看看如何用特质修改上述的行为。下面示例给除了在放入队列时对整数翻倍的特质。Doubling特质有两个好玩的地方。首先它声明了一个超类IntQueue。
这个声明意味着这个特质只能被混入同样继承自IntQueue的类。因此，可以将Doubling混入BasicIntQueue。    
```scala
trait Doubling extends IntQueue {
  abstract override def put(x: Int) = super.put(2 * x)
}
```    
　　第二个好玩的地方是该特质有在一个声明为抽象的方法里做了super调用。对于普通的类而言这样的调用是非法的，因为在运行时必定会失败。不过对于特
质而言，这样的调用实际上可以成功。由于特质中的super调用是动态绑定的，只要在给出了方法具体定义的特质或类之后混入，Doubling特质里的super调用
就可以正常工作。    
　　对于实现可叠加修改的特质，这样的安排通常是需要的。为了告诉编译器你是特意这样做的，必须将这样的方法标记为*abstract override*。这样的修
饰符组合只允许用在特质的成员上，不允许用在类的成员上，它的含义是该特质必须混入某个拥有该方法具体定义的类中。    
　　对于这样一个简单的特质而言，是不是有很多事情发生（在幕后）？这个特质用起来是这样的 ：    
```shell script
scala> class MyQueue extends BasicIntQueue with Doubling
defined class MyQueue
scala> val queue = new MyQueue
queue: MyQueue = MyQueue@44bbf788
scala> queue.put(10)
scala> queue.get()
res12: Int = 20
```    
　　注意，MyQueue并没有定义新的代码，只是简单地给出一个类然后混入一个特质。在这种情况下，可以在用new实例化的时候直接给出“BasicIntQueue 
with Doubling”，而不是定义一个有名字的类：    
```scala
val queue = new BasicIntQueue with Doubling
```    
　　下面给出另外两个修改特质，Incrementing和Filtering：    
```scala
trait Incrementing extends IntQueue {
  abstract override def put(x: Int) = super.put(x+1)
}
trait Filtering extends IntQueue {
  abstract override def put(x: Int) = {
    if (x >= 0) super.put(x)
  }
}
```    
　　有了这些修改特质，现在可以为特定的队列挑选想要的修改。举例来说，下面是一个既过滤掉负数同时还对所有数字加一的队列：    
```scala
val queue = new BasicIntQueue with Incrementing with Filtering
```    
　　*混入特质的顺序是重要的*，粗略地讲，越靠右出现的特质越先起作用。当你调用某个带有混入的类的方法时，最靠右端的特质中的方法最先被调用。如果
那个方法调用super，它将调用左侧紧挨着它的那个特质的方法，以此类推。     

***    
## Why-Not-Multiple-Inheritance    
　　特质是一种从多个像类一样的结构继承的方式，不过它们跟许多其他语言中的多重继承有着很重大的区别。其中一个尤为重要：对super的解读。在多重继
承中，super调用的方法在调用发生的地方就已经确定了。而特质中的super调用的方法取决于类和混入该类的特质的**线性化（linearization）**。正是
这个差别让可叠加修改变为了可能。    
　　考虑下面传统的多重继承的语言中要如何实现可叠加修改。     
```scala
//多重继承思维实验
val q = new BasicIntQueue with  Incrementing with Doubling
q.put(42)//应该会调用哪个put？
```    
　　第一个问题：这次执行的是哪一个put方法？也许规则是最后一个超类胜出，那么在本例中Doubling的put会被执行。Doubling于是对其参数翻倍，调用
super.put，然后就结束了。不会有加一发生！同理，如果规则是首个超类胜出，那么结果的队列将对整数加一，但不会翻倍。这么一来没有一种顺序是可行的。    
　　也许还可以尝试这样一种可能：让程序员自己指定调用super时到底是用哪一个超类的方法。例如，假设有下面这段Scala的代码，在这段代码中，super
看上去显式地调用了Incrementing和Doubling:    
```scala
//多重继承思维实验
trait MyQueue extends BasicIntQueue with Incrementing with Doubling {
  def put(x: Int) = {
    Incrementing.super.put(x)//并非真正的scala代码
    Doubling.super.put(x)
  }
}
```    
　　这种方法会带来新的问题（相比这些问题，代码啰嗦点根本不算什么），这样做可能发生的情况是基类的put方法被调用两次：一次在加一的时候，另一次
在翻倍的时候，不过两次都不是用加过一或翻过倍的值调用的。    
　　简单来说，多重继承对这类问题并没有好的解决方案。需要回过头来重新设计，重新组织代码。相比较而言，用Scala特质的解决方案是很直截了当的，只
需要简单地混入Incrementing和Doubling，Scala对特质中super的特殊处理完全达到了预期的效果。这种方案和多重继承相比，很显然有某种不一样，但是
这个区别究竟是什么？    
　　前面提到了，答案是*线性化*。当用new实例化一个类的时候，Scala会将类及它所有继承的类和特质都拿出来，将它们线地排列在一起。然后，当在某个
类中调用super时，被调用的方法是这个链条中向上最近的那一个。如果除了最后一个方法外，所有的方法都调用了super，那么最终的结果就是叠加在一起的
行为。    
　　线性化的确切顺序在语言规格说明书里有描述。这个描述有点复杂，不过需要知道的要点是，在任何线性化中，类总是位于所有它的超类和混入的特质之前。
因此，当写下super的方法时，那个方法绝对是在修改超类和混入特质的行为。    
　　Scala的线性化的主要性质可以用下面的例子来说明：假定有一个Cat类，这个类继承自超类Animal和两个超特质Furry和FourLegged。而FourLegged
又扩展自另一个特质HasLegs:    
```scala
class Animal
trait Furry extends Animal
trait HasLegs extends Animal
trait FourLegged extends HasLegs
class Cat extends Animal with Furry with FourLegged
```    
　　Cat类的继承关系和线性化如图所示。继承使用传统的UML表示法标记的：白色三角箭头表示继承，其中箭头的指向是超类型。黑色的箭头表示线性化，其中
箭头指向的是super调用的解析方向。    
　　![image](https://raw.githubusercontent.com/baocaixue/scala-grammar/master/traits/src/main/resources/Cat.jpg)    
　　Cat的线性化从后到前的计算过程如下。Cat线性化的最后一个部分是其超类Animal的线性化。这段线性化直接被复制过来不加修改。由于Animal并不显式
地扩展某个超类也没有混入任何超特质，它默认扩展自AnyRef，而AnyRef又扩展自Any。这样Animal的线性化看上去就是这样的：    
　　Animal --> AnyRef --> Any    
　　线性化的倒数第二个部分是首个混入（即Furry特质）的线性化，不过有已经出现在Animal线性化中的类都不再重复出现，*每个类在Cat的线性化当中只
出现一次*。结果是：    
　　Furry --> Animal --> AnyRef --> Any    
　　在这个结果之前，是FourLegged的线性化，同样，任何已经在超类或已混入中拷贝过的类都不再重复出现：    
　　FourLegged -> HasLegs -> Furry -> Animal --> AnyRef --> Any    
　　最后，Cat线性化中的第一个类是Cat自己：    
　　Cat --> FourLegged -> HasLegs -> Furry -> Animal --> AnyRef --> Any    

***    
## To-Trait-Or-Not-To-Trait    
　　当实现某个可复用的行为集合时，都需要决定是用特质还是抽象类。对这个决定，并没有某种确定的规则，不过可以有一些参考意见。    
　　*如果某个行为不会被复用*，用具体的类。毕竟它并不是可复用的行为。    
　　*如果某个行为可能被用于多个互相不相关的类*，用特质。只有特质才能被混入类继承关系中位于不同组成部分的类。    
　　*如果想要从Java代码中继承某个行为*，用抽象类。由于带有实现的特质并没有与之贴近的Java类比，因此从Java类继承特质会比较别扭。不过从Java类
继承Scala类跟继承Java类几乎一样。不过有一个例外，如果某个Scala特质只有抽象方法，它会被直接翻译成Java的接口，因此可以放心定义这样的特质，哪
怕预期会有Java代码继承自它。    
　　*如果计划将某个行为以编译好的形式分发*，且预期会有外部的组织编写继承自它的类，可能会倾向于使用抽象类。这里的问题在于当某个特质增加或减少
成员时，任何继承自该特质的类都需要被重新编译，哪怕它们并没有改变。如果外部的使用方只是调用到这个行为，而不是继承，那么使用特质也好似可以的 。    
　　*如果考虑了上述所有问题后，仍然没有答案，优先使用特质*。
