# 使用可变参数创建Java中集合的工厂方法

## TAG
Java Idiom, Collection, Varargs, Factory Pattern

## 要解决什么问题？

Java中并没有对集合（java.util.collection）类型实现任何的简单初始化语法，如果你需要创建一个集合，并将一些数据放置到集合中，你需要写比较多的语句。
如要创建一个水果名称的Set，并将苹果、橙子、香蕉加入到这个集合中，我们会这样写：

	Set<String> fruits = new HashSet<String>();
	fruits.add("apple");
	fruits.add("orange");
	fruits.add("banana");
	
于是，每个需要这样做的地方，大家就开始使用Ctrl-C/V大法。
这种做法会造成大量的冗余代码，同时降低代码的鲁棒性和可维护性。

## 如何更好的解决这个问题？

我们可以创建一个工具类（为通用起见，这个工具类继承了apache的SetUtils），并实现一个工厂方法。

	import java.util.Arrays;
	import java.util.HashSet;
	import java.util.Set;

	import org.apache.commons.collections.SetUtils;

	public class MySetUtils extends SetUtils {
		
		public static<T> Set<T> setOf(T... arg) {
			return new HashSet<T>(Arrays.asList(arg));
		}

	}

然后我们就可以很简单地调用这个工厂方法来完成上面的初始化工作。

	Set s = MySetUtils.setOf("apple", "orange", "banana");

## 如果我们想要一个更通用的做法

我们可以在Collection层次上创建一个工具类，并实现工厂方法。

	import java.util.Arrays;
	import java.util.Collection;

	import org.apache.commons.collections.CollectionUtils;

	public class MyCollectionUtils extends CollectionUtils {
		
		public static<T> Collection<T> initCollection(Collection<T> c, T... arg) {
			c.addAll(Arrays.asList(arg));
			return c;
		}

	}

然后我们把调用的方式修改为：

	Set<String> s = (Set<String>) initCollection(new HashSet<>(), "apple", "orange", "banana");

或

	List<Integer> fibonacci = (List<Integer>) initCollection(new ArrayList<>(),
				1, 1, 2, 3, 5, 8, 13, 21);

OK.