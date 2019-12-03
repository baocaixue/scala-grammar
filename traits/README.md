# 特质
- 特质如何工作...................................................[1](#How-Traits-Work)
- 瘦接口和富接口...................................................[1](#Thin-Versus-Rich-Interfaces)
- 示例：矩形对象...................................................[1](#Rectangular-Objects)
- Ordered特质...................................................[1](#Order-Trait)
- 作为可叠加修改的特质...................................................[1](#Traits-As-Stackable-Modifications)
- 为什么不用多重继承...................................................[1](#Why-Not-Multiple-Inheritance)
- 要特质还是不要特质...................................................[1](#To-Trait-Or-Not-To-Trait)    
    
　　特质是Scala代码复用的基础单元。特质将方法和字段定义封装起来，然后通过将它们混入（mix in）类的方式来实现复用。它不同于类继承，类继承要求
每个类都继承自一个（明确）的超类，而类可以同时混入任意数量的特质。这里将展示特质的工作原理并给出两种最常见的适用场景：将“瘦”接口拓宽为“富”接
口，以及地难以可叠加的修改。以及使用Ordered特质，特质和其他语言中多重继承的对比。    

***    
## How-Traits-Work    
　　特质的定义和类定义很像，除了关键字**trait**：    
```scala
trait Philosophical {
  def philosophize() = {
    println("I consume memory, therefore I am!")
  }
}
```    
　　该特质名为Philosophical。它并没有声明一个超类，因此跟类一样，有一个默认的超类AnyRef。它定义一个名为philosophize的方法，这个方法是具
体的。这是一个简单的特质，只是为了展示特质的工作原理。    
　　一旦特质被定义好，就可以用extends或with关键字将它**混入**到类中。Scala中是*混入（mix in）* 特质，而不是从特质继承，因为混入特质跟其
他许多编程语言中的多重继承有重要的区别。如下，用extends混入了Philosophical特质：    
```scala
class Frog extends Philosophical {
  override def toString: String = "green"
}
```    
　　可以用extends关键字来混入特质，在这种情况下隐式地继承了特质的超类。例如，在上面代码中，Frog类是AnyRef的子类（因为AnyRef是Philosophical
的超类），并且混入了Philosophical。从特质继承的方法跟从超类继承的方法用起来一样：    
```shell script
scala> val frog = new Frog
frog: Frog = green
scala> frog.philosophize()
I consume memory, therefore I am!
```    
　　特质同时也定义了一个类型。以下是Philosophical被用作类型的例子：    
```shell script
scala> val phil: Philosophical = frog
phil: Philosophical = green
scala> phil.philosophize()
I consume memory, therefore I am!
```    
　　这里phil的类型是Philosophical，这是一个特质。因此，变量phil可以由任何混入了Philosophical的类的对象初始化。    
　　如果想要将特质混入一个显式继承自某个超类的类，可以用extends来给出这个超类，并用with来混入特质。如果想要混入多个特质，可以用with子句进
行添加。    
```scala
class Animal
trait  Philosophical
trait HasLegs

class Frog1 extends Animal with Philosophical {
  override def toString = "green"
}

class Frog2 extends Animal with Philosophical with HasLegs {
  override def toString = "green"
}
```    
　　目前为止，Frog类从Philosophical特质继承了philosophize的实现。Frog也可以重写philosophize。重写的语法跟重写超类中声明的方法看上去一
样。    
```scala
class Animal
trait Philosophical {
  def philosophize() = {
    println("I consume memory, therefore I am!")
  }
}
class Frog extends Animal with Philosophical {
  override def toString: String = "green"
  override def philosophize() = {
    println("It ain't easy being" + toString + "!")
  }
}
```    
　　由于这个新的Frog定义依然混入了Philosophical特质，仍然可以用同一个类型的变量使用它。不过由于Frog重写了Philosophical的philosophize
实现，当调用这个方法时，将得到新的行为。    
　　至此，感觉特质很像是拥有具体方法的Java接口，不过它们能做的实际上远不止这些。比方说，特质可以声明字段并保持状态。事实上，在特质定义中可以
做任何在类定义中做的事情，语法也完全相同，除了以下两种情况：    
　　首先，特质不能有任何“类”参数（即那些传入类的主构造方法的参数）。    
　　另一个类和特质的区别在于类中的super调用是静态绑定的，而在特质中super是动态绑定的。如果在类中编写`super.toString`这样的代码，可以确切
知道实际调用的是那一个实现。在定义特质的时候并没有被定义。具体是哪个实现被调用，在每次该特质被混入到某个具体的类时，都会重新判定。这里的super
看上去有些奇特的行为是特质能实现*可叠加修改（stackable modification）* 的关键。    

***    

