# 基础类型和操作    
- 一些基础类型...................................................[1](#Some-Basic-Types)
- 字面量...................................................[2](#Literals)
- 字符串插值...................................................[3](#String-Interpolation)
- 操作符即方法...................................................[4](#Operators-Are-Method)
- 算术操作...................................................[5](#Arithmetic-Operations)
- 关系和逻辑操作...................................................[6](#Relational-And-Logical-Operations)
- 位运算操作...................................................[7](#Bitwise-Operations)
- 对象相等性...................................................[8](#Object-Equality)
- 操作符优先级和结合性...................................................[9](#Operator-Precedence-Associativity)
- 富包装类...................................................[10](#Rich-Wrappers)    

***
## Some-Basic-Types    
　　下表列出了Scala的一些基础类型和这些类型的实例允许的取值范围。    

 |  基础类型  | 取值区间  
 | --- | ---      
 | Byte | 8位带符号二进制补码整数（-2^7到2^7-1，闭区间）    
 | Short | 16位带符号二进制补码整数（-2^15到2^15-1，闭区间）    
 | Int | 32位带符号二进制补码整数（-2^31到2^31-1，闭区间）    
 | Long | 64位带符号二进制补码整数（-2^63到2^63-1，闭区间）    
 | Char | 16位无符号Unicode字符（0到2^16-1,闭区间）    
 | String | Char的序列    
 | Float | 32位IEEE 754单精度浮点数    
 | Double | 64位IEEE 754双精度浮点数    
 | Boolean | true或false    
 
　　Byte、Short、Int、Long和Char类型统称为*整数类型*（integral type）。整数类型加上Float和Double称作*数值类型*（numeric types）。
除了位于java.lang的String，其他的所有类型都是scala包的成员。例如，Int的完整名称为scala.Int。不过，由于scala包和java.lang包的所有成员
在Scala源文件中都已自动引入，可以在任何地方使用简单名称。 上表中Scala的基础类型和Java中对应的类型取值区间完全相同，这使得Scala编译器可以
产出的字节码中将Scala的*值类型*（value types），比如Int或Double的实例转换成Java的基本类型。    
 
***
## Literals    
　　上文表中列出的所有基础类型都可以用*字面量（literals）* 来书写。字面两是在代码中直接写入常量值的一种方式。需要注意一下 Scala原生字符串
和符号的字面量以及字符串的插值。还有就是Scala不支持八进制字面量和以0开头的整数字面量，如031,将无法通过编译。    

### 整数字面量    
　　用于Int、Long、Short和Byte的整数字面量有两种形式：十进制的和十六进制的。整数字面量的不同开头表示了不同的进制。如果是以0x或0X开头，意味
着这是十六进制的数，可以包含0到9以及大写或小写的A到F表示的数字。例如：    
```shell script
scala> val hex = 0x5
hex: Int = 5
scala> val hex2 = 0x00FF
hex2: Int = 255
scala> val magic = 0xcafebabe
magic: Int = -889275714
```    
　　注意，Scala的shell总是以十进制打印整数值，不论是用哪种形式来初始化。因此解释其把字面量0xFF初始化的变量hex2显示为十进制的255。如果字面
量以非0的数字开头，且除此之外没有其他的修饰，那么它就是十进制的。例如：    
```shell script
scala> val dec1 = 31
dec1: Int = 31
```    
　　如果整数字面量以l或L结尾，那么它就是Long类型。    
　　如果一个Int型的字面量被赋值给一个类型为Short或Byte的变量，该字面量会被当作Short或Byte类型，只要这个字面量的值在对应类型的合法取值区间
即可。    

### 浮点数字面量    
　　浮点数字面量由十进制的数字、可选的小数点、以及后续一个可选的E或e打头的指数组成。一些浮点数字的字面量如下：    
```scala
val big = 1.2345//1.2345 Double
val bigger = 1.2345e1 //1.2345 * 10^1  12.345 Double
val biggerStill = 123E45 //1.23E47 Double
```    
　　一些Float字面量如下：    
```scala
val little = 1.2345F
val littleBigger = 3e5f//300000.0 Float
```    

### 字符字面量    
　　和Java一样，如`val a = '\u0041'`    

### 字符串字面量    
　　Scala支持一种特殊的语法来表示*原生的字符串*。可以用三个双引号来使用。原生字符串内部可以包含任何字符，包括换行、单引号等特殊字符。当然，
连续三个双引号除外。可以看下面例子：    
```scala
println("""Welcome to Ultamix 3000.
           Type "HELP" for help. """)
//管道符，stripMargin调整
println("""|Welcome to Ultamix 3000.
           |Type "HELP" for help. """.stripMargin)
```    
### 符号字面量    
　　符号字面量的写法是'ident，其中ident可以是任何由字母和数字组成的标识符。这样的字面量会被映射成scala.Symbol这个预定义类的实例。确切的
说，字面量'cymbal会被编译器展开成一个工厂方法的调用：Symbol("cymbal")。符号字面量通常用在那些在动态类型语言中用来当作标识符的场合。例如，
定义一个更新数据库记录的方法：    
```scala
def updateRecordByName(r: Symbol, value: Any)
```    
　　这个方法以参数的形式接收一个符号和一个值，分别表示记录中要更新的字段和值。在动态语言中，可以传入一个未声明的字段标识来调用这个方法，但在
Scala中不行。不过，可以传入一个符号字面量，和动态语言同样精简：`updateRecordByName('favoriteAlbum, "Ok Computer")`。
　　对于符号，能做的不过，除了获取它的名字：`val name = r.name`。另一个需要注意的是，符号会被内部化（所谓内部化，可以参考Java对Long对象
的处理，最常用的-127～128L会被内部化，即重用对象而不是新建）。如果同样的符号字面量出现两次，这两次引用都会指向同一个Symbol对象。        

### 布尔字面量    
　　true  &&  false
***


