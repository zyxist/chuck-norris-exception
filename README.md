ChuckNorrisException
====================

> Chuck Norris throws exceptions that can't be caught.

> If you catch ChuckNorrisException, you'll probably die.

This is an implementation of uncatchable exception for Java.

Quick start
-----------

```java
public class Example {
	public static void main(String[] args) {
		try {
			throw new ChuckNorrisException();
		} catch(ChuckNorrisException thr) {
			System.err.println("I've caught "+thr.getClass().getSimpleName());
		} finally {
			System.out.println("Finalization.");
		}
	}
}
```

For any other exception, the result of this program would be obvious. However, with `ChuckNorrisException` we get roundhouse-kicked:

```shell
Finalization.
Exception in thread "main" com.zyxist.chuck.ChuckNorrisException: I'm uncatchable!
	at com.zyxist.Example.main(Example.java:8)
```

More examples can be found in the source code!

How it works?
-------------

There are several features of Java compiler and JVM used to make this work:

 * there may exist multiple classes with the same name, but they must be loaded by different classloaders,
 * bytecode generation allows us generating an evil class and predenting we are "nice",
 * certain methods, such as `getMessage()` and `printStackTrace()` can be overriden,
 * it is possible to alter the stacktrace of the exception with `setStacktrace()` method, thus hiding our manipulations,
 * shutdown hook is our last line of defense.
 
Basically, the constructor of the exception uses **Javassist** to make a copy of another class, `RoundhouseKick`, changes its name to `ChuckNorrisException` and generates a classloader for it. After instantiating that class, it is something completely different from JVM perspective and our **catch** no longer matches it. With bytecode manipulation, we can also easily avoid `catch (Exception)`. We can't avoid being caught by `catch (Throwable)`, but with the rest of the tricks, we can escape it without problems, and still hide from being easily detected.

More explanation (in Polish) can be found at my blog: [www.zyxist.com](https://www.zyxist.com/blog/chuck-norris-exception).

License
-------

The code is in public domain. But please, don't use it in production.
