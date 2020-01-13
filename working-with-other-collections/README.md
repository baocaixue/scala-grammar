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
## Sets-And-Maps    
　　Scala的集合类库同时提供了可变和不可变两个版本的集和类库。当写下“Set”或“Map”时，默认得到的是一个不可变的对象。如果想要的是可变的版本，
需要显式地做一次引入。Scala让我们更容易访问到不可变的版本，这是鼓励我们尽量使用不可变的集合。这样的访问便利是通过`Predef`对象完成的，这个
对象的内容在每个Scala源文件中都会隐式地引入。下面给出了相关定义：    
```scala
object Predef {
  type Map[A, +B] = collection.immutable.Map[A, B]
  type Set[A] = collection.immutable.Set[A]
  val Map = collection.immutable.Map
  val Set = collection.immutable.Set
  // ...
}
```    
　　`Predef`利用“type”关键字定义了`Set`和`Map`这两个别名，分别对应不可变的集和不可变的映射的完整名称。名为`Set`和`Map`的val被初始化成
指向不可变的`Set`和`Map`的单例对象。因此`Map`等同于`Predef.Map`，而`Predef.map`又等同于`scala.collection.immutable.Map`。这一点
对于`Map`类型和`Map`对象都成立。    
　　如果想在同一个源文件中同时使用可变的和不可变的集或映射，一种方式是引入包含可变版本的包：`import scala.collection.mutable`。可以继续
用`Set`来表示不可变集，不过现在还可以用`mutable.Set`来表示可变集：`val mutaSet = mutable.Set(1, 2, 3)`。    

### 使用集    
　　集的关键特征是它们会确保同一时刻，以==为标准，集里的每个对象都最多出现一次。作为示例，将用一个集来统计某个字符串中不同单词的个数。如果将
空格和标点符号作为分隔符给出，String的split方法可以帮助我们将字符串且分成单词。“\[ ! , .\]”这样的正则表达式表示给定的字符串需要在有一个
或多个空格或标点符号的地方切开。    
```scala
val text = "See Spot run. Run, Spot. Run!"
val wordsArray = text.split("[ ! , .]+")
val words = scala.collection.mutable.Set.empty[String]
for (word <- wordsArray) words += word.toLowerCase
words//Set(see, run, spot)
```    
　　可变集和不可变集常用的方法如下：    

| 操作 | 这个操作做什么 
| --- | ---    
| val nums = Set(1, 2, 3) | 创建一个不可变集
| nums + 5 | 添加一个元素
| nums - 3 | 移除一个元素
| nums ++ List(5, 6) | 添加多个元素
| nums -- List(1, 2) | 移除多个元素
| nums & Set(1, 3, 5, 7) | 获取两个集的交集
| nums.size | 返回集的大小
| nums.contains(3) | 检查是否包含
| ～～～～～～～～ | ~~~~~~~~~~~~
| import scala.collection.mutable | 让可变集合易于访问
| val words = mutable.Set.empty\[String\] | 创建一个空的可变集
| words += "the" | 添加一个元素
| words -= "the" | 移除一个元素
| words ++= List("do", "re", "mi") | 添加多个元素
| words --= List("do", "re") | 移除多个元素
| words.clear | 移除所有元素    

### 使用映射    
　　映射让我们可以对某个集的每个元素都关联一个值。使用映射看上去跟使用数组很像，只不过我们不再是用从0开始的整数下标去索引，而是可以用任何键来
索引它。如果引入了mutable这个包名，就可以像这样创建一个空的可变映射：`val map = mutable.Map.empty[String, Int]`。    
　　注意在创建映射时，必须给出两个类型。第一个类型针对映射的*键（key）*，而第二个类型是针对映射的*值（value）*。在本例中，键是字符串，而值
是整数。在映射中设置条目看上去跟在数组中设置条目类似：    
```scala
import scala.collection.mutable.Map
val map = Map.empty[String, Int]
map("hello") = 1
map("there") = 2
//读取
map("hello")//1
```    
　　下面是一个统计每个单词在字符串中出现次数的方法：    
```scala
import scala.collection.mutable
def countWords(text: String) = {
  val counts = mutable.Map.empty[String, Int]
  for (rawWord <- text.split("[ ! . ,]+")) {
    val word = rawWord.toLowerCase
    val oldCount = if (counts.contains(word)) counts(word) else 0
    counts += (word -> (oldCount + 1))
  }
  counts
}
```    
　　可变映射和不可变映射最常用的方法如下：    

| 操作 | 这个操作做什么
| --- | ---
| val nums = Map("i" -> 1, "ii" -> 2) | 创建一个不可变映射
| nums + ("vi" -> 6) | 添加一个条目
| nums - "ii" | 移除一个条目
| nums ++ List("iii" -> 3, "v" -> 5) | 添加多个条目
| nums -- List("i", "ii") | 移除多个条目
| nums.size | 返回映射大小
| nums.contains("ii") | 检查是否包含
| nums("ii") | 获取指定键的值
| nums.keys | 返回所有的键
| nums.keySet | 以集的形式返回所有的键
| nums.values | 返回所有的值
| nums.isEmpty | 表示映射是否为空
| ~~~~~~~~~~~~~~~ | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
| import scala.collection.mutable | 让可变集合易于访问
| val words = mutable.Map.empty\[String, Int\] | 创建一个空的可变映射
| words += ("one" -> 1) | 添加一个从"one"到1的映射条目
| words -= "one" | 移除一个映射条目
| words ++= List("one" -> 1, "two" -> 2, "three" -> 3) | 添加多个条目
| words --= List("one", "two") | 移除多个条目    

### 默认的集和映射    