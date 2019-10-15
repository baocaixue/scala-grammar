# Scala入门（续）    
- 第7步 用类型参数化数组...................................................[1](#Parameterize-Arrays-With-Type)
- 第8步 使用列表...................................................[2](#Use-Lists)
- 第9步 使用元祖...................................................[3](#Use-Tuples)
- 第10步 使用集和映射...................................................[4](#Use-Set-And-Maps)
- 第11步 识别函数式编程风格...................................................[5](#Recognize-Functional-Style)
- 第12步 从文件读取文本行...................................................[6](#Read-Lines-From-File)    

***    
## Parameterize-Arrays-With-Type    
　　在Scala中，可以用new来实例化对象或类的实例。当使用Scala实例化对象时，可以用值和类型对其进行*参数化（parameterize）*。参数化的意思是
**在创建实例时对实例做“配置”**。可以用值来参数化一个实例，做法是在构造方法的括号中传入对象的参数。例如，如下代码将实例化一个新的java.math.
BigInteger，并用值“12345”对它进行参数化：    
　　`val bigInt = new java.math.BigInteger("12345")`    
　　也可以用类型来参数化一个实例，做法是在*方括号里*给出一个或多个类型：    
```scala
val greetStrings = new Array[String](3)
greetStrings(0) = "Hello"
greetStrings(1) = ", "
greetStrings(2) = "world!\n"

greetStrings.foreach(print)
```    
　　**注意：** 上面代码示例只做概念的展示，这并不是Scala创建并初始化数组的推荐方法    

　　Scala的数组访问方式是将下标放在圆括号里，而不是像Java那样放到中括号里。同时，这里也展示了Scala关于val的一个重要概念。当用val定义一个变量
时，变量本身不能被重新赋值，但它指向的那个对象是有可能发生改变的。本例中，不能将greetStrings重新赋值成另一个数组，不过可以改变那个Array[String]
的元素，因此数组本身是可变的（mutable）。    

```scala
for (i <- 0 to 2) println(arr(i))
```    

　　上面代码，这个for表达式展示了Scala的另一个通行的规则：*如果一个方法只接收一个参数*，在调用它的时候，可以不使用英文句点。本例中的to实际上是
一个Int参数的方法。代码0 to 2会被转换为(0).to(2)。注意这种方式仅在给出方法调用的目标对象时才有效。即调用方式是这样：“目标对象 方法 参数”。    

　　Scala从技术上讲并没有操作符重载（operator overloading），因为它实际上并没有传统意义上的操作符。类似+、-、*、/这样的字符可以被用作方法名。
因此，例如往Scala解释器中键入1+2时，实际上调用了Int对象1上名为+的方法，将2作为参数传入。也可以用传统的方式来写1+2这段代码：(1).+(2)。    

　　这里展示的另一个重要理念是为什么Scala用圆括号（而不是方括号）来访问数组。跟Java相比Scala的特例更少。数组不过是类的实例，这一点跟其他Scala
实例并没有本质区别。当用一组圆括号将一个或多个值抱起来，并将其应用（apply）到某个对象时，Scala会将这段代码转换为对这个对象的一个名为apply的
方法的调用。所以array(i)会被转换为array.apply(i)。因此，在Scala中访问一个数组的元素就是一个简单的方法调用。当然，这样的代码仅在对象的类型
实际上定义了apply方法时才能通过编译。因此，这并不是一个特例，这是一个通行规则。    

　　同理，当我们尝试通过圆括号应用一个或多个参数的变量进行赋值时，编译器会将代码转换成update方法的调用，这个update方法接收两个参数：圆括号
括起来的值，以及等号右边的对象。例如：    
　　`array(0) = "Hello"`    
　　会被转换为：    
　　`array.update(0, "Hello")`    

　　Scala将数组到表达式的一切都当做带有方法的对象来处理，由此来实现概念上的简单化。不需要记住各种特例，比如Java中的基本类型与它们对应的包装
类型的区别，或数组和常规对象的区别等。不仅如此，这种统一并不带来显著的性能开销。Scala在编译代码时，会尽可能使用Java数组、基本类型和原生的算数
指令。    

　　Scala还提供了一种比通常做法更精简的方式来创建和初始化数组：    
```scala
val numNames = Array("zero", "one", "two")
```     
　　这实际上是调用了一个名为apply的工厂方法，这个方法创建并返回了新的数组。这个apply方法接收一个变长的参数列表，该方法定义在Array的*伴生对象
（companion object）* 中。可以想象成是调用了Array类的一个名为apply的静态方法。    

***

## Use-Lists    
　　函数式编程的重要理念之一就是方法不能有副作用。一个方法唯一要做的是计算并返回一个值。这样做的好处是方法不再互相纠缠在一起，因此变得更可靠、
更易复用。另一个好处（作为静态类型的编程语言）是类型检查器会检查方法的入参和出参，因此逻辑错误通常都是以类型错误的形式出现。将这个函数式的哲学
应用到对象的世界意味着让对象不可变。    
　　Scala数组是 一个拥有相同类型的对象的可变序列。如一个Array[String]只能包含字符串，虽然无法在数组实例化后改变其长度，却可以改变它的元素
值。因此数组是可变的对象。    

　　对于需要拥有相同类型的对象的不可变序列的场景，可以使用Scala的List类。Scala的List（scala.List）跟Java的java.util.List的不同在于Scala
的List是不可变的，而Java的List是可变的。更笼统地说，Scala的List被设计为允许函数式风格的编程。创建列表的方法如下：    
　　`val oneTwoThree = List(1,2,3)`
　　代码中建立一个新的名为oneTwoThree的val，并将其初始化成一个拥有整型元素1、2、3的List[Int]。由于List是不可变的，它们的行为有点类似于Java
的字符串：调用列表的某个方法，而这个方法的名字看上去像是会改变列表的时候，它实际上是创建并返回一个带有新值的新列表。如，List有个方法叫“:::”，
用于拼接列表。    
　　也许列表上用的最多的操作是“::”，读作“cons”。它在一个已有列表的最前面添加一个新的元素，并返回这个新的列表，如：    
```scala
val twoThree = List(2,3)
val oneTwoThree = 1 :: twoThree
println(oneTwoThree)
```    
　　**注意：**    
```text
　　在表达式“1 :: twoThree”中，::是右操作元（right operand，即twoThree这个列表）的方法。::方法的结合性（associativity）背后的规则是
这样的：如果一个方法被用在操作符表示法（operator notation）当中，比如 a * b，方法调用默认都发生在左操作元（left operand），除非方法名
以冒号（:）结尾。如果方法名的最后一个字符是冒号，该方法的调用会发生在它的右操作元。因此，在 1 :: twoThree中，:: 方法调用发生在twoThree上，
传入参数是1，即 twoThree.::(1)。
```    

　　表示空列表的快捷方式是Nil，初始化一个新的列表的另一种方式是用 :: 将元素串接起来，并将Nil作为最后一个元素（因为::方法定义在List上，若写
成1:2:3编译不通过）。如：    

　　`val oneTwoThree = 1 :: 2 :: 3 :: Nil`    

　　*为什么不在列表的末尾追加元素？*    
```text
　　List类的确提供了“追加”（append）操作，写作:+，但是这个操作很少被使用，因为往列表（末尾）追加元素的操作所需要的时间随着列表的大小线性
增加，而使用::在列表的前面添加元素只需要常量时间。如果想通过追加元素的方式高效地构建列表，可以依次在头部添加完成后，调用reverse。也可以用
ListBuffer，这是个可变的列表，支持追加操作，完成后调用toList即可。
```    

　　List的一些方法和用途:        

| 方法 | 用途  
| --- | ---      
| List()或Nil | 表示空列表    
| List("Cool", "tools") | 创建一个新的List[String]，并初始化值    
| val thrill = "Will" :: "fill" :: "until" :: Nil | 创建一个新的List[String]，包含3个值    
| List("a","b") ::: List("c","d") | 将两个列表拼接起来，返回一个新列表    
| thrill(2) | 返回列表thrill中下标为2的元素    
| thrill.count(s => s.length == 4) | 对thrill中长度为4的字符串进行计数    
| thrill.drop(2) | 返回去掉thrill的头两个元素的列表    
| thrill.dropRight(2) | 返回去掉thrill后两个元素的列表    
| thrill.exists(s => s == "until") | 判断thrill中是否有字符串元素值为“until”    
| thrill.filter(s => s.length == 4) | 按顺序返回列表中所有长度为4的元素列表    
| thrill.forall(s => s.endsWith("l")) | 表示列表中是否所有元素都以“l”结尾    
| thrill.foreach(s => println(s)) | foreach    
| thrill.head | thrill(0)，列表首个元素    
| thrill.init | 返回列表除最后一个元素之外所有元素组成的列表    
| thrill.isEmpty | 判断是否为空列表    
| thrill.last | 返回列表最后一个元素    
| thrill.length | 返回列表元素个数    
| thrill.map(s => s + "y") | 返回一个对列表thrill所有字符串元素末尾添加“y”的新字符串列表    
| thrill.mkString(", ") | 用列表thrill的所有元素组成字符串    
| thrill.filterNot(s => s.length == 4) | 按顺序返回列表中所有长度不为4的元素列表    
| thrill.reverse | 顺序反转    
| thrill.sortWith((s,t) => s.charAt(0).toLower < t.charAt(0).toLower) | 返回包含列表thrill的所有元素，按照首字母小写的字母顺序排序的列表    
| thrill.tail | 返回列表除首个元素外的所有元素    

***   

## Use-Tuples    
　　另一个有用的容器对象是元组（tuple）。跟list类似，元组也是不可变的。不过跟list不同的是，**元组可以容纳不同类型的元素**。列表可以是List[Int]
或List[String]，而元组可以同时包含整数和字符串。当需要从方法返回多个对象时，元组非常有用。在Java中遇到类似的情况通常会创建一个类似JavaBean
那样的类来承载多个返回值，而用Scala可以简单地返回一个元组。要实例化一个元组，只需将对象放在括号中，用逗号隔开即可。一旦实例化好一个元组，可以用
英文句点、下划线和*从1开始*的序号来访问每个元素。如下：    
```scala
val pair = (99, "Scala")
println(pair._1)
println(pair._2)
```    

　　**访问元组中的元素**    
　　元组不能像列表那样访问元素（也就是“pair(0)”），是因为列表的apply方法永远只能返回同一种类型，但元组里的元素可以是不同类型的：_1可能是一
种类型，_2可能是另一种类型，等等。这些_N表示的字段名是从1开始而不是从0开始，这是由其他同样支持静态类型元组的语言设定的传统，如Haskelll和ML。    

***    
## Use-Set-And-Maps    
　　Scala给开发者同时享有函数式和指令式编程风格，其集合类库特意对可变和不可变的集合进行了区分。举例来说，数组永远是可变的，列表永远是不可变的。
Scala同时还提供了集（set）和映射（map）的可变和不可变的不同选择，但使用同样的简单名字。对于集和映射而言，Scala通过不同的类继承关系来区分可变
和不可变版本。    
　　例如，Scala的API包含了一个基础的特质（trait）来表示集，这里的特质跟Java的接口定义类似。在此基础上，Scala提供了两个子特质（subtrait），
一个用于表示可变集，另一个用于表示不可变集。    
　　这三个特质都叫做Set（位于不同的包中）。Scala API中具体用于表示集的类，如HashSet，分别扩展自可变或不可变的Set。（在Java中“实现”某个接口，
而在Scala中“扩展”或“混入”特质）。创建集的默认方式如下：    
```scala
var jetSet = Set("Boeing", "AAirbus")
jetSet += "Lear"
println(jetSet.contains("Cessna"))
```    
　　这段代码默认创建的Set是不可变的，Scala编译器推断出jetSet的类型为不可变的Set[String]。要向集里添加新的元素，可以对集调用+方法，出入这个
新元素。不论是可变的还是不可变的集，+方法都会创建并返回一个新的包含了新元素的集。而可变变集提供了一个实际的+=方法，不可变集并不直接提供这个方法
（实为jetSet = jetSet + "Lear"）。因此，这里实际上是将jetSet这个var重新赋值成一个新集。    

　　如果想要一个可变集，需要做一次引入（import），如下：    
```scala
import scala.collection.mutable.Set

val movieSet = Set("Hitch", "Poltergeiset")
movieSet += "Shrek"
println(movieSet)
```    

　　尽管由可变和不可变Set的工程方法生产出来的默认集的实现对于大多数情况来说都够用了，偶尔可能也需要一类特定的集。需要做的是简单地引入需要的类，
然后使用其伴生对象上的工厂方法。例如，需要一个不可变的HashSet：     
```scala
import scala.collection.immutable.HashSet

val hashSet = HashSet("Tomatoes", "Chilies")
println(hashSet + "Coriander")
```    

　　Scala另一个有用的集合类是Map。跟集类似，Scala也提供了Map的可变和不可变版本，用类继承关系来区分。下面是创建并初始化一个可变的映射：    
```scala
import scala.collection.mutable

val treasureMap = mutable.Map[Int, String]()
treasureMap += (1 -> "Go to island.")
treasureMap.+=(2 -> "Find big X on ground.")
treasureMap += (3 -> "Dig.")
println(treasureMap(2))
```    
　　正如之前所述，Scala编译器会将二元的操作，如3->"Dig."，转换成标准的方法调用，即(3).->("Dig.")。**可以在Scala的任何对象上调用这个->方
法，它将返回包含键和值两个元素的元组**。    

　　如果是不可变的映射，则不需要任何引入：    
```scala
val romanNumeral = Map(
  1 -> "I", 2 -> "II", 3 -> "III", 4 -> "IV", 5 -> "V"
)
println(romanNumeral)
```    

***    



