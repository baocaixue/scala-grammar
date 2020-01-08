# 使用其他集合类
- 序列...................................................[1](#Sequence)
- 集和映射...................................................[2](#Sets-And-Maps)
- 在可变和不可变集合类间做选择...................................................[3](#Mutable-Or-Immutable)
- 初始化集合...................................................[4](#Initializing-Collections)
- 元组...................................................[5](#Tuples)    

***    
## Sequence    
　　序列类型可以用来处理依次排列分组的数据。由于元素是有次序的，可以向序列获取第1个元素、第2个、第3个...下面介绍那些重要的序列类型。    

### 列表    
　　List类是一种重要的序列类型，它是一种不可变列表。列表支持在头部快速添加和移除条目，不过并不提供快速的下表访问功能，因为实现这个功能需要线
性地遍历列表。    
　　这样的特性组合听上去可能有些怪，但其实对于很多算法而言都非常合适。快速的头部添加和移除意味着模式匹配很顺畅。而列表的不可变性质帮助开发正
确、高效的算法，因为不需要（防止意外）复制列表。    

### 数组    
　　数组允许我们保存一个序列的元素，并使用从零开始的下标高效地访问（获取或更新）指定位置的元素值。看下面的例子：    
```scala
//创建一个已知大小但不知道元素值的数组
val fiveInts = new Array[Int](5)
//初始化一个已知元素值的数组
val fiveToOne = Array(5, 4, 3, 2, 1)
```    
　　之前提到，在Scala中以下标访问数组的方式是把下标放在圆括号而不是像Java那样的方括号里。    

### 列表缓冲（list buffer）    
　　List类提供对列表头部的快速访问，对尾部发给你问则没有那么高效。因此当我们需要往列表尾部追加元素来构建列表时，通常要考虑反过来往头部追加
元素，追加完成后，再调用reverse来获取想要的序列。    
　　另一种避免reverse操作的可选方案是使用`ListBuffer`。ListBuffer是一个可变对象（包含在scala.collection.mutable包中），帮助我们在需
要追加元素来构建列表时可以更高效。ListBuffer提供了常量时间的往后追加和往前追加的操作。我们可以用+=操作符来往后追加元素，用+=:来往前追加元
素。完成构建之后，可以调用ListBuffer的toList来获取最终的List。参考下面的例子：    
```scala
import scala.collection.mutable.ListBuffer
val buf = new ListBuffer[Int]
buf += 1
buf += 2
0 +=: buf
buf.toList //List(0, 1, 2)
```    
　　使用ListBuffer而不是List的另一个原因是防止可能出现的栈溢出。如果我们可以通过往前追加来构建出预期的列表，但需要的递归算法并不是尾递归，
可以用for表达式或while循环加上ListBuffer实现。    

### 数组缓冲    
　　ArrayBuffer跟数组很像，除了可以额外地从序列头部或尾部添加或移除元素。所有的Array操作在ArrayBuffer都可用，不过由于实现的包装，会稍慢
一些。新的添加和移除操作平均而言是常量时间的，不过偶尔会需要线性的时间，这是因为其实现需要不时地分配新的数组来保存缓冲的内容。    
　　要使用ArrayBuffer，必须首先从可变集合的包引入它。在创建ArrayBuffer时，必须给出类型参数，不过并不需要指定长度。ArrayBuffer会在需要时
自动调整分配空间：    
```scala
import scala.collection.mutable.ArrayBuffer
val buf = new ArrayBuffer[Int]()
//+=方法追加
buf += 12
buf += 15

//常规数组操作都可用
buf.length //2
buf(0) //12
```    

### 字符串（通过StringOps）    
　　需要了解的另一个序列是`StringOps`，它实现了很多序列方法。由于Predef有一个从String到StringOps的隐式转换，可以将任何字符串当作序列来
处理。参考下面例子：    
```scala
def hasUpperCase(s: String) = s.exists(_.isUpper)
hasUpperCase("Isaac Bao")//true
hasUpperCase("isaac bao")//false
```    
　　在本例中，hasUpperCase方法体里，对名为s的字符串调用了exists方法。由于String类本身并没有声明任何名为“exists”的方法，Scala编译器会隐
式地将s转换成StringOps，StringOps有这样一个方法。exists方法将字符串当作字符的序列，当序列中存在大写字符时，这个方法返回true。    

***    