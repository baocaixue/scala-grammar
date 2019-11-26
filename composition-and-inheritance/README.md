# 组合和继承
- 一个二维的布局类库...................................................[1](#A-TwoDimensional-Layout-Library)
- 抽象类...................................................[2](#Abstract-Classes)
- 定义无参方法...................................................[3](#Defining-Parameterless-Methods)
- 扩展类...................................................[4](#Extending-Classes)
- 重写方法和字段...................................................[5](#Overriding-Methods-And-Fields)
- 定义参数化字段...................................................[6](#Defining-Parametric-Fields)
- 调用超类构造方法...................................................[7](#Invoking-Superclass-Constructors)
- 使用override修饰符...................................................[8](#Using-Override-Modifiers)
- 多态和动态绑定...................................................[9](#Polymorphism-And-Dynamic-Binding)
- 声明final成员...................................................[10](#Declaring-Final-Members)
- 使用组合和继承...................................................[11](#Using-Composition-And-Inheritance)
- 实现above、beside和toString...................................................[12](#Implementing-Above-Beside-And-Tostring)
- 定义工厂对象...................................................[13](#Defining-A-Factory-Object)
- 增高和增宽...................................................[14](#Heighten-And-Widen)
- 放在一起...................................................[15](#Putting-It-All-Together)    

***    
## A-TwoDimensional-Layout-Library    
　　这里将创建一个用于构建和渲染二维布局元素的类库作为示例。每个元素表示一个用文本填充的长方形。为方便起见，类库将提供名为“elem”的工厂方法，
从传入的数据构造新的元素。例如，可以用下面这个签名的工厂方法传见一个包含字符串的布局元素：     
```scala
def elem(s: String): Element
```    
　　用一个名为Element的类型来对元素建模。可以对一个元素调用above或beside，传入另一个元素，来获取一个将两个元素结合在一起的新元素。例如，
下面这个表达式将创建一个由两列组成的更大的元素，每一列的高度都为2：    
```scala
val column1 = elem("hello") above elem("***")
val column2 = elem("***") above elem("world")
column1 beside column2
```    
　　打印上述表达式的结果将得到：    
```
hello ***
*** world
```    
　　布局元素很好地展示了这样一个系统：在这个系统中，对象可以通过组合操作符的帮助由简单的部件构建出来。这里将定义那些可以从数组、线和巨型构造
出元素对象的类，这些基础的元素对象是我们说的简单部件，我们还会定义组合操作符above和beside。这样的组合操作符通常也被称作组合子，因为它们将
某个领域内的元素组合成新的元素。    
　　用组合子来思考通常是一个设计类库的好办法：对于某个特定的应用领域中对象，它们有那些基本的构造方式，这样的思考是很有意义的。简单的对象如何
构造出更有趣的对象？如何将组合子有机地结合在一起？最通用的组合有哪些？它们是否满足某种有趣的法则？如果对这些问题都有很好的答案，那么你的类库
设计就走在正轨上。    

***    
## Abstract-Classes    
　　首先要定义Element类型，用来表示元素。由于元素是一个由字符组成的二维矩阵，用一个成员contents来表示某个布局元素的内容。内容可以用字符串
的数组表示，每个字符串代表一行。因此，由contents返回的结果类型将会是Array[String]：    
```scala
abstract class Element {
  def contents: Array[String]
}
```    
　　这个类中，contents被声明为一个没有实现的方法。换句话说，这个方法是Element类的*抽象*成员。一个包含抽象成员的类本身也要声明为抽象的，做
法是在class关键字前加上abstract修饰符，和Java一样抽象类也是不能直接实例化的。可以创建Element的子类，这些子类是可以实例化的。    
　　需要注意，Element类中的contents方法并没有标上abstract修饰符。一个方法只要没有实现（即没有等号或方法体），那么它就是抽象的。跟Java不
同，不需要（也不能）对方法加上abstract修饰符。那些给出了实现的的方法叫做*具体（concrete）* 方法。另一组在叫法上的区分是*声明*和*定义*。
Element类声明了contents这个抽象方法，但目前没有定义具体的方法。下面，将通过定义一些具体的方法来增强Element。    

***    
## Defining-Parameterless-Methods
　　现在，我们将给Element添加方法来获取它的宽度和高度，如下所示。height方法返回contents中的行数。而width方法返回第一行的长度，如果完全
没有内容则返回0。    
```scala
abstract class Element {
  def contents: Array[String]
  def height: Int = contents.length
  def width: Int = if (height == 0) 0 else contents(0).length
}
```    
　　注意，Element的三个方法无一例外都没有参数列表，连空参数列表都没有。这样的*无参方法（parameterless method）* 在Scala中很常见。于此对
应，那些用空的圆括号定义的方法，如`def height(): Int`被称作*空圆括号方法（empty-paren method）* 。推荐的做法是*对没有参数且只通过读取
所在对象字段方式访问可变状态（确切地说不改变状态）的情况下尽量使用无参方法*。这样的做法支持所谓的*统一访问原则*：使用方代码不应该收到某个属
性是用字段还是用方法实现的影响。    
　　举例来说，完全可以吧width和height实现成字段，而不是方法，只要将定义中的def换成val即可：    
```scala
abstract class Element {
  def contents: Array[String]
  val height: Int = contents.length
  val width: Int = if (height == 0) 0 else contents(0).length
}
```    
　　从使用方代码看，这组定义完全等价。唯一的区别是字段访问可能比方法调用略快，因为字段值在类初始化时就被预先计算好，而不是每次方法调用时重新
计算。另一方面，字段需要每个Element对象为其分配额外的内存空间。因此属性实现为字段好还是方法好，这个问题取决于类的用法，而用法可以随着时间变
化而变化的。核心点在于Element类的使用方不应爱被内部实现的变化所影响。具体来说，当Element的某个字段被改成访问函数时，Element的使用方代码不需要被重新编写，只要这个访问函数是纯的（即它没有副作用也不依赖于可变
状态）。使用方代码不需要关心究竟是哪一种实现。    
　　但现在有一个问题，这跟Java处理细节有关。Java并没有实现统一访问原则。因此Java中要写`string.length()`而不是`string.length`，而对于
数组要写`array.length`而不是`array.length()`，这让人很困扰。为了更好地桥接这两种写法，Scala对于混用无参方法和空括号方法的处理非常灵活。
具体来说，可以用空括号方法重写无参方法，反过来也可以。还可以在调用某个不需要入参的方法时省去空括号。    
　　从原理上讲，可以对Scala所有无参函数调用都去掉空括号。不过，仍然建议在被调用的方法不仅代表接收该调用的对象的某个属性时加上空括号。举例来说，
空括号的场景包括该方法执行I/O、写入可冲ixn赋值的变量var、读取接收该调用对象字段之外的var（不论是直接还是间接地使用了可变对象）。这样以来，
参数列表就可以作为一个视觉上的线索，告诉我们调用触发了某个有趣的计算。    
　　总结下来就是，Scala鼓励我们将那些不接收参数也没有副作用的方法定义为无参方法。同时，对有副作用的方法，不应该省去空括号。    

***    
## Extending-Classes    
　　我们仍然需要某种方式创建新的元素对象。已知“new Element”是不能用的，因为Element类是抽象的。因此，要实例化一个元素，需要创建一个扩展自
Element的子类，并实现contents这个抽象方法：    
```scala
class ArrayElement(conts: Array[String]) extends Element {
  def contents: Array[String] = conts
}
```    
　　ArrayElement类被定义为扩展自Element类。跟Java一样，可以在类名后用extends子句来表达。这样的extends子句有两个作用：它使得ArrayElement
类从Element类继承所有非私有成员，并且它也让ArrayElement的类型成为Element类型的子类型。由于ArrayElement扩展自Element，ArrayElement类
被称作Element类的子类。反过来讲，Element是ArrayElement的超类。如果去掉extends子句，Scala编译器会默认假定你的类扩展自**scala.AnyRef**
这对应到Java平台跟*java.lang.Object*相同。因此，Element类默认也扩展自AnyRef类。    
　　*继承（inheritance）* 的意思是超类的所有成员也是子类的成员，但是有两个例外：一个是私有成员并不会被子类继承;二是如果子类里已经实现了相同
名称和参数的成员，那么该成员不会被继承（*重写*）。而对于超类中的抽象成员，子类需要*实现*这个抽象的成员。    
　　例如,ArrayElement里的contents方法实现了Element的抽象方法contents。与此不同的是，ArrayElement类从Element类继承了width和height
这两个方法。例如，假定有一个ArrayElement ae，可以用ae.width来查询其宽度，就像width是定义在ArrayElement类一样。    
　　*子类型*的意思是子类的值可以被用在任何需要超类的值的场合。例如：    
```scala
val e: Element = new ArrayElement(Array("hello"))
```    
　　变量e的类型是Element，因此用于初始化的值也应该是一个Element。事实上，初始值的类型是ArrayElement。这是可以的，因为ArrayElement类扩
展自Element，这样，ArrayElement类型是与Element类型兼容的。    
　　ArrayElement和Array[String]之间存在着*组合（composition）* 关系。这个关系被称作组合，是因为ArrayElement是通过使用Array[String]
组合出来的，Scala编译器会在为ArrayElement生成二进制类文件中放入一个指向传入的conts数组的的字段。    

***    
## Overriding-Methods-And-Fields   
　　统一访问原则只是Scala比Java在处理字段和方法上更加统一的一个方面。另一个区别是Scala中字段和方法属于同一个命名空间。这使得用字段重写无参
方法变为可能。但是，Scala禁止在同一个类中使用相同名称命名的字段和方法（Java可以）。    
　　一般来说，Scala只有两个命名空间用于定义，不同于Java的四个。Java的四个命名空间分别是：字段、方法、类型和包，而Scala的两个命名空间分别
是：*值（字段、方法、包和单例对象）、类型（类和特质名）*。Scala将字段和方法放在同一个命名空间的原因是为了让你可以用val来重写一个无参方法。    

***    
## Defining-Parametric-Fields    
　　现在ArrayElement类有一个conts参数，这个参数存在的唯一目的就是被拷贝到contents字段上。参数的名称选用conts也是为了让它看上去跟字段名
contents相似但又不至于跟它冲突。这个是“代码的坏味道（code smell）”，是代码可能存在不必要的冗余和重复的一种信号。    
　　可以通过将参数和字段合并成*参数化字段（parametric field）* 定义的方式来避免这个问题：    
```scala
class ArrayElement(
  val contents: Array[String]
) extends Element
```    
　　注意，现在contents是val的。这是同时定义参数和同名字段的简写方式。具体来说，ArrayElement类现在具备一个（不能被重新赋值的）contents字
段，该字段可以被外界访问到。该字段被初始化为参数的值。就好像类定义是如下样子，其中x123是这个参数的任意起的新名：    
```scala
class ArrayElement(x123: Array[String]) extends Element {
  val contents: Array[String] = x123
}
```    
　　也可以在类参数的前面加上var，这样的话对应的字段就可以被重新赋值。最后，还可以给这些参数化字段添加修饰符，比如private、protected或者
overried，就像鞥能够对其他类成员做的那样。例如下面这些类的定义：    
```scala
class Cat {
  val dangerous = false
}
class Tiger(
  override val dangerous: Boolean,
  private var age: Int
) extends Cat
```    
　　Tiger的定义是如下这个包含重写成员dangerous和私有成员age的类定义的简写方式：    
```scala
class Tiger(param1: Boolean, param2: Int) extends Cat {
  override val dangerous = param1
  private var age = param2
}
```    
　　这两个成员都通过对应的参数初始化。选择param1和param2这两个名字是非常随意的，重要的是它们并不跟当前作用域的其他名称相冲突。    

***    
## Invoking-Superclass-Constructors    
　　目前为止已经拥有一个由两个类组成的完整系统：一个抽象类Element，这个类又被另一个具体类ArrayElement扩展。当然可以有其他方式来表达一个元
素。比如，使用方可能要创建一个由字符串给出的单行组成的布局元素。面向对象的编程让我们很容易用新的数据变种来扩展一个已有的系统，只需要添加子类
即可。举例来说，示例给出了一个扩展自ArrayElement的LineElement类：    
```scala
class LineElement(s: String) extends ArrayElement(Array(s)) {
  override def width: Int = s.length
  override def height: Int = 1 
}
```    
　　由于LineElement扩展自ArrayElement，而ArrayElement的构造方法接收一个参数（Array[String]），LineElement需要向其超类的主构造方法
传入这样一个入参。要调用超类的构造方法，只需要将打算传入的入参放在超类名称后的圆括号里即可。    

***    
## Using-Override-Modifiers    
　　LineElement的width和height的定义前面都带上了*override*修饰符。Scala要求在所有重写了父类具体成员的成员前加上这个修饰符。而如果某个
成员并不重写或继承基类中的某个成员，这个修饰符则是被禁用的。由于LineElement的height和width的确是重写了Element类中的具体定义，override
这个修饰符是必需的。这样的规则为编译器提供了有用的信息，帮助避免某些难以捕获的错误，让系统得以更加安全地进化。    
　　这个override的规约对于系统进化来说更为重要。例如，打算定义一个2D绘图方法的类库，公开了这个类库，并且有很多人使用。在这个类库的下一个版
本，打算给基类Shape添加一个新的方法，签名如下：    
```scala
def hidden(): Boolean
```    
　　这个新的方法将被多个绘图方法用来判定某个形状是否需要被绘制出来。这有可能带来巨大的性能提升，不过没办法在不产生破坏使用方代码的风险的情况
下添加这个方法。毕竟，类库的使用者可能定义了带有不同hidden实现的Shape子类。也许使用方的方法实际上会让接收调用的对象消失而不是测试该对象是否
是隐藏的。这些“不小心出现的重写”是所谓“*脆弱基类（fragile base class）*”问题最常见的表现形式。这个问题之所以存在，原因是如果如果在某个类
继承关系中对基类（超类）添加新的成员，将面临破坏使用方代码的风险。Scala并不能完全解决脆弱基类的问题，但它相比Java对此情况有所改善（相比于
@Override，Scala中override是必须的）。如果这个绘图类库和使用方代码是用Scala写的，那么使用方代码中原先hidden实现并不会带上override修饰
符。

***    
## Polymorphism-And-Dynamic-Binding    
　　已知：类型为Element的变量可以指向一个类型为ArrayElement的对象。这个现象的名称叫作*多态（polymorphism）*，意思是“多个形状”或“多种形
式”。在我们的这个例子中，Element对象可以有许多不同的展现形式（这一类多态被称为子类型多态，Scala还有一种多态，全类型多态或叫做参数多态）。     
　　目前为止，Element有两种形式：ArrayElement和LineElement。可以通过定义新的Element子类来创建更多形式的Element。例如，可以定义一个新
形式的Element，有一个指定宽度和高度，并用制定的字符填充：    
```scala
class UniformElement(
  ch: Char,
  override width: Int,
  override height: Int
) extends Element {
  private val line = ch.toString * width
  override def contents = Array.fill(height)(line)
}
val e: Element = new UniformElement('x',2,3)
```    
　　需要注意，对变量和表达式的方法调用是*动态绑定（dynamic bound）* 的。就是说实际被调用的方法实现是在运行时基于对象的类来决定的，而不是
变量或表达式的类型来决定的。    

***    
## Declaring-Final-Members    
　　有时，在设计类继承关系的过程中，想确保某个成员不能被子类继承。在Scala中，跟Java一样，可以通过在成员前面加上final修饰符来实现：    
```scala
class ArrayElement extends Element {
  final override def demo() = {
    println("")
  }
}
```    
　　如果想确保某个类没有子类，可以将类声明为final。    

***    
## Using-Composition-And-Inheritance    
　　组合和继承是两种用其他已有的类来定义新类的方式。如果主要追求的是代码复用，一般来说应当优先使用组合而不是继承。只有继承才会受到脆弱基类问
题的困扰，会在修改超类时不小心破坏了子类的代码。    
　　关于继承关系，在建模的时候这个关系是否满足*is-a（是一个）* 的关系。例如，ArrayElement是一个Element。另一个判定的方法是，这些类的使用
方是否会把子类的类型当作超类的类型来使用。以ArrayElement为例，确实是预期使用方会将ArrayElement作为Element来用。    
　　考虑LineElement，是否理所应当是一个ArrayElement？是否认为使用方会需要吧LineElement当作ArrayElement来用？其实，将LineElement定义
为ArrayElement的主要目的是复用ArrayElement的contents定义。因此，也许更好的做法是将LineElement定义为Element的直接子类：    
```scala
class LineElement(s: String) extends Element {
  val contents = Array(s)
  override def width = s.length
  override def height = 1
}
```    
　　在前一个版本中，LineElement有一个跟ArrayElement的继承关系，它继承了contents。现在LineElement有一个跟Array的组合关系：它包含了一个
从自己的contents字段指向一个字符串数组的引用。    

***    
