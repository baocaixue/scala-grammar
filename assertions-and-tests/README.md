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

