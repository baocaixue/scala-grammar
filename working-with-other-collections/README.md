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
　　对于大部分使用场景，由`Set()`、`scala.collection.mutable.Map()`等工厂方法提供的可变和不可变的集和映射的实现通常都够用了。这些工厂
方法提供的实现使用快速的查找算法，通常用到哈希表，因此可以很快判断出某个对象是否在集合中。    
　　举例来说，`scala.collection.mutable.Set()`这个工厂方法返回一个`scala.collection.mutable.HashSet`，在内部使用了哈希表。同理，
`scala.collection.mutable.Map()`这个工厂方法返回的是一个`scala.collection.mutable.HashMap`。    
　　对于不可变集和映射而言，情况要稍微复杂一些。举例来说，`scala.collection.immutable.Set()`工厂方法返回的类取决于传入了多少元素（详细
明细参见下表）。对于少于五个元素的集，有专门的特定大小的类与之对应，以此来达到最好的性能。一旦要求一个大于等于五个元素的集，这个工厂方法将返
回一个使用哈希字典树（hash trie）的实现。    

| 元素个数 | 实现    
| --- | ---
| 0 | scala.collection.immutable.EmptySet
| 1 | scala.collection.immutable.Set1
| 2 | scala.collection.immutable.Set2
| 3 | scala.collection.immutable.Set3
| 4 | scala.collection.immutable.Set4
| 5或更多 | scala.collection.immutable.HashSet     
　　同理，`scala.collection.immutable.Map()`这个工厂方法会根据我们传给它多少键值来据定返回什么类的实现（详细参见下表）。跟集类似，对于
少于五个元素的不可变映射，都会有一个特定的固定大小的映射与之对应，以此来达到最佳性能。而一旦映射中的键值对个数达到或超过五个，则会使用不可变
的HashMap。    

| 元素个数 | 实现     
| --- | ---
| 0 | scala.collection.immutable.EmptyMap
| 1 | scala.collection.immutable.Map1
| 2 | scala.collection.immutable.Map2
| 3 | scala.collection.immutable.Map3
| 4 | scala.collection.immutable.Map4
| 5 | scala.collection.immutable.HashMap    
　　上面两个表给出的默认不可变实现类能够带给我们最佳的性能。举例来说，如果添加一个元素到EmptySet，我们将得到一个Set1。如果添加一个元素到
Set1,将得到一个Set2。如果从Set2移除一个元素，又会得到一个Set1。    

### 排好序的集和映射    
　　有时我们可能需要一个迭代器按照特定顺序返回元素的集或映射。对此，Scala集合类提供了*SortedSet*和*SortedMap*特质。这些特质被**TreeSet**
和**TreeMap**类实现，这些实现用红黑树来保持元素（对TreeSet而言）或键（对TreeMap而言）的顺序。具体顺序由*Ordered*特质决定，集的元素类型
或映射的键的类型都必须混入或能够被隐式转换成Ordered。这两个类只有不可变的版本。    
　　TreeSet的例子：    
```scala
import scala.collection.immutable.TreeSet

val ts = TreeSet(9, 3, 1, 8, 0, 2, 7, 4, 6, 5)//0 1 2 3 4 5 6 7 8 9
val cs = TreeSet('f', 'u', 'n')//f n u
```    
　　TreeMap的例子：    
```scala
import scala.collection.immutable.TreeMap

var tm = TreeMap(3 -> 'x', 1 -> 'x', 4 -> 'x')//1->x 3->x 4->x
tm += (2 -> 'x')
tm //1->x 2->x 3->x 4->x
```    

