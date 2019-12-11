# 包和引入
- 将代码放进包里...................................................[1](#Putting-Code-In-Packages)
- 对相关代码的精简访问...................................................[2](#Concise-Access-To-Related-Code)
- 引入...................................................[3](#Imports)
- 隐式引入...................................................[4](#Implicit-Imports)
- 访问修饰符...................................................[5](#Access-Modifiers)
- 包对象...................................................[6](#Package-Objects)     
    
 　　在处理程序，尤其是大型程序时，减少*耦合（coupling）* 是很重要的。所谓耦合，指的是程序不同部分依赖其他部分的程度。低耦合能减少程序某个
 局部的某个看似无害的改动对其他部分造成严重后果的风险。减少耦合的一种方式是以模块化的风格编写代码。可以将程序切分成若干较小的模块，每个模块
 都有所谓的内部和外部之分。当在模块内部（即实现部分）工作时，只需要跟同样在这个模块工作的程序员协同。只有当你必须修改模块的外部（即接口部分）
 时，才有必要跟其他模块工作的开发者协同。    

***    
## Putting-Code-In-Packages
　　Scala代码存在于Java平台全局的包层次结构当中。在Scala中，可以通过两种方式将代码放进带名字的包里。第一种方式是在文件的顶部放置一个package
子句，让整个文件的内容放进指定的包：    
```scala
package com.isaac
class Navigator
```    
　　**注意**    
　　由于Scala代码是Java生态的一部分，对于你打算发布出来的Scala包，建议遵循Java将域名倒过来作为包名的习惯。    
　　另一种将Scala代码放进包的方式更像是C#的命名空间。可以在package子句之后加上一段用花括号包起来的代码块，这个代码块包含了进入该包的定义。
这个语法成为*打包（packaging）*。效果与上述一样：    
```scala
package com.isaac {
  class Navigator
}
```    
　　对于这样一个简单的例子而言，完全可以用第一种方式那样的写法。不过，这个更通用的表示法可以让我们在一个文件里包含多个包的内容。举例来说，可
以把某个类的测试代码跟原始代码放在同一个文件里，不过分成不同的包：    
```scala
package com {
  package isaac {
    //位于com.isaac包中
    class Navigator

    package test {
      //位于com.isaac.test包中
      class NavigatorSuite
    }
  }
}
```    

***    
## Concise-Access-To-Related-Code    
　　将代码按照包层次结构划分以后，不仅有助于浏览代码，同时也是在告诉编译器，同一个包中的代码之间存在某种相关性。在访问同一个包的代码时，Scala
允许使用简短的，不带限定前缀的名称。    
```scala
package com {
  package isaac {
    class Navigator {
      //不需要说com.isaac.StartMap
      val map = new StarMap
    }
    class StarMap
  }
  class Ship {
    //不需要说com.isaac.Navigator
    val nav = new isaac.Navigator
  }
  package org {
    class Fleet {
      //不需要说com.Ship
      def addShip() = new Ship
    }
  }
}
```    
　　这里给出了三个例子。首先，一个类不需要前缀就可以在自己的包内被别人访问。这就是为什么new StarMap能够通过编译;其次，包自身可以从包含它的
包里不带前缀地访问到。注意Navigator类是如何实例化的，new表达式出现在com包中，这个包包含了com.isaac包，因此可以简单地用isaac访问com.isaac
包的内容;再次，用花括号打包语法，所有在包外的作用域内可以被访问的名称，在包内也可以访问到，参见Ship的实例化。    
　　注意这类访问只有当你显式地嵌套打包时才有效。如果坚持每个文件只有一个包的做法，那么（就跟Java一样）只有那些在当前包内定义的名称才可以直接
使用。如果花括号嵌套包让代码过于往右缩进，可以用用多个package子句但不使用花括号：    
```scala
package com
package isaac
class Fleet
```    
　　最后一个小技巧也很重要。有时，会遇到需要在非常拥挤的作用域内编写代码，包名互相遮挡。如下列代码所示，MissionControl类的作用域内包含了三
个独立的名为launch的包！如何来分别引用Booster1、Booster2、Booster3呢？    
```scala
package launch {
  class Booster3
}
package isaac {
  package navigation {
    package launch {
      class Booster1
    }
    class MissionControl {
      val booster1 = new launch.Booster1
      val booster2 = new isaac.launch.Booster2
      val booster3 = new _root_.launch.Booster3
    }
  }
  package launch {
    class Booster2
  }
}
```    
　　访问地一个很容易。直接引用launch会指向isaac.navigation.launch包，因为这是最近的作用域定义的launch包。因此可以简单地用launch.Booster1
来引用第一个类。访问第二个也不难，可以用isaac.launch.Booster2，这样就可以清晰地表达想要的是哪一个包。那么问题就剩下第三个：考虑到嵌套的
launch包遮挡了位于顶层的那一个，那如何访问Booster3呢？    
　　为了解决这个问题，Scala提供了一个名为_root_的包，这个包不会跟任何用户编写的包冲突。换句话说，每个你能编写的顶层包都被当作是_root_包的
成员。    

***    

