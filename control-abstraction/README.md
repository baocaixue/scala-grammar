# 控制抽象    
- 减少代码重复...................................................[1](#Reducing-Code-Duplication)
- 简化调用方代码...................................................[2](#Simplifying-Client-Code)
- 柯里化...................................................[3](#Currying)
- 编写新的控制结构...................................................[4](#Writing-New-Control-Structures)
- 传名参数...................................................[5](#ByName-Parameters)    

***

## Reducing-Code-Duplication  
　　所有的函数都能被分解每次函数调用都一样的公共部分和每次调用不一样的非公共部分。公共部分是函数体，而非公共部分必须通过实参传入。当把函数值
当作入参的时候，这段算法的非公共部分本身又是另一个算法！每当这样的函数被调用，都可以传入不同的函数值作为实参，被调用的函数会（在由它选择的时
机）调用传入的函数值。这些*高阶函数（higher-order function）*，即那些接收函数作为参数的函数，让我们有机会来进一步压缩和简化代码。    
　　高阶函数的好处之一是可以用来创建减少代码重复的控制抽象。例如，假定在编写一个文件浏览器，而你打算提供API给用户来查找匹配某个条件的文件。
首先，添加了一个机制用来查找文件名是以指定字符串结尾的文件。比如，这将允许用户查找所有扩展名为“.scala”的文件。可以通过在单例对象中定义一个
公共的filesEnding方法来提供这样的API：    
```scala
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles()
  
  def filesEnding(query: String) = 
    for (file <- filesHere; if file.getName.endsWith(query))
      yield file
}
```    
　　这个filesEnding方法用私有的助手方法filesHere来获取当前目录下的所有文件，然后基于文件名是否以用户给定的查询条件来结尾过滤这些文件。由于
filesHere是私有的，fileEnding方法是FileMatcher中定义的唯一一个能被访问到的方法。    


