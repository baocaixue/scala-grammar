# Scala的继承关系
- Scala的类继承关系...................................................[1](#Scala's-Class-Hierarchy)
- 基本类型的实现机制...................................................[2](#Primitives-Implemented)
- 底类型（bottom types）...................................................[3](#Bottom-Types)
- 定义自己的值类型 ...................................................[4](#Defining-You-Own-Value-Classes)    
    
　　在Scala中，每个类都继承自同一个名为*Any*的超类。由于每个类都是Any的子类，在Any中定义的方法是“全类型的”（universal）：它可以在
任何对象上被调用。Scala还在继承关系的底部定义了一些有趣的类，*Null*和*Nothing*，它们本质上是作为通用的子类存在的。例如，就像Any是
每一个其他类的超类那样，Nothing是每一个其他类的子类。

***    
## Scala's-Class-Hierarchy
　　
![image](https://github.com/baocaixue/scala-grammar/tree/master/scala-hierarchy/src/main/resources/ScalasHierarchy.png)