## Mutable-Or-Immutable    
　　对于某些问题，可变集合更好用，而对于一些问题，不可变集合更适用。如果拿不定主意，那么最好从一个不可变集合开始，事后如果有需要再做调整。因
为跟可变集合比起来，不可变集合更容易推敲。    
　　同样地，有时候我们也可以反过来看。如果发现某些使用了可变集合的代码开始变得复杂和难以理解，也可以考虑是不是换成不可变集合能帮上忙。尤其当
我们发现经常需要担心在正确的地方对可变集合做拷贝，或者花大量的时间思考谁“拥有”或“包含”某个可变集合时，考虑将某些集合换成不可变的版本。    
　　除了可能更容易推敲之外，在元素不多的情况下，不可变集合通常还可以比可变集合存储得更紧凑。举例来说，一个空的可变映射，按照默认的HashMap实
现，会占掉80个字节，每增加一个条目需要额外16个字节。一个空的不可变Map只是单个对象，可以被所有的引用共享，所以引用它本质上只需要花费一个指针
字段。    
　　不仅如此，Scala集合类库目前的不可变映射和不可变集单个对象（Set1到Set4、Map1到Map4）最多可以存4个条目，根据条目数的不同，通常占据16到
40个字节。因此对于小型的映射和集而言，不可变的版本比可变的版本要紧凑得多。由于实际使用中很多集合都很小，采用不可变的版本可以节约大量的空间，
带来重要的性能优势。    
　　为了让从不可变集转到可变集（或者反过来）更容易，Scala提供了一些语法糖。尽管不可变集和映射并不真正支持 += 操作，Scala提供了一个变通的解
读：只要看到`a += b`而a并不支持名为+=的方法，Scala会尝试将它解读给`a = a + b`。例如，不可变集并不支持+=操作：    
```scala
val people = Set("Nancy", "Jane")
people += "Bob" // error: value += is not a member of scala.collection.immutable.Set[String]
```    
　　不过，如果将People声明为var而不是val，那么这个集合就能够用 += 操作来“更新”，尽管它是不可变的。首先，一个新的集合被创建出来，然后people
将被重新赋值指向新的集合：    
```scala
var people = Set("Nancy", "Jane")
people += "Bob"
```    
　　在这一系列语句之后，变量people指向了新的集，包含添加的字符串“Bob”。同样的理念适用于任何以=结尾的方法，并不仅仅是+=方法。以下是相同的语
法规则应用于-=操作符的例子，这个操作符将某个元素从集里移除;以及++=操作符将一组元素添加到集里：    
```scala
var people = Set("Nancy", "Jane")
people -= "Jane"
people ++= List("Tom", "Isaac")
```    

## Initializing-Collections    
　　创建和初始化一个集合最常见的方式是将初始元素传入所选集合的伴生对象的工厂方法。只需要将元素放在伴生对象名后的圆括号里，Scala编译器会将它
转换成伴生对象apply方法的调用：    
```scala
List(1, 2, 3)
Set('a', 'b', 'c')

import scala.collection.mutable
mutable.Map("hi" -> 2, "there" -> 5)

Array(1.0, 2.0, 3.0)
```    
　　虽然大部分时候可以让Scala编译器从传入工厂方法的元素来推断出集合类型，但是有的时候我们可能希望在创建集合时指定跟编译器所选的不同的类型。
对于可变集合来说尤其 如此。参考下面的例子：    
```scala
import scala.collection.mutable
val stuff = mutable.Set(42)
stuff += "a"//error: type mismatch; found: String required: Int
```    
　　这里的问题是stuff被编译器推断为类型Int的集合。如果想要的类型是Any，得显式地将元素类型放在方括号里：`val stuff = mutable.Set[Any](42)`。
另一个特殊的情况是当我们用别的集合初始化当前集合的时候。举例来说，假设有一个列表，但希望用TreeSet来包含这个列表的元素。    
```scala
import scala.collection.immutable.TreeSet
val colors = List("blue", "yellow", "red", "green")
//val treeSet = TreeSet(colors)//不能将colors列表传入TreeSet工厂方法

//需要创建一个空的TreeSet[String],然后用TreeSet的++操作将列表元素添加进去
val treeSet = TreeSet[String]() ++ colors
```    

