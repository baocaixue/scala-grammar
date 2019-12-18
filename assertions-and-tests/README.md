# 断言和测试    
- 断言...................................................[1](#Assertion)
- 用Scala写测试...................................................[2](#Testing-In-Scala)
- 失败报告...................................................[3](#Informative-Failure-Reports)
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
## Informative-Failure-Reports    
　　如果断言失败了，失败报告就会包括文件名和该断言所在的行号，以及一条翔实的错误消息：    
```shell script
scala> val width = 3
width: Int = 3
scala> assert(width == 2)
org.scalatest.exception.TestFailedException:
    3 did not equal 2
```    
　　为了在断言失败时提供描述性的错误消息，ScalaTest会在编译时分析传入每次assert调用的表达式。如果想要看到更详细的关于断言失败的信息，可以
使用ScalaTest的DiagrammedAssertions，其错误消息会显示传入assert的表达式的一张示意图：    
```shell script
scala> assert(List(1, 2, 3).contains(4))
org.scalatest.exceptions.TestFailedException:
    assert(List(1, 2, 3).contains(4))
           |    |  |  |  |        |
           |    1  2  3  false    4
           List(1, 2, 3)
```    
　　ScalaTest的assert方法并不在错误消息中区分实际和预期的结果，它们仅仅是提示我们左侧的操作元跟右侧的操作元不想等，或者在示意图中显示出表
达式的值。如果想强调实际和预期的差别，可以换用ScalaTest的assertResult方法：    
```scala
assertResult(2) {
  ele.width
}
```    
　　通过这个表达式，表明了预期花括号中的代码的执行结果是2。如果换括号中的代码的执行结果是3,将会在失败报告中看到“Expected 2, but got 3”这
样的消息。
　　如果想要检查某个方法抛出某个预期的异常，可以用ScalaTest的assertThrows方法：    
```scala
assertThrows[IllegalArgumentException] {
  ele('x', -2, 3)
}
```    
　　如果花括号中的代码抛出了不同于预期的异常，或者并没有抛出异常，assertThrow将以TestFailedException异常中止。将在失败报告中得到一个对
排查问题有帮助的错误消息，比如：    
```
Expected IllegalArgumentException to be thrown,
    but NegativeArraySizeException was thrown.
```    
　　而如果代码以传入的异常类的实例异常中止（即代码抛出了预期的异常），assertThrows将正常返回。如果想要进一步检视预期的异常，可以使用intercept
而不是assertThrows。intercept方法跟assertThrows的运行机制不同，不过当异常被抛出时，intercept将返回这个异常：    
```scala
val caught = 
    itercept[ArithmeticException] {
      1 / 0
    }
```    
　　简而言之，ScalaTest的断言会尽其所能提供有助于诊断和修复代码问题的失败消息。    

***    
## Tests-As-Specifications    
　　*行为驱动开发（BDD）* 测试风格的重点是编写人可读的关于代码预期行为的规格说明，同时给出验证代码具备指定行为的测试。ScalaTest包含了若干
特质来支持这种风格的测试。西面示例给出了这样的一个特质FlatSpec的例子：    
```scala
import org.scalatest.{FlatSpec, Matchers}
import Element.elem

/*
  用ScalaTest的FlatSpec描述并测试代码行为
 */
class ElementSpec extends FlatSpec with Matchers{
  "A UniformElement" should "have a width equal to the passed value" in {
    val ele = elem('x', 2, 3)
    ele.width should be (2)
  }

  it should "have a height equal to the passed value" in {
    val ele = elem('x', 2, 3)
    ele.height should be (3)
  }

  it should "throw an IAE if passed a negative width" in {
    an [IllegalArgumentException] should be thrownBy {
      elem('x', -2, 3)
    }
  }
}
```    
　　在FlatSpec中，我们以*规格子句（specifier clause）* 的形式编写测试。我们先写下以字符串表示的要测试的*主题（subject）*（即“A 
UniformElement”），然后是should（must或can），再然后是一个描述该主题需要具备某种行为的字符串，再接下来是in。在in后面的花括号中，我们编
写用于测试指定行为的代码。在后续的子句中，可以用it来指代最近给出的主题。当一个FlatSpec被执行时，它将每个规格子句作为ScalaTest测试运行。FlatSpec
（以及ScalaTest的其他规格说明特质）在运行后将生成读起来像规格说明书的输出。例如，以下就是在解释器中运行上述示例的ElementSpec时输出的样子：    
```shell script
scala> (new ElementSpec).execute()
A UniformElement
- should have a width equal to the passed value
- should have a height equal to the passed value
- should throw an IAE if passed a negative width
```    
　　上面示例中还展示了ScalaTest的*匹配器（matcher）* 领域特定语言（DSL）。通过混入Matchers特质，可以编写读上去更像自然语言的断言。ScalaTest
在其DSL中提供了许多匹配器，并允许你用定制的失败消息定义新的matcher。示例中的匹配器包括“should be”和“an \[...\] should be thrownBy 
{...}”这样的语法。如果相比should更喜欢用must，也可以选择混入MustMatchers。例如，混入MustMatchers将允许编写这样的表达式：    
```scala
result must be >= 0
map must contain key 'c'
```    
　　如果最后的断言失败了，将看到类似于下面这样的错误消息：    
```
Map('a' -> 1, 'b' -> 2) did not contain key 'c'
```    
　　[specs2](http://etorreborre.github.io/spec2/)测试框架是Eric Torreborre用Scala编写的开源工具，也支持BDD风格的测试，不过语法可
能不太一样。    
　　BDD的一个重要思想是测试可以在那些决定软件系统应该做什么的人、那些实现软件的人和那些判定软件是否完成并正常工作的人之间架起一道沟通的桥梁。
虽然ScalaTest和specs2的任何一种风格都可以这样来用，但是ScalaTest的FeatureSpec是专门设计的：    
```scala
import org.scalatest.{FeatureSpec, GivenWhenThen}

class TVSetSpec extends FeatureSpec with GivenWhenThen{
  feature("TV power button") {
    scenario("User presses power button when TV is off") {
      Given("a TV set that is switched off")
      When("the power button is pressed")
      Then("the TV should switch on")
      pending
    }
  }
}
```    
　　FeatureSpec的设计目的是引导关于软件需求的对话：必须指明具体的*功能（feature）*，然后用*场景（scenario）* 来描述这些功能。Given、When、
Then方法（由GivenWhenThen特质提供）能帮助我们将对话聚焦在每个独立场景的具体细节上。最后的pending调用表明测试和实际的行为都还没有实现——这
里只是规格说明。一旦所有的测试和给定的行为都实现了，这些测试就会通过，我们就可以说需求已经满足。    

***    
## Property-Based-Testing    
　　Scala的另一个有用的测试工具是ScalaCheck，这是由Rickard Nilsson编写的开源框架。ScalaCheck让你能够指定被测试的代码必须满足的性质。对
每个性质，ScalaCheck都会生成数据并执行断言，检查代码是否满足该性质。下面示例给出了一个混入了PropertyChecks特质的WordSpec的ScalaTest中
使用ScalaCheck的例子：    
```scala
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks
import org.scalatest.MustMatchers._
import Element.elem

class ElementSpec1 extends WordSpec with PropertyChecks{
  "elem result" must {
    "have passed width" in {
      forAll{ w: Int =>
        whenever (w > 0) {
          elem('x', w, 3).width must equal (w)
        }
      }
    }
  }
}
```    
　　WordSpec是一个ScalaTest的风格类。PropertyChecks特质提供了若干forAll方法，让你可以将基于性质的测试跟传统的基于断言或基于匹配器的测
试混合在一起。在本例中，检查了一个elem工厂必须需满足的性质。ScalaCheck的性质在代码中表现为以参数形式接收性质断言所需的函数值。这些数据将由
ScalaCheck代为生成。对于示例中的性质，数据是名为w的整数，代表宽度。在这个函数的函数体中，这段代码：    
```scala
whenever (w > 0) {
  elem('x', w, 3).width must equal (w)
}
```    
　　whenever子句表达的意思是，只要左边的表达式为true，那么右边的表达式也必须为true。本例中，只要w大于0,代码块中的表达式就必须为true。当
传给elem工厂的宽度跟工厂返回Element的宽度一致时，本例右侧表达式就会交出true。    
　　只需要这样一小段代码，ScalaCheck就会帮助我们生成数百条w可能的取值并对每一个执行测试，尝试找出不满足该性质的值。如果对于ScalaCheck尝试
的每个值，该性质都满足，测试就通过了。否则，测试将以TestFailedException终止，这个异常会包含关于造成该测试失败的指的信息。    

***    
