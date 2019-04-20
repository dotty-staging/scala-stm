[![Build Status](https://travis-ci.org/scala-stm/scala-stm.svg?branch=master)](https://travis-ci.org/scala-stm/scala-stm)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.scala-stm/scala-stm_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.scala-stm/scala-stm_2.12)

# ScalaSTM
 
ScalaSTM is a lightweight software transactional memory for Scala,
inspired by the STMs in Haskell and Clojure. It was written by Nathan Bronson
and the Scala STM Expert Group, and it is published under a
BSD 3-Clause License.

ScalaSTM is available for Scala 2.11.x and 2.12.x.
It is also possible to use the library from Java, see `JavaAPITests`.

You can use it in your sbt build file as follows:

```scala
libraryDependencies += "org.scala-stm" %% "scala-stm" % "0.9.1"
```

Or in Maven:

```
<dependency>
  <groupId>org.scala-stm</groupId>
  <artifactId>scala-stm_2.11</artifactId>
  <version>0.9.1</version>
</dependency>
```

For download info and documentation see http://scala-stm.org

For the design of the library, see the paper

- Bronson, N. G., Chafi, H., \& Olukotun, K. [CCSTM: A Library-Based STM for Scala](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.220.1995).
  _Proceedings of the First Annual Scala Workshop_. Lausanne, 2010