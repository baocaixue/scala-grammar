## 使用列表    
- List字面量...................................................[1](#List-Literals)
- List类型...................................................[2](#The-List-Type)
- 构建列表...................................................[3](#Construction-Lists)
- 列表的基本操作...................................................[4](#Basic-Operations-On-Lists)
- 列表模式...................................................[5](#List-Patterns)
- List类的初阶方法...................................................[6](#First-Order-Methods-On-Class-List)
- List类的高阶方法...................................................[7](#Higher-Order-Methos-On-Class-List)
- List对象的方法...................................................[8](#Methods-Of-The-List-Object)
- 同时处理多个列表...................................................[9](#Processing-Multiple-Lists)
- 理解Scala的类型推断算法...................................................[10](#Scalas-Type-Inference-Algorithm)    

***    
## List-Literals    
　　下面是表示列表的一些例子：    
```scala
val fruit = List("apples", "oranges", "pears")
val nums = List(1, 2, 3)
val diag3 = 
    List(
      List(1, 0, 0),
      List(0, 1, 0),
      List(0, 0, 1)
    )
val empty = List()
```    
　　列表和数组非常向，不过有两个重要的区别。首先，列表是不可变的。也就是说，列表的元素不能通过赋值改变。其次，列表的结构是递归的（即链表，
linked list），而数组是平的。    

***    
## The-List-Type    
　　跟数组一样，列表也是同构（homogeneous）的：同一个列表的所有元素都必须是相同的类型。元素类型为T的列表的类型写作`List[T]`。例如，以下
是同样的四个列表显式添加了类型之后的样子：    
```scala
val fruit: List[String] = List("apples", "oranges", "pears")
val nums: List[Int] = List(1, 2, 3, 4)
val diag3: List[List[Int]] =
    List(
      List(1, 0, 0),
      List(0, 1, 0),
      List(0, 0, 1)
    )
val empty: List[Nothing] = List()
```    
　　Scala的列表类型是*协变（covariant）* 的。对每一组类型S和T，如果S是T的子类型，那么`List[S]`就是`List[T]`的子类型。例如，`List[String]`
是`List[Object]`的子类型。因为每个字符串列表也都可以被当作对象列表。    
　　注意，空列表的类型为`List[Nothing]`。在Scala的类继承关系中，Nothing是底类型。由于列表是协变的，对于任何T而言，`List[Nothing]`都是
`List[T]`的子类型。因此既然空列表对象的类型为`List[Nothing]`，可以被当作其他形如`List[T]`类型的对象。这也是为什么编译器允许编写如下的代
码：    
```scala
val xs: List[String] = List()
```    

***    
## Construction-Lists    
　　所有的列表都构建自两个基础的构建单元：`Nil`和`::`（读做“cons”）。`Nil`表示空列表。中缀操作符`::`表示在列表前追加元素。也就是说，`x :: xs`
表示这样一个列表：第一个元素为x，接下来是列表xs的全部元素，因此，前面的列表值也可以这样定义：    
```scala
val fruit = "apple" :: ("oranges" :: ("pear" :: Nil))
val nums = 1 :: (2 :: (3 :: (4 :: Nil)))
val diag3 = (1 :: (0 :: (0 :: Nil))) ::
            (0 :: (1 :: (0 :: Nil))) ::
            (0 :: (0 :: (1 :: Nil))) :: Nil
val empty = Nil
```    
　　事实上，之前用`List(...)`对fruit、nums、diag3和empty的定义，不过是最终展开成上面这些定义的包装方法而已。由于`::`以冒号结尾，`::`
这个操作符是右结合的：`A :: B :: C`会被翻译成`A :: (B :: C)`。因此，可以在前面的定义中去掉圆括号。    

***    
## Basic-Operations-On-Lists    
　　对列表的所有操作都可以用下面这三项来表述：    
- head    返回列表的第一个元素
- tail    返回列表中除第一个外的所有元素
- isEmpty    返回列表是否为空列表    
　　这些操作在List类中定义为方法。head和tail方法只对非空列表有定义。当从一个空列表调用时，它们将抛出异常：
`java.util.NoSuchElementException: head of empty list`。    
　　作为如何处理列表的例子，考虑按升序排列一个数字列表的元素。一个简单的做法是*插入排序（insertion sort）*，这个算法的工作原理如下：对于非
空列表`x :: xs`，先对xs排序，然后将第一个元素x插入到这个排序结果中正确的位置：    
```scala
def sort(xs: List[Int]): List[Int] = {
  if (xs.isEmpty) Nil
  else insert(xs.head, sort(xs.tail))
}
def insert(head: Int, sortedTail: List[Int]): List[Int] = {
  if (sortedTail.isEmpty || head <= sortedTail.head) head :: sortedTail
  else sortedTail.head :: insert(head, sortedTail.tail)
}
```    

***    
## List-Patterns    
　　
