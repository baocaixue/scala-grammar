# 样例类和模式匹配    
- 一个简单的例子...................................................[1](#A-Sample-Example)    
- 模式的种类...................................................[2](#Kinds-Of-Pattern)    
- 模式守卫...................................................[3](#Pattern-Guards)    
- 模式重叠...................................................[4](#Pattern-Overlaps)    
- 密封类...................................................[5](#Sealed-Classes)    
- Option类型...................................................[6](#The-Option-Type)    
- 到处都是模式...................................................[7](#Patterns-Everywhere)    
- 一个复杂的例子...................................................[8](#A-Larger-Example)    
    
　　本节关于Scala中的*样例类（case class）* 和*模式匹配（pattern matching）* ，这组孪生的语法结构为我们编写规则的、未封装的数据结构提供
支持。这两个语法结构对于表达树形的递归数据尤其有用。    
　　模式匹配是函数式编程中的相关概念。而样例类是Scala用来对对象进行模式匹配而并不需要大量的样板代码的方式。笼统地说，要做的就是对那些希望能
做模式匹配的类加上一个case关键字。    

***    
## A-Sample-Example    
　　在深入探讨模式匹配的所有规则和细节之前，有必要先看一个简单的例子，好让我们明白模式匹配大概是做什么的。假定需要编写一个操作算数表达式的类
库，可能这个类库是正在设计的某个领域特性语言（DSL）的一部分。    
　　解决这个问题的第一步是是定义输入数据。为保持简单，我们将注意力集中在由变量、数，以及一元和二元操作符组成的算数表达式上。用Scala的类层次
结构来表达：    
```scala
abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr
```    
　　这个层次结构包括一个抽象的基类Expr和四个子类，每一个都表示我们要考虑一种表达式。所有五个类的定义体都是空的。如之前所提到的那样，Scala允
许省去空定义体的花括号。    

### 样例类    
　　上面示例中另一个值得注意的点是每个子类都有一个case修饰符。带有这种修饰符的类称作*样例类（case class）*。用上这个修饰符会让Scala编译器
对我们的类添加一些语法上的便利。    
　　首先，它会添加一个**跟类同名的工厂方法**。这意味着可以用“Var("x")”来构造一个Var对象，而不是稍长版本的“new Var("x")”。当需要嵌套定义
时，工厂方法尤为有用。由于代码中不再到处落满new关键字，可以一眼就看明白表达式的结构`val op = BindOp("+", Number(1), v)`。    
　　其次，第二个语法上的便利是参数列表中的参数都**隐式地获得了一个val前缀**，因此它们会被当作字段处理。    
　　再次，编译器会帮我们以“自然”的方式**实现toString、hashCode和equals方法**。这些方法分别会打印、哈希、比较包含类及所有入参的整棵树。由于
Scala的==总是代理给equals方法，这意味着样例类表示的元素总是以结构化的方式做比较：`op.right == Var("x")`。    
　　最后，编译器还会添加一个**copy方法**用于制作修改过的拷贝。这个方法用于制作一两个属性不同之外其余完全相同的该类的新实例。这个方法用到了
*带名字的参数*和*缺省参数*。用带名字的参数给出想要的修改。对于任何没有给出名字的参数，都会用老对象中的原值：`op.copy(operator = "=")`。    
　　所有这些带来的是大量的便利（代价却很小）。你需要多写一个case修饰符，并且你的类和对象会变得大那么一点。之所以更大，是因为生成了额外的方法，
并且对于构造方法的每个参数都隐式地添加字段。不过，样例类最大的好处是它们支持模式匹配。    

### 模式匹配    
　　假定想要简化算数表达式。可用的简化规则非常多，以下只列举一部分：    
```
UnOp("-", UnOp("-", e)) => e //双重取负
BinOp("+", e, Number(0)) => e //加0
BinOp("*", e, Number(1)) => e //乘1 
```     
　　用模式匹配的话，这些规则可以被看成是一个Scala编写的简化函数的核心逻辑，如下代码所示。可以这样来使用这个simplifyTop函数：    
```scala
def simplifyTop(expr: Expr): Expr = expr match {
  case UnOp("-", UnOp("-", e)) => e
  case BinOp("+", e, Number(0)) => e
  case BinOp("*", e, Number(1)) => e
  case _ => expr
}

simplifyTop(UnOp("-", UnOp("-", Var("x"))))
```    
　　simplifyTop的右边由一个match表达式组成。match表达式对应Java的switch，不过match关键字出现在选择器表达式后面。模式匹配包含一系列以
case关键字打头的*可选分支*。每一个可选分支都包括一个模式以及一个或多个表达式，如果模式匹配了，这些表达式就会被求值。箭头符`=>`用于将模式和
表达式分开。    
　　一个match表达式的求值过程是*按照模式给出的顺序逐一尝试的*。第一个模式匹配上后，表达式求值，然后返回。    
　　类似“+”和1这样的*常量模式*可以匹配那些按照`==`的要求跟它们相等的值。而像e这样的*变量模式*可以匹配任何值。匹配后，在右侧的表达式中，这个
变量指向这个匹配的值。在本例中，注意前三个可选分支都求值为e，一个在对应的模式中绑定的变量。*通配模式*，即_也匹配任何值，不过它并不会引入一个
变量名来指向这个值。在上面示例中，注意match是以一个缺省的什么都不做的case结尾的，这个缺省的case直接返回用于匹配的表达式expr。    
　　*构造方法模式*看上去就像UbOp("-", e)。这个模式匹配所有类型为UnOp且首个入参匹配"-"而第二个入参匹配e的值。注意构造方法的入参本身也是模
式。这允许我们用精简的表示法来编写有深度的模式。例如`UnOp("-", UnOp("-", e))`。    

### 对比match和switch    
　　match表达式可以被看作Java风格的switch的广义化。Java风格的switch可以很自然地用match表达式表达，其中每个模式都是常量且最后一个模式可以
是一个通配模式（代表switch中的模式case）。    
　　不过，需要记住三个区别：首先，Scala的match是一个表达式（也就是说它总能得到一个值）。其次，Scala的可选分支不会贯穿到下一个case。最后，
如果没有一个模式匹配上，会抛出名为MatchError的异常。这意味着你需要确保所有的case被覆盖到，哪怕这意味着你需要添加一个什么也不做的缺省case。    
　　参考下面示例。第二个case是必要的，因为没有它的话，match表达式对于任何非BinOp的expr入参都会抛出MatchError。在本例中，对于第二个case，
并没有给出任何代码，因此如果这个case被运行，什么都不会发生。两个case的结果都是unit值，即()，这也是这个match表达式的结果。    
```scala
expr match {
  case BinOp(op, left, right) =>
    println(expr + " is a binary operation")
  case _ =>
}
```    

***    
## Kinds-Of-Pattern    
　　所有的模式跟相应的表达式看上去完全一样。例如，基于上面的类层次结构，Var(x)这个模式将匹配任何变量表达式，并将x绑定成这个变量的名字。作为
表达式使用时，Var(x)——完全相同的语法——将重新创建一个等效的对象，当然前提是x已经绑定到这个变量名。由于模式的语法是透明度，我们只需要关心能
使用哪几种模式就对了。    

### 通配模式    
　　通配模式（\_）会匹配任何对象。前面已经看过通配模式用于却生、捕获所有的可选路径，就像这样：    
```scala
expr match {
  case BinOp(op, left, right) => println(expr + " is a binary operation")
  case _ => //处理默认case
}
```    
　　通配模式还可以用来忽略某个对象中你并不关心的局部。例如，前面这个例子实际上并不需要关心二元操作的操作元是什么，它只是检查这个表达式是否是
二元操作，仅此而已。因此，这段代码也完全可以用通配模式来表示BinOp的操作元：    
```scala
expr match {
  case BinOp(_, _, _) => println(expr + " is a binary operation")
  case _ => println("It's something else")
}
```    

### 常量模式    
　　常量模式仅匹配自己。任何字面量都可以作为常量（模式）使用。例如，5、true和"hello"都是常量模式。同时，任何val或单例对象也可以被当作常量
（模式）使用。例如，Nil这个单例对象能且仅能匹配空列表。下面给出了常量模式的例子：    
```scala
def describe(x: Any) = x match {
  case 5 => "five"
  case true => "truth"
  case "hello" => "hi!"
  case Nil => "the empty list"
  case _ => "something else"
}
```    

### 变量模式    
　　变量模式匹配任何对象，这一点跟通配模式相同。不过不同于通配模式的是，Scala将对应的变量绑定成匹配上的对象。在绑定之后，就可以用这个变量来
对对象做进一步的处理。下面示例给出了一个针对零的特例和针对所有其他值的缺省处理的模式匹配。缺省的case用到了变量模式，这样就给匹配的值赋予了一
个名称。    
```scala
expr match {
  case 0 => "zero"
  case somethingElse => "not zero: " + somethingElse
}
```    
　　*变量还是常量？*    
　　常量模式也可以有符号形式的名称。当我们把Nil当作一个模式的时候，实际上就是在用一个符号名称来引用常量。这里有一个相关的例子，这个模式匹配
牵扯到常量E（2.71828...）和Pi（3.14159...）。    
```scala
import math.{E, Pi}
E match {
  case Pi => "strange math? Pi = " + Pi
  case _ => "OK"
}
```    
　　跟预期一样，E并不匹配Pi，因此“strange math”这个case没有被使用。Scala编译器是如何知道Pi是从scala.math包引入的常量，而不是一个代表选
择器值本身的变量呢？Scala采用了一个简单的词法规则来区分：一个以小写字母打头的简单名称会被当作模式变量来处理;所有其他引用都是常量。下面展示了
具体的区别，给Pi创建一个小写的别名。    
```scala
import math.{E,Pi => pi}
E match {
  case pi => "strange math? Pi = " + pi
}
```    
　　在这里编译器甚至不允许添加一个默认的case。由于pi是变量模式，它将会匹配所有的输入，因此不可能走到后面的case。如果需要，仍然可以用小写的
名称来作为模式常量，有两个技巧。首先，如果常量是莫个对象的字段，可以在字段名前加上限定词。例如，虽然pi是个变量模式，但this.pi或obj.pi是常
量（模式），尽管它们以小写字母打头。如果这样不行（比如pi可能是个局部变量），也可以用反引号将这个名称包起来。例如\`pi\`就能再次被编译器解读
为一个常量。    
　　给标识符加上反引号在Scala中有两种用途，用来帮助从不寻常的代码场景中走出来。这里的是如果将小写字母打头的标识符用作模式匹配中的常量。也可
以用反引号将关键字当作普通的标识符，比如Thread.\`yield\`()这儿段代码将yield当作标识符而不是关键字。    

### 构造方法模式    
　　构造方法模式是真正体现出模式匹配威力的地方。一个构造方法模式看上去像这样：`BindOp("+", e, Number(0))`。它由一个名称（BindOp）和一组
圆括号中的模式："+"、e和Number(0)组成。假定这里的名称指的是一个样例类，这样的一个模式将首先检查被匹配的对象是否以这个名称命名的样例类的实
例，然后再检查这个对象的构造方法参数是否匹配这些额外给出的模式。    
　　这些额外的模式意味着Scala的模式匹配支持*深度匹配（deep match）*。这样的模式不仅检查给出的对象的顶层，还会进一步检查对象的内容是否匹配
额外的模式要求。由于额外的模式也可能是构造方法模式，用它们来检查对象内部时可以到任意的深度。例如，下面给出的模式将检查顶层的对象是BinOp，而
它的第三个构造方法参数是一个Number，且这个Number的值字段为0.这是一个长度只有一行但深度有三层的模式：    
```scala
expr match {
  case BinOp("+", e, Number(0)) => println("a deep match")
  case _ =>
}
```    

### 序列模式    
　　就跟样例类匹配一样，也可以跟序列类型做匹配，比如List或Array。使用语法是相同的，不过现在可以在模式中给出任意数量的元素。如下显示了一个以
0开始的三元素列表的模式：    
```scala
expr match {
  case List(0, _, _) => println("found it")
  case _ =>
}
```    
　　如果想匹配一个序列，但又不想给出多长，可以用\_\*作为模式的最后元素。它能匹配序列中任意数量的元素，包括0个元素。    

### 元组模式    
　　我们还可以匹配元组。形如(a, b, c)这样的模式能匹配任意的三元组。    
```scala
def tupleDemo(expr: Any) = 
    expr match {
      case (a, b,c) => println("matched" + a + b + c)
      case _ =>
    }
```     

### 带类型的模式    
　　可以用*带类型的模式（typed pattern）* 来代替类型测试和类型转换：    
```scala
def generalSize(x: Any) = x match {
  case s: String => s.length
  case m: Map[_, _] => m.size
  case _ => -1
}

generalSize("abc")//3
generalSize(Map(1 -> 'a', 2 ->'b'))//2
generalSize(Math.PI)//-1
```    
　　generalSize方法返回不同类型的对象的大小或长度。其入参的类型是Any，因此可以是任何值。如果入参是String，那么方法将返回这个字符串的长度。
模式“s: String”是一个带类型的模式，它将匹配每个（非null）String实例。其中模式变量s将指向这个字符串。    
　　需要注意的是，尽管s和x指向同一个值，x的类型是Any，而s的类型是String。因此可以在与模式想对应的可选分支中使用s.length，但不能写成x.length，
因为类型Any没有一个叫做length的成员。    
　　另一个跟用带类型的模式匹配等效但是更冗长的方式是做类型测试然后（强制）类型转换。对于类型测试和转换，Scala和Java的语法不太一样。比方说要
测试某个表达式expr的类型是否为String，需要这样`expr.isInstanceOf[String]`，要将这个表达式转换成String类型，需要用`expr.asInstanceOf[String]`
。通过类型测试和类型转换可以重写上述示例：    
```scala
//不良风格
def generalSize(x: Any) = 
    if (x.isInstanceOf[String]) {
      val s = x.asInstanceOf[String]
      s.length
    } else {
      //...
    }
```    
　　isInstanceOf和asInstanceOf两个操作符或被当作Any类预定义方法处理，这两个方法接收一个用方括号括起来的类型参数。事实上，
`x.asInstanceOf[String]`是该方法调用的一个特例，它带上了显式类型参数String。    
　　在Scala中编写类型测试和类型检查会比较啰嗦。这里是为了展示有意为之，这并不是一个值得鼓励的做法。通常，使用带类型的模式会更好，尤其是当你
需要同时做类型测试和类型转换的时候，因为这两个操作所做的事情会被并在单个模式匹配中完成。    
　　示例中的match表达式的第二个case包含了带类型的模式“m: Map\[\_, \_\]”。这个模式匹配是任何Map值，不管它的键和值的类型是什么，然后让m指
向这个值。因此，m.size的类型是完备的，返回的是这个映射（map）的大小。类型模式中的下划线就像是其他模式中的通配符。除了用下划线，也可以用
（小写的）类型变量。    
　　*类型擦除*    
　　除了笼统的映射，我们还能测试特定元素类型的映射么？这对于测试某个值是否是Int到Int的映射这类场景会很方便：    
```scala
def isIntIntMap(x: Any) = x match {
  case m: Map[Int, Int] => true
  case _ => false
}
isIntIntMap(Map(1->"a"))//true
```    
　　Scala采用了擦除式的泛型，就跟Java一样。这意味着在运行时并不会保留类型参数的信息。这么一来，在运行时就无法判断某个给定的Map对象是用两个
Int的类型参数创建的，还是其他什么类型参数创建的。系统能做的判断只是判断某个值是某种不确定类型参数的Map。    
　　对于这个擦除规则唯一的例外是数组，因为Java和Scala都对它们做了特殊处理。数组的元素是跟数组一起保存的，因此可以对它进行模式匹配：    
```scala
def isStringArray(x: Any) = x match {
  case a: Array[String] => true
  case _ => false
}

isStringArray(Array(1,2))//false
```    

### 变量绑定    
　　除了独自存在的变量模式外，还可以对任何其他模式添加变量。只需要写下变量名、一个@符和模式本身，就得到一个变量绑定模式。意味着这个模式将跟
平常一样执行模式匹配，如果匹配成功，就像简单的变量模式一样。示例中给出一个（表达式）查找绝对值操作被连续应用两次的模式匹配的例子：    
```scala
expr match {
  case UnOp("abs", e @ UnOp("abs", _)) => e
  case _ => 
}
```    
　　示例包括了一个以e为变量，UnOp("abs", \_)为模式的变量绑定模式。如果整个匹配成功了，那么匹配了UnOp("abs", \_)的部分就被赋值给变量e。
这个case的结果就是e，这好似因为e跟expr的值相同，但是少了一次求绝对值的操作。    

***    
## Pattern-Guards    
　　有时候语法级的模式匹配不够精确。举例来说，假定我们需要公式化一个简单规则，即用乘以2（e * 2）来替换对两个相同操作元的加法（e + e）。在
表示Expr中，下面这样的表达式：    
　　`BinOp("+", Var("x"), Var("x"))`    
　　应用该简化规则后将得到：    
　　`BinOp("*", Var("x"), Number(2))`    
　　而如果使用如下这样来定义规则：    
```scala
def simplifyAdd(e: Expr) = e match {
  case BinOp("+", x, x) => BinOp("*", x, Number(2))
  case _ => e
}
```    
　　这样做会失败，因为Scala要求模式是线性的：*同一个模式变量在模式中只能出现一次*。不过，我们可以用*模式守卫*来重新定义这个匹配逻辑：    
```scala
def simplifyAdd(e: Expr) = e match {
  case BinOp("+", x, y) if x == y =>
    BinOp("*", x, Number(2))
  case _ => e
}
```    
　　模式守卫出现在模式之后，并以if打头。模式守卫可以是任意的布尔表达式，通常会引用到模式中的变量。如果存在模式守卫，这个匹配仅在模式守卫值
得到为true时才会成功。因此，上面提到的首个case只能匹配那些两个操作元相等的二元操作。    
　　以下是其他一些带有守卫的模式示例：    
```scala
//只匹配正整数
case n: Int if n > 0 => ...
//只匹配以字母'a'打头的字符串
case s: String if s(0) == 'a' => ...
```    

***    
## Pattern-Overlaps    
　　模式会按照代码中的顺序逐个被尝试。下面展示了模式中的case出现顺序的重要性：    
```scala
def simplifyAll(expr: Expr): Expr = expr match {
  case UnOp("-", UnOp("-", e)) =>
    simplifyAll(e) //取反
  case BinOp("+", e, Number(0)) =>
    simplifyAll(e)
  case BinOp("*", e, Number(1)) =>
    simplifyAll(e)
  case UnOp(op, e) =>
    UnOp(op, simplifyAll(e))
  case BinOp(op, l, r) =>
    BinOp(op, simplifyAll(l), simplifyAll(r))
  case _ => expr
}
```    

***    