### 转换成数组或列表    
　　如果想用别的集合初始化数组或列表则相对直截了当。要用别的集合初始化新的列表，只需要简单地对集合调用toList：`treeSet.toList`。如果要初
始化数组，就调用toArray：`treeSet.toArray`。    
　　注意虽然原始的colors列表没有排序，对TreeSet调用toList得到的列表中，元素是按字母顺序排序的。当对集合调用toList或toArray时，产生的列
表或数组中元素的顺序跟调用elements获取迭代器产生的元素顺序一致。由于`TreeSet[String]`的迭代器会按照字母顺序产生字符串，这些字符串在对这个
TreeSet调用toList得到的i列表中也会按字母顺序出现。    
　　需要注意的是，转换成列表或数组通常需要将集合的所有元素做拷贝，因此对于大型集合来说可能会比较费时。不过由于某些已经存在的API，我们有时需
要这样做。而且，许多集合本来元素就不多，因拷贝带来的性能开销并不高。    

### 在可变和不可变集及映射间转换    
　　还有可能出现的一种情况是将可变集或映射转换成不可变的版本，或者反过来。要完成这样的转换，可以用前面展示的用列表元素初始化TreeSet的技巧。
首先用empty创建一个新类型的集合，然后用++或++=添加新元素。    
```scala
import  scala.collection.mutable
val treeSet = scala.collection.immutable.TreeSet("blue", "green", "red", "yellow")
val mutaSet = mutable.Set.empty ++= treeSet
val immutaSet = Set.empty ++ mutaSet

//映射
val muta = mutable.Map("i" -> 1, "ii" -> 2)
val immu = Map.empty ++ muta
```    

## Tuples    
　　一个元组将一组固定个数的条目组合在一起，作为整体进行传递。不同于数组或列表，元组可以持有不同类型的对象。这是一个同时持有整数、字符串和控
制台对象的元素：`(1, "hello", Console)`。    
　　元组可以帮助省去定义那些简单的主要承载数据的类的麻烦。尽管定义类本身已经足够简单，这的确也是工作量，而且有时候除了定义一下也没有别的意义。
有了元组，我们不再需要给类选一个名称、选一个作用域、选成员名称等。如果我们的类只是简单地持有一个整数和一个字符串，定义一个名为
AnIntegerAndAString的类并不会让代码变得更清晰。    
　　由于元组可以将不同类型的对象组合起来，它们并不继承自`Traversable`。如果只需要将一个整数和一个字符串放在一起，我们需要的是一个元组，而不
是List或Array。    
　　元组的一个常见的应用场景是从方法返回多个返回值。下面是一个在集合中查找最长单词同时返回下标的方法：    
```scala
def longestWord(words: Array[String]) = {
  var word =words(0)
  var idx = 0
  for (i <- 1 until words.length) {
    if (words(i).length > word.length) {
      word = words(i)
      idx = i
    }
  }
  (word, idx)
}
```    
　　访问元组的元素，可以用`_1`访问地一个元素，用`_2`访问第二个元素...不仅如此，还可以将元组的元素分别赋值给不同的变量，但是要注意下面情况：    
```scala
val tuple = ("hello", 1)
val (word, idx) = tuple 
//word: String = hello
//idx: Int = 1

//如果去掉圆括号
val word1, idx1 = tuple
//word1: (String, Int) = (hello, 1)
//idx1: (String, Int) = (hello, 1)
```    
　　这样的语法对相同的表达式给出了*多重定义（multiple definitions）*。每个变量都通过对等号右侧的表达式求值来初始化。本例中右侧表达式求值
的到元组这个细节并不重要，两个变量都被完整地赋予了元组的值。    
　　需要注意的是，元组用起来太容易以至于可能会过度使用。当我们对数据的要求仅仅是“一个A和一个B”的时候，元组很棒。不过，一旦这个组合有某种具体
的含义，或者我们想给这个组合添加方法的时候，最好还是单独创建一个类。举例来说，不建议用三元组来表示年、月、日的组合，建议用Date类。这样意图更
清晰，对读者更友好，也让编译器和语言有机会帮助发现程序错误。    