# 包和引入
- 将代码放进包里...................................................[1](#Putting-Code-In-Packages)
- 对相关代码的精简访问...................................................[2](#Concise-Access-To-Related-Code)
- 引入...................................................[3](#Imports)
- 隐式引入...................................................[4](#Implicit-Imports)
- 访问修饰符...................................................[5](#Access-Modifiers)
- 包对象...................................................[6](#Package-Objects)     
    
 　　在处理程序，尤其是大型程序时，减少*耦合（coupling）* 是很重要的。所谓耦合，指的是程序不同部分依赖其他部分的程度。低耦合能减少程序某个
 局部的某个看似无害的改动对其他部分造成严重后果的风险。减少耦合的一种方式是以模块化的风格编写代码。可以将程序切分成若干较小的模块，每个模块
 都有所谓的内部和外部之分。当在模块内部（即实现部分）工作时，只需要跟同样在这个模块工作的程序员协同。只有当你必须修改模块的外部（即接口部分）
 时，才有必要跟其他模块工作的开发者协同。    

***    
## Putting-Code-In-Packages
　　Scala代码存在于Java平台全局的包层次结构当中。在Scala中，可以通过两种方式将代码放进带名字的包里。第一种方式是在文件的顶部放置一个package
子句，让整个文件的内容放进指定的包：    
```scala
package com.isaac
class Navigator
```    
　　**注意**    
　　由于Scala代码是Java生态的一部分，对于你打算发布出来的Scala包，建议遵循Java将域名倒过来作为包名的习惯。    
　　另一种将Scala代码放进包的方式更像是C#的命名空间。可以在package子句之后加上一段用花括号包起来的代码块，这个代码块包含了进入该包的定义。
这个语法成为*打包（packaging）*。效果与上述一样：    
```scala
package com.isaac {
  class Navigator
}
```    
　　对于这样一个简单的例子而言，完全可以用第一种方式那样的写法。不过，这个更通用的表示法可以让我们在一个文件里包含多个包的内容。举例来说，可
以把某个类的测试代码跟原始代码放在同一个文件里，不过分成不同的包：    
```scala
package com {
  package isaac {
    //位于com.isaac包中
    class Navigator

    package test {
      //位于com.isaac.test包中
      class NavigatorSuite
    }
  }
}
```    

***    
## Concise-Access-To-Related-Code    
　　将代码按照包层次结构划分以后，不仅有助于浏览代码，同时也是在告诉编译器，同一个包中的代码之间存在某种相关性。在访问同一个包的代码时，Scala
允许使用简短的，不带限定前缀的名称。    
```scala
package com {
  package isaac {
    class Navigator {
      //不需要说com.isaac.StartMap
      val map = new StarMap
    }
    class StarMap
  }
  class Ship {
    //不需要说com.isaac.Navigator
    val nav = new isaac.Navigator
  }
  package org {
    class Fleet {
      //不需要说com.Ship
      def addShip() = new Ship
    }
  }
}
```    
　　这里给出了三个例子。首先，一个类不需要前缀就可以在自己的包内被别人访问。这就是为什么new StarMap能够通过编译;其次，包自身可以从包含它的
包里不带前缀地访问到。注意Navigator类是如何实例化的，new表达式出现在com包中，这个包包含了com.isaac包，因此可以简单地用isaac访问com.isaac
包的内容;再次，用花括号打包语法，所有在包外的作用域内可以被访问的名称，在包内也可以访问到，参见Ship的实例化。    
　　注意这类访问只有当你显式地嵌套打包时才有效。如果坚持每个文件只有一个包的做法，那么（就跟Java一样）只有那些在当前包内定义的名称才可以直接
使用。如果花括号嵌套包让代码过于往右缩进，可以用用多个package子句但不使用花括号：    
```scala
package com
package isaac
class Fleet
```    
　　最后一个小技巧也很重要。有时，会遇到需要在非常拥挤的作用域内编写代码，包名互相遮挡。如下列代码所示，MissionControl类的作用域内包含了三
个独立的名为launch的包！如何来分别引用Booster1、Booster2、Booster3呢？    
```scala
package launch {
  class Booster3
}
package isaac {
  package navigation {
    package launch {
      class Booster1
    }
    class MissionControl {
      val booster1 = new launch.Booster1
      val booster2 = new isaac.launch.Booster2
      val booster3 = new _root_.launch.Booster3
    }
  }
  package launch {
    class Booster2
  }
}
```    
　　访问地一个很容易。直接引用launch会指向isaac.navigation.launch包，因为这是最近的作用域定义的launch包。因此可以简单地用launch.Booster1
来引用第一个类。访问第二个也不难，可以用isaac.launch.Booster2，这样就可以清晰地表达想要的是哪一个包。那么问题就剩下第三个：考虑到嵌套的
launch包遮挡了位于顶层的那一个，那如何访问Booster3呢？    
　　为了解决这个问题，Scala提供了一个名为_root_的包，这个包不会跟任何用户编写的包冲突。换句话说，每个你能编写的顶层包都被当作是_root_包的
成员。    

***    
## Imports    
　　在Scala中，可以用import子句引入包和它们的成员。被引用的项目可以用例如File这样的简单名称访问，而不需要限定名称，比如java.io.File：    
```scala
abstract class Fruit(val name: String, val color: String)
object Fruits {
  object Apple extends Fruit("apple", "red")
  object Orange extends Fruit("orange", "orange")
  object Pear extends Fruit("pear", "yellowish")
  val menu = List(Apple, Orange, Pear)
}
```    
　　import子句使得某个包或对象的成员可以只用它们的名字访问，而不需要在前面加上包名或对象名。下面是一些简单的例子：    
```scala
//到Fruit的便捷访问
import com.isaac.ch13.importdemo.Fruit

//到com包下所有成员的便捷访问
import com._

//到Fruits所有成员的便捷访问
import com.isaac.ch13.importdemo.Fruits._
```    
　　第一个对应Java的单类型引入，而第二个对应Java的*按需（on-demand）* 引入。唯一的区别是Scala的按需引入跟在后面的是下划线（\_）而不是星
号（\*）（毕竟\*号是个合法的标识符）。上述第三个引入子句对应Java对静态字段的引入。    
　　Scala的引入实际上更加通用。首先，Scala的引入可以出现在任何地方，不仅仅是在某个编译单元的最开始，它们还可以引用任意值。比如：    
```scala
import com.isaac.ch13.importdemo.Fruit
def showFruit(fruit: Fruit) = {
  import fruit._
  println(name + "s are " + color)
}
```    
　　showFruit方法引入了其参数fruit（类型为Fruit）的所有成员。这样接下来的println语句就可以直接引用name和color。这两个引用等同于fruit.
name和fruit.color。这种语法在需要用对象来表示模块时尤其有用。    
    
　　**Scala的灵活引入**    
　　跟Java相比，Scala的import子句要灵活的多，主要区别有三点。在Scala中，引入可以：    
* 出现在任意位置
* 引用对象（不论是单例还是常规对象），而不只是包
* 可以重命名或隐藏某些被引入的成员    
    
　　还有一点可以说明Scala的引入更灵活：它们可以引入包本身，而不仅仅是包中的非包成员。如果包嵌套的包想象成包含在上层包内，这样的处理就很自然。
例如，在下面示例中，被引入的包是java.util.regex，这使得可以在代码中使用regex这个简单的名字。要访问java.util.regex包里的Pattern单例对象，
可以直接用regex.Pattern：    
```scala
import java.util.regex

class AStartB {
  //访问java.util.regex.Pattern
  val pat = regex.Pattern.compile("a*b")
}
```    
　　Scala中的引入还可以重命名或隐藏指定的成员。做法是包在花括号内的*引入选择器子句（import selector clause）* 中，这个子句跟在那个要引入
成员的对象后面。以下是一些例子：    
```scala
import com.isaac.ch13.importdemo.Fruits.{Apple, Orange}
```    
　　这只会从Fruits对象引入Orange和Apple两个成员。    
```scala
import com.isaac.ch13.importdemo.Fruits.{Apple => McIntosh, Orange}
```    
　　这会从Fruits对象引入Apple和Orange两个成员。不过Apple对象被重命名为McIntosh，因此代码中要么用`Fruits.Apple`要么用`McIntosh`来访问
这个对象。重命名的子句的形式永远都是**原名 => 新名**。    
```scala
import java.sql.{Date => SDate}
```    
　　这会以SDate为名引入SQL日期类，这样就可以同时以Date这个名字引入Java的普通日期对象。    
```scala
import java.{sql => S}
```    
　　这会以S为名引入java.sql包，这样就可以编写类似S.Date这样的代码。    
```scala
import com.isaac.ch13.importdemo.Fruits.{_}
```    
　　这将从Fruits对象引入所有成员，跟`import com.isaac.ch13.importdemo.Fruits._`的含义是一样的。    
```scala
import com.isaac.ch13.importdemo.Fruits.{Apple => McIntosh, _}
```    
　　这将从Fruits对象引入所有的成员，但会把Apple重命名为McIntosh。    
```scala
import com.isaac.ch13.importdemo.Fruits.{Pear => _, _}
```    
　　这会引入除Pear外Fruits的所有成员。总之，引入选择器可以包含：    
* 一个简单那的名称x。这将把x包含在引入的名称集里。
* 一个重命名子句x => y。这会让名为x的成员以y的名称可见。
* 一个隐藏子句x => \_。这会从引入名称集里排除掉x。
* 一个捕获所有的“\_”。这回引入除了之前子句中提到的成员之外的所有成员。如果要给出捕获所有子句，它必须出现在引入选择器列表的末尾    

　　开始给出的简单引入子句可以被视为带有选择器子句的特殊简写。例如，“import p.\_”等价于“import p.{\_}”，而“import p.n”等价于“import p.{n}”。    

***    
## Implicit-Imports    
　　Scala对每个程序都隐式地添加了一些引入。本质上，这就好比每个扩展名为“.scala”的源码文件的顶部都添加了如下三行引入子句：    
```scala
import java.lang._//java.lang包的全部内容
import scala._//scala包的全部内容
import Predef._//Predef对象的全部内容
```    
　　java.lang包包含了标准的Java类，它总是被隐式地引入到Scala源码文件中。由于java.lang是隐式引入的，举例来说，可以直接写Thread，而不必是
java.lang.Thread。    
　　scala包包含了Scala的标准类库，这里面有许多公用的类和对象。由于scala是隐式引入的，举例来说，可以直接写List，而不是scala.List。    
　　Predef对象包含了许多类型、方法和隐式转换的定义，这些定义在Scala程序中经常被用到。举例来说，由于Predef是隐式引入的，可以直接写assert，
而不是Predef.assert。    
　　Scala对这三个引入子句做了一些特殊处理，后引入的会遮挡前面的。举例来说，scala包和Java 1.5版本后的java.lang包都定义了StringBuilder类。
由于scala的引入遮挡了java.lang的引入，因此StringBuilder这个简单的名称会引用到scala.StringBuilder，而不是java.lang.StringBuilder。    

***    
## Access-Modifiers    
　　包、类或对象的成员可以标上private和protected这样的访问修饰符。这些修饰符将对成员的访问限定在特定的代码区域。Scala对访问修饰符的处理大
体上跟Java保持一致，不过也有些重要的区别。    

### 私有成员    
　　Scala对私有成员的处理跟Java类似。标为private的成员只在包含该定义的类或对象内部可见。在Scala中，这个规则同样适用于内部类。Scala在一致
性方面做得比Java更好，但做法不一样。    
```scala
class Outer {
  class Inner {
    private def f() = println("f")
    class InnerMost {
      f()//OK
    }
  }
  //(new Inner).f()//错误：无法访问f
}
```    
　　在Scala中，像`(new Inner).f()`这样的访问方式是非法的，因为f在Inner中声明为private并且对f的调用并不是发生在Inner类内部。而第一次在
InnerMost类中访问f是OK的，因为这个调用包含在Inner类内部。Java则对两种访问都允许，因为Java中可以从外部类访问其内部类的私有成员。    

### 收保护的成员    
　　跟Java相比，Scala对protected成员的访问也更严格。在Scala中，protected的成员只能从定义该成员的子类访问。而Java允许同一个包内的其他类
访问这个类的收保护成员。Scala提供了另一种方式来达到这个效果（限定词），因此protected不需要为此放宽限制。    
```scala
package p {
  class Super {
    protected def f() = println("f")
  }
  class Sub extends Super {
    f()
  }
  class Other {
    //(new Super).f()//错误：无法访问f
  }
}
```    

### 公共成员    
　　Scala并没有专门的修饰符用来标记公共成员：任何没有被标记为private或protected的成员都是公共的。公共成员可以在任何位置访问到。    

### 保护的范围    
```scala
package bobsrockets

package navigation {
  private[bobsrockets] class Navigator {
    protected[navigation] def useStartChart() = {}
    class LegOfJourney {
      private[Navigator] val distance = 100
    }
    private[this] var speed = 200
  }
}

package launch {
  import navigation._
  object Vehicle {
    private[launch] val guide = new Navigator
  }
}
```    
　　可以用限定词对Scala中的访问修饰符机制进行增强。形如`private[X]`或`protected[X]`的修饰符的含义是对此成员的访问限制“上至”X都是私有或
保护的，其中X表示，某个包含该定义的包、类或单例对象。    
　　带有限定词的访问修饰符让我们可以对成员的可见性做非常细粒度的控制，尤其是它允许我们表达Java中访问限制的语义，比如包内私有、包内受保护或到
最外层嵌套类范围内私有等。这些用Scala中简单的修饰符是无法直接表达出来的。这种机制还允许我们表达那些在Java中表达的访问规则。    
　　上面的示例中给出了使用多种访问限定词的用法。示例中，Navigator类被标记为`private[bobsrocket]`，其含义是这个类对bobsrocket包内所有类
和对象都可见。具体来说，Vehicle对象中对Navigator的访问是允许的，因为Vehicle位于launch包，而launch是bobsrockets的子包。另一方面，所有
bobsrocket包之外的代码都不能访问Navigator。    
　　这个机制在那些跨多个包的大工程中非常有用。可以定义对工程中某些子包但对外部不可见的实体。这在Java中是无法做到的。一旦某个定义越过了包的边
界，它就对整个世界可见的。    
　　当然，private的限定词也可以是直接包含该定义包。比如示例中Vehicle对象的guide成员变量的访问修饰符。这样的访问修饰符跟Java的包内私有访问
是等效的。    
　　LegOfJourney.distance上private修饰符的作用：    

无访问修饰符 | 公共访问    
--- |  ---     
private\[bobsrockets\] | 外围包内访问    
private\[navigation\] | 与Java中的包可见性相同    
private\[Navigator\] | 与Java中的private相同    
private\[LegOfJourney\] | 与Scala中的private相同    
private\[this\] | 仅在当前对象内访问    

　　所有的限定词也可以应用在protected上，跟private上的限定词作用一样。也就是说，如果在C类中使用protected\[X\]这个修饰符，那么C的所有子
类，以及X表示的包、类或对象中，都能访问这个被标记的定义。例如上面示例中的useStarChart方法在Navigator的所有子类，以及navigation包中的代
码都可以访问。这样一来，这里的含义就跟Java的protected是完全一样的。    
　　private的限定词也可以引用包含它的类或对象。例如，示例代码中LegOfJourney类的distance变量被标记为private\[Navigator\]，因此整个
Navigator类都可以访问。这就达到了跟Java中内部类的私有成员一样的访问能力。当C是最外层的嵌套时，private\[C\]跟Java的private就是一样的效
果。    
　　最后，Scala还提供了比private限制范围更小的访问修饰符。被标记为private\[this\]的定义，只能包含该定义的同一个对象中访问。这样的定义被
称作是*对象私有（object-private）* 的。例如，示例中Navigator类的speed定义就是对象私有的。这意味着所有对它的访问不仅必须来自Navigator
类内部，并且还必须是来自Navigator的同一实例。因此Navigator中“speed”和“this.speed”是合法的访问。    
　　而如下访问则是不被允许的，虽然它来自Navigator类内部：    
```scala
val other = new Navigator
other.speed //该行不鞥被编译
```    
　　将一个成员标记为private\[this\]，保证了它不会被同一个类的其他对象看到。这对于文档来说是有意义的。同时也方便我们编写更通用的型变注解。    

### 可见性和伴生对象    
　　在Java中，静态成员和实例成员同属一个类，因此访问修饰符对它们的应用方式是统一的。但Scala没有静态成员;而是用伴生对象来承载那些只存一次的
成员。例如下面代码中，Rocket对象就是Rocket类的伴生对象：    
```scala
class Rocket {
  import Rocket.fuel
  private def canGoHomeAgain = fuel > 20
}

object Rocket {
  private def fuel = 10
  def chooseStrategy(rocket: Rocket) = {
    if (rocket.canGoHomeAgain)
      goHome()
    else
      pickAStar()
  }
  def goHome() = {}
  def pickAStar() = {}
}
```    
　　Scala的访问规则在private和protected的处理上给伴生对象和类保留了特权。一个类会将它的所有访问权跟它的伴生对象共享，反过来也一样。    
　　Scala和Java在修饰符的方面的确很相似，不过有一个重要例外：protected static。Java中类C的protected static成员可以被C的所有子类访问。
而对于Scala的伴生对象而言，protected的成员没有意义，因为单例对象没有子类。    

***    
## Package-Objects    
　　前面这些能添加到包里的代码有类、特质和单例对象。这些是放在包内顶层最常见的定义。不过Scala允许放在包级别的并不止上述这些——任何能放在类级
别的定义，都能放到包级别。如果希望有在整个包都能用的助手方法，大可将它放在包的顶层。    
　　具体做法是把定义放在*包对象（package object）* 中。每个包都允许有一个包对象，任何被放在包对象里的定义都会被放做这个包本身的成员。    
```scala
//位于文件bobsdelights/package.scala中
package object bobsdelights {
  def showFruit(fruit: Fruit) = {
    import fruit._
    println(name + "s are" + color)
  }
}

//位于PrintMenu.scala中
package  pritmenu


object PrintMenu {
  def def main(args: Array[String]): Unit = {
    for (fruit <- Fruits.menu) = {
      showFruit(fruit)
    }
  }
}
```    
　　参考上面代码示例。package.scala这个文件包含了一个bobsdelights包的包对象。从语法上讲，包对象跟前面展示的花括号“打包”很像。唯一的区别
是包对象包含了一个object关键字。这是一个包对象，而不是一个包。花括号括起来的部分可以包含任何想添加的定义。    
