# 断言和测试    
- 断言...................................................[1](#Assertion)
- 用Scala写测试...................................................[2](#Testing-In-Scala)
- 失败报告...................................................[3](#Infomative-Failure-Reports)
- 作为规格说明的测试...................................................[4](#Tests-As-Specifications)
- 基于性质的测试...................................................[5](#Property-Based-Testing)
- 组织和运行测试...................................................[6](#Organizing-And-Running-Tests)    

***    
## Assertion    
　　在Scala中，断言的写法是对预定义方法assert的调用（assert方法定义在Predef单例对象中，每个Scala源文件都会自动引入该单例对象的成员）。如
果condition不满足，表达式assert(condition)将抛出AssertionError。assert还有另一个版本：assert(condition, explanation)，首先检查
condition是否满足，如果不满足，那么就抛出包含给定explanation的AssertionError。explanation的类型为Any，因此可以传入任何对象。assert
方法将调用explanation的toString方法来获取一个字符串的解释放入AssertionError。例如，前面示例的
[Element](../composition-and-inheritance/src/main/scala/com/isaac/ch10/Element.scala)类中名为“above”的方法，可以在对widen的
调用之后加入一行断言来确保被加宽的（两个）元素具有相同的宽度。    
```scala
def above(that: Element): Element = {
  val this1 = this widen that.width
  val that1 = that widen this.width
  assert(this1.width == that1.width)
  elem(this1.contents ++ that1.contents)
}
```    
　　另一种实现方式可能是在widen方法末尾，返回结果之前，检查两个宽度值是否相等。具体做法是将结果存放在一个val中，对结果进行断言，然后最后写
上这个val，这样一来，如果断言成功，结果就会被正常返回。不过，也可以用更精简的代码来完成：即Predef的ensuring方法：    
```scala
private def widen(w: Int): Element = 
    if (w < width)
      this
    else {
      val left = elem(' ', (w - width) / 2, height)
      val right = elem(' ', w -width - left.width, height)
      left beside this beside right
    } ensuring(w <= _.width)
```    
　　ensuring这个方法可以被用于任何结果类型，这得益于一个隐式转换。虽然这段代码看傻瓜你去调用的是widen结果的ensuring方法，实际上调用的是某
个可以从Element隐式转换得到的类型的ensuring方法。该方法接收一个参数，这是一个接收结果类型参数并返回Boolean的前提条件的函数。ensuring所做
的，就是把计算结果传递给这个前提条件函数。如果前提条件函数返回true，那么ensuring就正常返回结果;如果前提条件返回false，那么ensuring将抛出
AssertionError。    
　　在本例中，前提条件函数是”w <= _.width“。这里的下划线是传入该函数的入参的占位符，即调用widen方法的结果：一个Element。如果作为w传入widen
方法的宽度小于或等于结果Element的width，这个前提条件函数将得到true的结果，这样ensuring就会返回被调用的那个Element结果。由于是widen方法
的最后一个表达式，widen本身的结果也就是这个Element。    
　　断言可以用JVM的命令行参数-ea和-da来分别打开或关闭。打开时，断言就像是一个个小测试，用的是运行时得到的真实数据。在下面，我们将把精力集中
在如何编写外部测试上，这些测试自己提供测试数据，并且独立于应用程序执行。    

***    
## Testing-In-Scala    
　　用Scala写测试，有很多选择，从已被广泛认可的Java工具，比如JUnit和TestNG，到用Scala编写的工具，比如ScalaTest、specs2和ScalaCheck。
首先，从ScalaTest开始。    
　　ScalaTest是最灵活的Scala测试框架：可以很容易地定制它来解决不同的问题。ScalaTest的灵活性意味着团队可以使用任何最能满足他们需求的测试
风格。例如，对于熟悉JUnit的团队，FunSuite风格是最舒适和熟悉的：    
```scala
import org.scalatest.FunSuite
import Element.elem

class ElementSuite extends FunSuite{
  test("elem result should have passed width") {
    val ele = elem('x', 2, 3)
    assert(ele.width == 2)
  }
}
```    
　　ScalaTest的核心概念是*套件（suite）* ，即测试的集合。所谓测试可以是任何带有名称，可以被启动，并且要么成功，要么失败，要么被暂停，要么
被取消的代码。在ScalaTest中，Suite特质是核心组合单元。Suite声明了一组“生命周期”方法，定义了运行测试的默认方法，我们也可以重写这些方法来
对测试的编写和运行进行定制。    
　　ScalaTest提供了*风格特质（style trait）* ，这些特质扩展Suite并重写了生命周期方法来支持不同的测试风格。它还提供了*混入特质*，这些特质
重写了生命周期方法来满足特定的测试需要。可以组合Suite的风格和混入特质来定义测试类，以及通过编写Suite实例来定义测试套件。    
　　上面示例中的测试类扩展自FunSuite，这就是风格特质的一个例子。FunSuite中的“Fun”指的是函数;而“test”是定义在FunSuite中的一个方法，该方
法被ElementSuite的主构造方法调用。可以在圆括号中用字符串给出测试的名称，并在花括号中给出具体的测试代码。测试代码是一个以传名参数传入test的
函数，test将这个函数登记下来，稍后执行。    
　　ScalaTest已经被集成进常见的构建工具（比如sbt和Maven）和IDE（比如IntelliJ IDEA和Eclipse）。也可以通过ScalaTest的Runner应用程序直
接运行Suite，或者在Scala解释器中简单地调用它的execute方法，比如：    
```shell script
scala> (new ElementSuite).execute()
ElementSuite:
- elem result should have passed width
```    
　　ScalaTest的所有风格，包括FunSuite在内，都被设计为鼓励编写专注的、带有描述性名称的测试。不仅如此，所有的风格都会生成规格说明书般的输出，
方便在干系人之间交流。所选择的风格只规定了测试代码长什么样，不论选择什么样的风格，[ScalaTest](http://www.scalatest.org/)的运行机制都始
终保持一致。    

***    

