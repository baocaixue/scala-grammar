# Scala的继承关系
- Scala的类继承关系...................................................[1](#Scala's-Class-Hierarchy)
- 基本类型的实现机制...................................................[2](#Primitives-Implemented)
- 底类型（bottom types）...................................................[3](#Bottom-Types)
- 定义自己的值类型 ...................................................[4](#defining-you-own-value-classes)    
    
　　在Scala中，每个类都继承自同一个名为*Any*的超类。由于每个类都是Any的子类，在Any中定义的方法是“全类型的”（universal）：它可以在
任何对象上被调用。Scala还在继承关系的底部定义了一些有趣的类，*Null*和*Nothing*，它们本质上是作为通用的子类存在的。例如，就像Any是
每一个其他类的超类那样，Nothing是每一个其他类的子类。

***    
## Scala's-Class-Hierarchy
　　下图展示了Scala类继承关系的轮廓。在继承关系的顶部是Any类，定义了如下的方法：    
```scala
final def ==(that: Any): Boolean
final def !=(that: Any): Boolean
def equals(that: Any): Boolean
def ##: Int
def hashCode: Int
def toString: Int
```    
![image](https://github.com/baocaixue/scala-grammar/blob/master/scala-hierarchy/src/main/scala/com/isaac/ch11/ScalasHierarchy.png?raw=true)    
　　由于每个类都继承自Any，Scala程序中的每个对象都可以用`==`、`!=`、或`equals`来进行比较，用`##`或`hashCode`做哈希，以及用`toString`
做格式化。相等和不等方法（`==`和`!=`）在Any类中声明为final，所以它们不能被子类重写。    
　　`==`方法从本质上讲等同于`equals`，而`!=`一定是`equals`的反义（唯一一个`==`不直接调用`equals`的场景是Java的数值类，比如Integer或
Long，在Scala中`new Integer(1)`和`new Long(1)`是`==`的，并且`##`的哈希值也是相同的）。这样一来，子类可以通过重写equals方法来定制`==`
或`!=`的含义。    
　　根类Any有两个子类：*AnyVal*和*AnyRef*。AnyVal是Scala中的所有*值类（value class）* 的父类。虽然可以定义自己的值类，Scala提供了九
个内建的值类：Byte、Short、Char、Int、Long、Float、Double、Boolean和Unit。前八个对应Java中的基本类型，它们的值在运行时是用Java的基本
类型的值来表示的。这些类的实例在Scala中统统写作字面量。例如，42是Int的实例，'x'是Char的实例，而false是Boolean的实例。*不能用new来创建
这些类的实例*。这一点是通过将值定义为抽象的同时是final的这个“小技巧”来完成的。另外的那个值类Unit粗略地对应到Java的void类型;它用来作为那些
不返回结果的方法的结果类型。*Unit有且只有一个实例值，写作()*。
　　值类以方法的形式支持通常的算数和布尔操作符。例如，Int拥有名为+和*的方法，而Boolean拥有名为||和&&的方法。值类同样继承了Any类的所有方法：    
```shell script
scala> 42.toString
res1: String = 42
scala> 42.hashCode
res2: Int = 42
scala> 42 equals 42
res3: Boolean = true
```    
　　注意，值类空间是是扁平的，所有的值类都是`scala.AnyVal`的子类，但它们互相之间没有子类关系。不同的值类类型之间存在隐式转换。例如，在需要
时，scala.Int类的一个实例可以（通过隐式转换）被自动放宽成scala.Long的实例。隐式转换还被用于给值类型添加更多的功能。例如，Int类型支持所有
下列操作：    
```shell script
scala> 42 max 43
res4: Int = 43
scala> 42 min 43
res5: Int = 42
scala> 1 until 5
res6: scala.collection.immutable.Range = Range(1,2,3,4)
scala> 1 to 5
res7: scala.collection.immutable.Range.Inclusive = Range(1,2,3,4,5)
scala> (-3).abs
res8: Int = 3
```    
　　工作原理是这样的：方法min、max、until、to和abs都定义在scala.runtime.RichInt类中，并且存在从Int类到RichInt类的隐式转换。只要对Int
调用的方法没有在Int类中定义，而RichInt类定义了这样的方法，隐式转换就会被自动应用。其他值类也有类似的“助推类”和隐式转换。    
　　根类Any的另一个子类是*AnyRef*类。这是Scala所有引用类的基类。在Java平台上AnyRef事实上只是java.lang.Object的一个别名。因此Java编写
的类和Scala编写的类都继承自AnyRef。因此，可以这样看待java.lang.Object：它是AnyRef在Java平台的实现。虽然可以在面向Java平台的Scala程序
中任意换用Object和AnyRef，推荐的风格是尽量都使用AnyRef。    

***    
## Primitives-Implemented    
　　事实上，Scala存放整数的方式跟Java一样，都是32位的词（word）。这对于JVM上的效率以及跟Java类库的互操作都很重要。标准操作比如加法和乘法
被实现为基本操作。不过，Scala在任何需要将整数当作（Java）对象时，都会启用“备选”的java.lang.Integer类。例如，对整数调用toString或将整数
赋值给一个类型为Any的变量时，都会发生这种情况。类型为Int的整数在必要时都会透明地被转换成类型为java.lang.Integer的“装箱整数”。    
　　所有这些听上去都很像Java 5的自动装箱（auto-boxing）机制，也的确非常相似。不过有一个很重要的区别：Scala中的装箱跟Java相比要透明得多。
参考下面的Java代码：    
```java
boolean isEqual(int x, int y) {
    return x == y;
}
System.out.println(isEquals(421, 421))
```    
　　这里会得到true。现在，将isEqual的参数类型改为java.lang.Integer（或者Object也可以）：    
```java
boolean isEqual(Integer x, Integer y) {
    return x == y;
}
System.out.println(isEqual(421, 421))
```    
　　会发现得到的是false！这里的数字421被装箱了两次，因此x和y这两个参数实际上是两个不同的对象。由于==对于引用类型而言意味着引用相等性。而
Integer是一个引用类型，结果就是false。这一点也显示出Java并不是一个纯面向对象的语言。基本类型和引用类型之间有一个清晰可被观察到的区别。    
　　现在，用Scala来做相同的实验：    
```scala
def isEqual1(x: Int, y: Int) = x == y
def isEqual2(x: Any, y: Any) = x == y
isEqual1(421, 421)//true
isEqual2(421, 421)//true
```    
　　Scala的相等性操作==被设计为对于类型的实际呈现是透明的。对于值类型而言，它表示的是自然（数值或布尔值）相等性。而对于Java装箱数值类型之外
的引用类型，==被处理成Object继承的equals方法的别名。这个方法原本定义用于引用相等性，但很多子类都重写了这个方法来实现它们对于相等性更自然的
理解和表示。这也意味着在Scala中不会陷入Java那个跟字符串对比相关的陷阱。Scala的字符串对比是它应该有的样子：    
```scala
val x = "abcd".substring(2)//"cd"
val y = "abcd".substring(2)//"cd"
x == y//true
```    
　　在Java中，对x和y的对比结果会返回false。这里因该用equals，但是Scala就不必如此。    
　　不过，在有些场景下需要引用相等性而不是用户定义的相等性。例如，有些场景对于效率的要求超高，可能会对某些类使用hash cons并引用相等性来对比
其实例（hash cons的意思是将创建的实例缓存在一个弱引用的集合中。然后，当想获取该类的新实例时，首先检查这个缓存，如果缓存已经有一个元素跟要创
建的相等，就可以复用这个已存在的实例。这样以来，任何两个以equals()相等的实例从引用相等性的角度也是相等的）。对于这些情况，AnyRef定义了一个
额外的**eq方法**，该方法不能被重写，实现为引用相等性（行为跟Java中==对于引用类型的行为是一致的）。还有一个eq的反义方法*ne*。    

***   
## Bottom-Types    
　　在上面的类继承关系图的底部，有两个类：scala.Null和scala.Nothing。它们是Scala面向对象的类型系统统一处理某些“极端情况（corner case）”
的特殊类型。    
　　Null类是null引用的类型，它是每个引用类（继承自AnyRef的类）的子类。Null并不兼容于值类型，比如不能将null赋值给一个整数变量。    
　　Nothing位于Scala类继承关系的底部，它是每个其他类型的子类型。不过，并不存在这个类型的任何值。为什么要这么一个没有值的类型呢？Nothing
的用途之一是给出非正常终止的信号。    
　　举例来说，Scala标准类库Predef对象有一个error方法，其定义如下：    
```scala
def error(message: String): Nothing = throw new RuntimeException(message)
```    
　　error的返回类型是Nothing，这告诉使用方该方法并不会正常返回（它会抛出异常）。由于Nothing是每个其他类型的子类型，可以以非常灵活的方式来
使用error这样的方法。例如：    
```scala
def divide(x: Int, y: Int): Int = 
    if (y != 0) x / y
    else error("can't divide by zero")
```    
　　这里`x / y`条件判断的“then”分支的类型为Int，而else分支类型为Nothing。由于Nothing是Int的子类型，整个条件判断表达式的类型就是Int，正
如方法声明要求的那样。    
　　
***    
## defining-you-own-value-classes    
　　现在可以定义自己的值类来对内建的值类进行扩充。跟内建的值类一样，自定义的值类的实例通常也会编译成那种不使用包装类的Java字节码。在需要包装
类的上下文里，比如泛型代码，值将被自动装箱和拆箱。    
　　只有特定的几个类可以成为值类。要使得某个类成为值类，它必须有且仅有一个参数，并且内部除了def之外不能有任何其他东西。不仅如此，也不能有其
他类扩展自值类，且值类不能重新定义equals和hashCode。    
　　要定义值类，需要将它处理成AnyVal的子类，并在它唯一的参数前加上val。以下是值类的一个例子：    
```scala
class Dollars(val amount: Int) extends AnyVal {
  override def toString: String = "$" + amount 
}
```    
　　参数前的val让amount参数可以作为字段被外界访问。例如，如下代码将创建这个值类的一个实例，然后从中获取金额：    
```scala
val money = new Dollars(100000)//Dollars = $100000
money.amount//int = 100000
```    
　　本例中，money指向该值类的一个实例。它在Scala的源码中的类型为Dollars，但在便以后的Java字节码中将直接使用Int。    
　　这个例子定义了toString方法，编译器将识别出什么时候使用这个方法。这就是为什么打印money将给出$100000，带上了美元符，而打印money.amount
仅会给出100000。甚至可以定义多个同样以Int值支撑的值类型。例如：    
```scala
class SwissFrances(val amount: Int) extends AnyVal {
  override def toString: String = amount + "CHF" 
}
```    
　　尽管Dollars和SwissFrancs最终都是以整数呈现的，在相同的作用域内同时使用它们并没有什么问题。    

### 避免类型单一化    
　　要想尽可能发挥Scala类继承关系的好处，请试着对每个领域概念定义一个新的类，哪怕服用相同的类作不同的用途也是可行的。即便这样的一个类是所谓
的*细微类型（tiny type）* ，即没有方法也没有字段，定义这样的一个额外的类有助于编译器在更多的地方帮助到你。    
　　例如，假定编写代码生成HTML。在HTML中，风格名是用字符串表示的。锚定标识符也是如此。HTML自身也是字符串，所以可以用字符串定义的助手方法来
表示所有的这些内容：    
```scala
def title(text: String, anchor: String, style: String): String = 
    s"<a id='$anchor'<h1 class='$style'>$text</h1></a>"
```    
　　这个类型签名出现了四个字符串！这类*字符串类型（stringly typed）* 的代码从技术上讲是强类型的，但由于能看到的都是字符串类型的，编译器
并不能帮助检测用错参数的情况。例如：    
```scala
title("chap:vcls", "bold", "Value Classes")//String = <a id='bold'><h1 class='Value Classes'>cha.vcls</h1></a>
```    
　　这段HTML代码完全坏掉了。本意是用来显示的文本“Value Classes”被用成了风格类，而显示出来的文本是“chap.vcls”，这本应该是锚定点的。最后
实际的锚定标识为“bold”，这其实本应是风格类的。这些错误，编译器都不会提示出来。    
　　如果对每个领域概念都定义一个细微类型，编译器就能对我们更有帮助。比如，可以分别对风格、锚定标识、显示文本和HTML都定义一个小类。由于这些
类只有一个参数，没有其他成员，它们可以被定义成值类：    
```scala
class Anchor(val value: String) extends AnyRef
class Style(val value: String) extends AnyRef
class Text(val value: String) extends AnyRef
class Html(val value: String) extends AnyRef

def title(text: Text, anchor: Anchor, style: Style): Html = 
    new Html(
      s"<a id='${anchor.value}'>" + 
          s"<h1 class='${style.value}'>" +
          text.value +
          "</h1></a>"
    )
```    

