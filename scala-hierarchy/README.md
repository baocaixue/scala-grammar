# Scala的继承关系
- Scala的类继承关系...................................................[1](#Scala's-Class-Hierarchy)
- 基本类型的实现机制...................................................[2](#Primitives-Implemented)
- 底类型（bottom types）...................................................[3](#Bottom-Types)
- 定义自己的值类型 ...................................................[4](#Defining-You-Own-Value-Classes)    
    
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
![image](https://raw.githubusercontent.com/baocaixue/scala-grammar/master/scala-hierarchy/src/main/resources/ScalasHierarchy.png)    
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
