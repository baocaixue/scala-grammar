## 使用列表    
- List字面量...................................................[1](#List-Literals)
- List类型...................................................[2](#The-List-Type)
- 构建列表...................................................[3](#Construction-Lists)
- 列表的基本操作...................................................[4](#Basic-Operations-On-Lists)
- 列表模式...................................................[5](#List-Patterns)
- List类的初阶方法...................................................[6](#First-Order-Methods-On-Class-List)
- List类的高阶方法...................................................[7](#Higher-Order-Methods-On-Class-List)
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
　　列表也可以用模式匹配解开。列表模式可以逐一对应到列表表达式。既可以用`List(...)`这样的模式来匹配列表的所有元素，也可以用`::`操作符和Nil
常量一点点地将列表解开：    
```scala
val fruit = List("apples", "oranges", "pears")
val List(a, b, c) = fruit//a:"apples"  b:"oranges"  c:"pears"
val a1 :: b1 :: c1 :: Nil = fruit
```    
　　关于List的模式匹配    
```
   不论是List(...)还是::都不满足前面说到的可能出现的模式的形式。事实上，List(...)是一个由类库定义的提取器（extractor）模式的实例。而
x :: xs这样的“cons”模式是中缀操作模式的一个特例。作为表达式，中缀操作等同于一次方法调用。对模式而言，规则是不同的：作为模式，p op q这样的
中缀操作等同于op(p, q)。也就是说，中缀操作符op是被当作构造方法模式处理的。具体来说，x :: xs这个表达式相当于::(x, xs)。
   这里透露一个细节，应该有一个名为::的类与这个模式构造方法想对应。的确有这么一个类，它的名字叫scala.::，并且就是用来构建非空列表的。因此
::在Scala中出现了两次，一次作为scala包中的一个类的名字，一次是在List类的方法名。::方法的作用是产出一个scala.::类的实例。
```    
　　使用模式是用基本方法head、tail和isEmpty来解开列表的变通方式，例如，我们再次实现插入排序：    
```scala
def sort(xs: List[Int]): List[Int] = xs match {
  case List() => List()
  case x :: xs1 => insert(x, sort(xs1))
}
def insert(head: Int, sortedTail: List[Int]): List[Int] = sortedTail match {
  case List() => List(head)
  case y :: ys => if (head <= y) head :: sortedTail else y :: insert(head, ys)
}
```    

***    
## First-Order-Methods-On-Class-List    
　　如果一个方法不接收任何函数作为入参，就被称为*初阶（first-order）* 方法。下面是列表的一些初阶方法。    

### 拼接两个列表    
　　跟`::`操作相似的一个操作是拼接，写作`:::`。不同于`::`，`:::`接收两个列表参数作为操作元。`xs ::: ys`的结果是一个包含了xs所有元素，加
上ys所有元素的列表。这里有一些例子：    
```scala
List(1, 2) ::: List(3, 4, 5)//List(1, 2, 3, 4, 5)
List() ::: List(1, 2, 3)//List(1, 2, 3)
```    
　　跟cons类似，列表的拼接操作也是右结合的。    

### 分治（Divide and Conquer）原则    
　　拼接（:::）是作为List类的一个方法实现的。  也可以通过对列表进行模式匹配来“手工”实现拼接。首先，需要明确拼接方法的签名：    
```scala
def append[T](xs: List[T], ys: List[T]): List[T]
```    
　　要设计这样一个append方法，有必要回顾一下对于列表这样的递归数据结构的“分而治之”的程序设计原则。许多对列表的算法都首先会用模式匹配将输入
的列表切分成更小的样例。这是设计原则中“分”的部分。然后对每个样例构建对应的结果。如果结果是一个非空列表，那么这个列表的局部可以通过递归地调用
同一个算法来构建出来。这是设计原则中“治”的部分。    
　　把这个设计原则应用到append方法的实现，第一个问题是匹配哪一个列表。跟其他方法相比，append方法并不简单，因为有两个选择。好在后续的“治”部
分告诉我们需要同时包含两个输入列表的所有元素。由于列表是从后往前构建的，ys可以保持不动，而xs则需要被解开然后追加到ys的前面。这样一来，我们有
理由选择xs作为模式匹配的来源。匹配列表最常见的模式是区分空列表和非空列表：    
```scala
def append[T](xs: List[T], ys: List[T]): List[T] = xs match {
  case List() => ???
  case head :: tail => ???
}
```    
　　剩下的便是填充由???标出的两处（???这个方法在运行时会抛出scala.NotImplementedError，其结果类型为Noting，可以在开发过程中当作临时实现
来用）。第一出是当xs为空列表时的可选分支。这个case当中的拼接操作可以直接交出第二个列表：`case List() => ys`。    
　　第二处是当输入列表xs由某个头head和尾tail组成时的可选分支。这个case中结果也是一个非空列表。要构建一个非空列表，需要知道这个非空列表的头
和尾分别是什么。已经知道结果的第一个元素是head，而与下的元素可以通过将第二个列表ys拼接在第一个列表的剩余部分即tail之后：    
```scala
def append[T](xs: List[T], ys: List[T]): List[T] = xs match {
  case List() => ys
  case head :: tail => head :: append(tail, ys)
}
```    
　　第二个可选分支的计算展示了分治原则中“治”的部分：首先思考我们想要的输出的形状是什么，然后计算这个形状当中的各个独立的组成部分，在这个过程
中的必要环节递归的调用同一个算法。最后，从这些组成部分构建出最终的输出结果。    

### 获取列表长度：length    
　　length方法计算列表的长度。不同于数组，在列表上的length操作相对更耗资源。找到一个列表的末尾需要遍历整个列表，因此需要消耗与元素数量成正
比的时间。这也是为什么将`xs.isEmpty`这样的测试换成`xs.length==0`并不是个好主意。这两种测试的结果没有什么区别，但第二个会更慢，尤其当xs很
长时。    

### 访问列表的末端：init和last    
　　head和tail分别都一个对偶（dual）方法：last返回（非空）列表的最后一个元素，而init返回除了最后一个元素之外的剩余部分。但是，不像head和
tail那样在运行的时候消耗常量时间，init和last需要遍历整个列表来计算结果，因此它们的耗时跟列表的长度成正比。所以，最好将数据组织成大多数访问
都发生在头部而不是尾部。    

### 反转列表：reverse    
　　如果在算法当中某个点需要频繁地访问列表的末尾，有时候先将列表反转，再对反转后的列表做操作是更好的做法。跟所有其他列表操作一样，reverse会
创建一个新的列表，而不是对传入的列表做修改。由于列表是不可变的，这样的修改是做不到的。    
```scala
val list = List(1, 2, 3)
list.reverse.head == list.last
list.reverse.tail == list.init
```    

### 前缀和后缀：drop、take和splitAt    
　　drop和take是对tail和init的一般化。它们返回的是列表任意长度的前缀或后缀。表达式“xs take n”返回列表的前n个元素。如果n大于xs.length，
那么将返回整个列表。操作“xs drop n”返回列表xs除了前n个元素之外的所有元素。如果n大于等于xs.length，那么就返回空列表。    
　　splitAt操作将列表从指定的下标位置切开，返回这两个列表组成的对偶。它的定义来自如下这个等式：    
```
xs splitAt n    等于    (xs take n, xs drop n)
```    
　　不过，splitAt会避免遍历xs列表两次。    

### 元素选择：apply和indices    
　　apply方法支持从任意位置选取元素。不过相对于数组而言，对列表的这项操作并不是那么常用。跟其他类型一样，当对象出现在方法调用中函数出现的位
置时，编译器会帮助插入apply。所以`list(1)`和`list apply 1`是等效的。    
　　对列表而言，从任意位置选取元素的操作之所以不那么常用，是因为xs(n)的耗时跟下标n成正比。事实上，apply是通过drop和head定义的：    
``` 
xs apply n    等于    (xs drop n).head
```    
　　indices方法返回包含了指定列表的所有有效下标的列表。    

### 扁平化列表：flatten    
　　flatten方法接收一个列表的列表并将它扁平化，返回单个列表。    

### 将列表zip起来：zip和unzip    
　　zip操作接收两个列表，返回一个由对偶组成的列表。如果两个列表长度不同，那么任何没有配对上的元素将会被丢弃。一个有用的特例是将列表和它的下
标zip起来。最高效的做法是用`zipWithIndex`方法，这个方法会将列表中的每个元素和它出现自爱列表中的位置组合成对偶。    
　　任何元组的列表也可以通过unzip方法转换回由列表组成的元组。    

### 显示列表：toString和mkString    
　　toString操作返回列表的标准字符串表现形式。如果需要不同的表现形式，可以用mkString方法。`xs mkString (pre, sep, post)`涉及四个操作
元：要显示的列表xs、出现在最前面的前缀字符串pre、在元素间分隔字符串sep，以及出现在最后面的后缀字符串post。这个操作的结果是如下的字符串：    
``` 
    pre + xs(0) + sep + ... + sep + xs(xs.length -1) + post
```    
　　mkString有两个重载的变种，让我们不必填写部分或全部入参。第一个变种是只接收一个分隔字符串：`xs mkString sep`，等效于`xs mkString 
("", sep, "")`;第二个变种可以什么入参都不填：`xs mkString`等效于`xs mkString ""`。    
　　mkString方法还有别的变种，比如addString，这个方法将构建出来的字符串追加到一个StringBuilder对象（scala.StringBuilder），而不是作
为结果返回：    
```scala
val buf = new StringBuilder
"abcde" addString (buf, "(", ";", ")")

```    
　　mkString和addString这两个方法继承自List的超特质Traversable，因此它们也可以用在所有其他集合类型上。    

### 转换列表：iterator、toArray、copyToArray    
　　为了在扁平的数组世界和递归的列表世界之间做数据转换，可以使用List类的toArray和Array类的toList方法。    
　　还有一个copyToArray方法可以将列表中的元素依次复制到目标数组的指定位置。如下操作：    
```
xs copyToArray (arr, start) 
```    
　　列表xs的所有元素复制至数组arr，从下表start开始（要确保目标数组足够容纳整个列表）。    

### 例子：归并排序    
　　前面的插入排序写起来很简洁，不过效率并不是很高。它的平均复杂度跟输入列表长度的平方成正比。更高效的算法是*归并排序（merge sort）*。    
　　归并排序的机制如下：首先，如`果列表有零个或一个元素，那么它已然是排好序的，因此列表可以被直接返回。更长一些的列表会被切分成两个子列表，每
个子列表各含约一般原列表的元素。每个子列表被递归地调用同一个函数来排序，然后两个排好序的子列表会通过一次归并操作合在一起。    
　　要实现一个通用的归并排序实现 ，要允许被排序列表的元素类型和用来比较元素大小的函数是灵活可变的。通过参数将这两项作为参数传入，就得到了灵活
的函数。最终实现如下所示：    
```scala
def msort[T](less: (T, T) => Boolean)(xs: List[T]): List[T] = {
  def merge(xs: List[T], ys: List[T]): List[T] =
    (xs, ys) match {
      case (_, Nil) => xs
      case (Nil, _) => ys 
      case (x :: xs1, y :: ys1) =>
        if (less(x, y)) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
  val n = xs.length / 2
  if (n == 0) xs
  else {
    val (ys, zs) = xs splitAt n 
    merge(msort(less)(ys), msort(less)(zs))
  }
}
```    
　　msort的复杂度为（nlog(n)），其中n为输入列表的长度。要搞清楚为么，注意将列表切分为两个子列表，以及将两个排好序的列表归并到一起，这两种
操作消耗的时间都跟列表的长度成正比。每次对msort的递归调用都会对输入的元素数量减半，因此差不多需要log(n)次连续的递归调用直到到达长度为1的列
表这个基本case。不过，对更长的列表而言，每次调用都会进一步生成两次调用。所有这些加在一起，在log(n)层的调用当中，原始列表的每个元素都会参与
一次切分操作和一次归并操作。    
　　这样一来，每个调用级别的总成本也是跟n成正比。由于有log(n)层调用，得到的总成本为n log(n)。这个成本跟列表中预算的初始分布无关，因此最差
的成本跟平均成本相同。归并排序的这个性质让它成为很有吸引力的算法。    
　　同时msort也是柯里化概念的经典案例。柯里化让我们很容易将函数定制为一种采用特定比较函数的特例。    

***    
## Higher-Order-Methods-On-Class-List    
　　许多对列表的操作都有相似的结构，有一些模式反复出现。例如：以某种方式对列表中的每个元素做转换，验证列表中所有元素是否都满足某种性质，从列
表元素中提取满足某个指定条件的元素，或用某种操作符来组合列表中的元素。在Java中，这些模式通常要通过固定写法的for或while循环来组装。而Scala
允许我们使用高阶操作符来更精简、更直接地表达，这些高阶操作是通过List类的方法实现的。    

### 对列表做映射：map、flatMap和foreach    
　　`xs map f`这个操作将类型将类型为`List[T]`的列表xs和类型为`T => U`的函数f作为操作元。它返回一个通过应用f到xs的每个元素后得到的列表。
例如：    
```scala
val addedList = List(1, 2, 3) map (_ + 1)
val words = List("the", "quick", "brown", "fox")
words map (_.length)
words map (_.toCharArray.reverse.mkString)
```    
　　flatMap操作符跟map类似，不过它要求右侧的操作元是一个返回元素列表的函数。它将这个函数应用到每个元素，然后将所有结果拼接起来返回。下面例
子展示了map和flatMap的区别：    
```scala
val words = List("the", "quick", "brown", "fox")
words map (_.toList) //List[List[Char]] = List(List(t, h, e), List(q, u, i, c, k), List(b, r, o, w, n), List(f, o, x))
words flatMap (_.toList) //List[Char] = List(t, h, e, q, u, i, c, k, b, r, o, w, n, f, o, x)
```    
　　可以看到，map返回的是列表的列表，而flatMap返回的是所有元素拼接起来的单个列表。下面这个表达式也体现了map和flatMap的区别和联系，这个表达
式构建的是一个1<=j<i<5的所有对偶(i,j)：    
```scala
List.range(1, 5).flatMap(i => List.range(1, i).map(j => (j, i)))
```    
　　`List.range`是一个用来创建某个区间内所有整数的列表的工具方法。在本例中，用到了两次：一次是生成从1（含）到5（不含）的整数列表，另一次是
生成从1到i的整数列表，其中i是来自第一个列表的每一个元素。表达式中的map生成的是一个由元组(i, j)组成的列表，其中j<i。外围的flatMap对1到5之
间的每一个i生成一个列表，并将结果拼接起来。也可以用for表达式构建同样的列表：    
```scala
for (i <- List.range(1, 5); j <- List.range(1, i)) yield (i, j)
```    
　　第三个映射类的操作是foreach。不同于map和flatMap，foreach要求右操作元是一个过程（结果类型为Unit的函数）。它只是简单地将过程应用到列表
中的每个元素。整个操作本身的结果类型也是Unit，并没有列表类型的结果被组装出来。    

### 过滤列表：filter、partition、find、takeWhile、dropWhile和span    
　　`xs filter p`这个操作的两个操作元分别是类型为`List[T]`的xs和类型为`T => Boolean`的前提条件函数p。这个操作将交出xs中所有`p(x)`为
true的元素，例如：    
```scala
List.range(1, 6) filter (_ % 2 == 0)//List(2, 4)
```    
　　partition方法跟filter很像不过返回的是一对列表。其中一个包含所有前提条件为true的元素，另一个包含所有的前提条件为false的元素。它满足如
下等式：` xs partition p    等于    (xs filter p, xs filter (!p(_)))`，参考下面的例子：    
```scala
List(1, 2, 3, 4, 5) partition (_ % 2 == 0)//(List(2, ,4), List(1, 3, 5))
```    
　　find方法跟filter也很像，不过它返回满足给定前提条件的第一个元素，而不是所有元素。`xs find p`这个操作接收列表xs和前提条件函数p两个操作
元，返回可以可选值。如果xs中存在一个元素x满足p(x)为true，那么就返回`Some(x)`。而如果对于所有元素而言p都为false，那么则返回None。    
　　takeWhile和dropWhile操作符也将一个前提条件作为右操作元。`xs takWhile p`操作返回列表xs中连续满足p的最长前缀。同理，`xs dropWhile p`
操作将去除列表xs中连续满足p的最长前缀。来看一些例子：    
```scala
List(1, 2, 3, -4, 5) takeWhile (_ > 0)//List(1, 2, 3)
val words = List("the", "quick", "brown", "fox")
words dropWhile (_ startsWith "t")//List("quick", "brown", "fox")
```    
　　span方法将takeWhile和dropWhile两个操作合二为一，就像splitAt将take和drop合二为一一样。它返回一堆列表，满足：`xs span p    等于 
(xs takeWhile p, xs dropWhile p)`，跟splitAt一样，span同样不会重复遍历xs。    

### 折叠列表：/:和\:    
　　对列表的另一种常见操作是用某种操作符合并元素。例如：`sum(List(a,b,c))   等于   0+a+b+c`，这是一个折叠操作。同理，`product
(List(a,b,c))   等于   1*a*b*c`：    
```scala
def sum(xs: List[Int]): Int = (0 /: xs)(_ + _)

def product(xs: List[Int]): Int = (1 /: xs)(_ * _)
```    
　　*左折叠（fold left）* 操作“(z /: xs)(op)”涉及三个对象：起始值z、列表xs和二元操作op。折叠的结果是以z为前缀，对列表的元素依次连续应用
op。例如：`(z /: List(a,b,c))(op)   等于   op(op(op(z, a), b), c)`。    
　　还有个例子可以说明`\:`的用用处。为了把列表中的字符串表示的单词拼接起来，在当中和最前面加上空格，可以：`("" /: words)(_ + " " + _)`。
这里开始会多出一个空格，要去除这个空格，可以：`(words.head /: words.tail)(_ + " " + _)`。    
　　`/:`操作符产生一颗往左靠的操作树，同理`:\`这个操作产生一棵往右靠的树，例如：`(List(a,b,c) :\ z)(op)  等于  op(a,op(b,op(c,z)))`。    
　　`:\`操作读做*右折叠（fold right）*。它涉及跟左折叠一样的三个操作元，不过前两个出现的顺序是颠倒的：第一个操作元是要折叠的列表，而第二个
操作元是起始值。    
　　对结合性的操作而言，左折叠和右折叠是等效的，不过可能存在执行效率上的差异。可以设想一下flatten方法对应的操作，这个操作是将一个列表的列表
中所有元素拼起来。可以用左折叠也可以用右折叠来完成：    
```scala
def flattenLeft[T](xss: List[List[T]]) = (List[T]() /: xss) (_ ::: _)

def flattenRight[T](xss: List[List[T]]) = (xss :\ List[T]()) (_ ::: _)
```    
　　由于列表拼接`xs ::: ys`的执行时间跟首个入参xs的长度成正比，用右折叠的flattenRight比用左折叠的flattenLeft更高效。左折叠在这里的问题
是`flattenLeft(xss)`需要复制首个元素列表`xss.head` n-1 次，其中n为列表xss的长度。    
　　注意上述的两个flatten版本都需要对表示折叠的起始值的空列表做类型注解。这是由于Scala类型推断程序的一个局限，不能自动推断出正确的列表类型。
最后，虽然`/:`和`:\`操作符的一个优势是斜杠的方向形象地表示出往左或往右靠的树形结构，同时冒号的结合性也将起始值放在了表达式中跟树中一样的位
置，可能还是有人会觉得不直观。只要你想，也可以用foldLeft和foldRight这样的方法名，这两个也是定义在List类中的方法。    

### 例子：用fold反转列表    
　　之前做了一个reverse方法的实现，名为rev，其运行时间是待反转列表长度的平方级。现在看一个reverse的不同实现，运行开销是线性的。原理基于下
面的机制来做左折叠：`def reverseLeft[T](xs: List[T]) = (startValue /: xs) (operation)`。    
　　剩下需要补全的就是startValue（起始值）和operation（操作）的部分了。事实上，可以用简单的例子推导出来。为了推导出startValue的正确取值，
可以用最简单的列表List()开始：    
``` 
List()
    等同于 （根据reverseLeft的性质）
reverseLeft(List())
    等同于 （根据reverseLeft的模板）
(起始值 /: List())(操作)
    等同于 （根据/:的定义）
起始值
```    
　　因此，startValue必须是List()。要推导出第二个操作元，可以拿仅次于List()的最小列表为样例。已经知道了startValue是List()，可以做如下演
算：    
``` 
List(x)
    等同于 （根据reverseLeft的性质）
reverseLeft(List(x))
    等同于 （根据reverseLeft的模板，其中，起始值为List()）
(List() /: List(x))(操作)
    等同于 （根据/:的定义）
操作(List(), x)
```    
　　因此，operation(List(), x)等于List(x)，而List(x)也可以写作`x :: List()`。这样我们发现基于`::`操作符把两个操作元反转一下得到operation
（这个操作有时被称作“scons”，即把::的“cons”反过来念）。于是得到了如下reverseLeft的实现：    
```scala
def reverseLeft[T](xs: List[T]) = 
    (List[T]() /: xs) {(ys, y) => y :: ys}
```    
　　同样地，为了让类型推断程序正常工作，这里的类型注解`List[T]()`是必须的。    

### 列表排序：sortWith    
　　`xs sortWith before`这个操作对列表xs中的元素进行排序，其中“xs”是列表，而“before”是一个用来比较两个元素的函数。表达式`x before y`
对于在预期的排序中x应出现在y之前的情况应返回true。例如：    
```scala
List(1, -3, 4, 2, 6) sortWith (_ < _)
```    
　　注意，sortWith执行的跟前面的msort算法类似的归并排序。不过sortWith是List类的方法，而msort定义在列表之外。    

***
## Methods-Of-The-List-Object    
　　除了List类上的方法（具体列表对象调用），还有一些方法是定义在全局可访问对象scala.List上的，这是List类的伴生对象。某些操作是用于创建列
表的工厂方法，另一些是对特定形状的列表进行操作。    

### 从元素创建列表：List.apply    
　　我们已经看到过不止一次诸如`List(1, 2, 3)`这样的列表字面量。这样的语法并没有什么特别之处。List(1,2,3)这样的字面量只不过是简单地将对系
List应用到元素1、2、3而已。也就是说，它跟`List.apply(1, 2, 3)`是等效的。    

### 创建数值区间：List.range    
　　List的range方法创建的是一个包含一个区间的数值的列表。这个方法最简单的形式是`List.range(from, until)`，创建一个包含了从from开始递增
到 until-1的数的列表。所以最终 util并不是区间的一部分。    
　　range方法还有另一个版本，接收step作为参数。这个操作交出的列表元素是从from开始，间隔为step的值。step可以是正值也可以是负值。    

### 创建相同元素的列表：List.fill    
　　fill方法创建包含零个或多个同一个元素拷贝的列表。它接收两个参数：要创建的列表长度和需要重复的元素。两个参数各自以不同的参数列表给出：    
```scala
val list1 = List.fill(5)('a')
val list2 = List.fill(3)("hello")
```    
　　如果给fill的参数多于1个，那么它就会创建多维的列表。也就是说，它将创建出列表的列表、列表的列表的列表，等等。多出来的参数要放在第一个参数
列表中：`List.fill(2, 3)('b')//List(List('b','b','b'), List('b','b','b'))`    

### 表格化一个函数：List.tabulate    
　　tabulate方法创建的是一个根据给定的函数计算的元素的列表。其入参和`List.fill`的一样：第一个参数列表给出要创建列表的维度，而第二个参数列
表描述列表的元素。唯一的区别是，元素值不再是固定的，而是从函数计算得来的：    
```scala
val squares = List.tabulate(5)(n => n * n)//List(0,1,4,9,16)
val multiplication = List.tabulate(5,5)(_ * _)//List(List(0,0,0,0,0), List(0,1,2,3,4), List(0,2,4,6,8),List(0,3,6,9,12), List(0,4,8,12,16))
```    

### 拼接多个列表：List.concat    
　　concat方法将多个列表拼接在一起。要拼接的列表通过concat的直接入参给出：`List.concat(List('a','b'), List('c'))`。    

***    
## Processing-Multiple-Lists    
　　元组的zipped方法将若干通用的操作一般化了，它们不再只是针对单个列表而是能同时处理多个列表。其中一个通用的操作是map。对两个zip在一起的列
表调用map的效果是对元素一组一组地做映射，而不是单个元素。每个列表的第一个元素是一对，第二个也是，以此类推，列表有多长，就有多少对。参考下面
的例子：    
```scala
(List(10,20), List(3,4,5)).zipped.map(_ * _)//List(30,80)
```    
　　注意第二个列表的第三个元素被丢弃了。zipped方法只会把所有列表中都有值的元素zip在一起，多出来的元素会被丢弃。    
　　同理，exists和forall也有zip起来的版本。它们跟单列表的版本做的事情相同，只不过它们操作的是多个列表而不是一个：    
```scala
(List("abc","de"), List(3,2)).zipped.forall(_.length == _)//true
(List("abc","de"), List(3,2)).zipped.exists(_.length != _)//false
```    

***    
