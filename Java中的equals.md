# 深入掌握Java中的equals方法

## equals方法的定义

equals方法是Java中用来判断两个对象内容是否一样的方法，而很多现代语言如Python、Scala等，已经改为使用==判断对象内容了。
但Java为了保持与前面版本的一致性（历史包袱），仍然使用==判断对象引用是否一致，而使用equals判断对象内容是否一致。
考虑下面这个类：

	public class A {
		public int id;
		public String name;
	}
	
如果我们要为A加上一个equals方法，很可能就如下了：

	public class A {
		public int id;
		public String name;
		public boolean equals(A other) {
			return id == other.id && name == other.name;
		}
	}
	
很遗憾，这种写法是错误的。

首先要记得equals是Object类定义的方法，所有类都是Object类的子类，因此equals方法不是你创建的，而是你覆盖(Override)的。
因此如果你将上面的程序片段改为：

	public class A {
		public int id;
		public String name;
		@Override
		public boolean equals(A other) {
			return id == other.id && name == other.name;
		}
	}

编译器就拒绝通过了。因为我们需要看看Object类的equals方法签名：

    public boolean equals(Object arg);

因此，如果需要覆盖equals方法，你需要与Object中的equals方法签名一致：

	public class A {
		public int id;
		public String name;
		@Override
		public boolean equals(Object other) {
			//...
		}
	}

## 真正良好地实现一个equals方法

这样改呢？

	public class A {
		public int id;
		public String name;
		@Override
		public boolean equals(Object other) {
			return id == other.id && name == other.name;
		}
	}
	
你会发现，编译器还是会报错，错误原因是other并不具有id和name属性。因为other是一个Object！
所以你需要先改成这样：

	public class A {
		public int id;
		public String name;
		@Override
		public boolean equals(Object other) {
			if (!other instanceof A) {
				return false;
			}
			A that = (A) other;
			return id == that.id && name == that.name;
		}
	}

编译是总算通过了，但是依然是错误的，id是int的，用==比较没有问题，但name是String对象，又比较了引用的一致性了。再改：

	public class A {
		public int id;
		public String name;
		@Override
		public boolean equals(Object other) {
			if (!other instanceof A) {
				return false;
			}
			A that = (A) other;
			return id == that.id && name.equals(that.name);
		}
	}
	
你很满意，并且部署到了生产环境，却发现很多的来自客户的投诉，说页面经常出现500错误（我用Web开发举例了）。
那是因为name有可能是null，这里会有未捕获的空指针异常。再改：

	public class A {
		public int id;
		public String name;
		@Override
		public boolean equals(Object other) {
			if (!other instanceof A) {
				return false;
			}
			A that = (A) other;
			if (return id == that.id) {
				if (name == null) {
					return that.name == null;
				} else {
					return name.equals(that.name);
				}
			}
			return false;
		}
	}
	
这才是我们A类equals方法的最终版本。

## 调用equals方法

了解的equals方法的原理后，你应该就能大致想到如何调用别人做好的equals方法了。
- 要明确知道你代入到equals方法中的参数会被抹去类型，变成一个Object，因此事实上你可以用任何对象代入equals方法。
- 但是equals内部会首先做类型判定，如果类型都不一致，equals将直接返回false（因为后续无法强制造型成该类型）。

调用equals方法之前应该确保对象非空，比方说：

	String str;
	//...
	if (str.equals("hello world")) {
		//...
	}
	
这样的写法就不好，因为会出现未捕获的空指针异常，可以改为：

	String str;
	//...
	if (str != null && str.equals("hello world")) {
		//...
	}
	
当然，因为"hello world"这个字符串对象会在JVM启动的时候自动创建并放置在常量内存空间，因此最简单的写法应该是：

	String str;
	//...
	if ("hello world".equals(str)) {
		//...
	}
	
以上就是我对Java的equals方法的一些总结。
