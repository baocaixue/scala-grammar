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


