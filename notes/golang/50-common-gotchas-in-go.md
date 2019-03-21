# Go lang新手经常犯的50个错误

## 概述

Go是一门简单而有趣的语言，但是，类似任何其他语言，它也有一些坑。这些坑不完全是Go语言本身的错，有些坑是当你从其他语言转来的时候很自然会踩的，其他一些则是由于错误的预设条件和细节的缺失造成的。

很多这些坑当你花了时间去学习语言，阅读官方文档，查看wiki，参与邮件组讨论，细读Rob Pike帖子和演示，阅读源码之后都会变得很显而易见。但并非每个人都遵循同样的路线学习，这没问题。如果你是一个Go的新手，这篇文档将会节省你debug代码的许多时间。

## 目录

### 完全菜鸟

- [左大括号不能独立一行书写](#OpenBraceOnSepLine)
- [未使用的变量](#UnusedVariables)
- [未使用的引用](#UnusedImports)
- [短变量声明应该仅限于函数内部](#ShortVariableInsideFunctions)
- [使用短变量声明重新声明变量](#RedeclaringVariableUsingShortVariableDeclarations)
- [不能使用短变量声明给结构体属性赋值](#ShortVariableDeclarationsFieldValues)
- [意外的变量覆盖](#AccidentalVariableShadowing)
- [不能使用nil来初始化一个未指定类型的变量](#NilInitializeVariable)
- [使用nil的切片slice和表map](#UsingNilSlicesMaps)
- [表map容量](#MapCapacity)
- [字符串String不能为nil](#StringsCantBeNil)
- [数组函数参数](#ArrayFunctionArguments)
- [奇怪的range](#UnexpectedValuesRangeClauses)
- [数组和切片都是一维的](#SlicesAndArraysAreOne-Dimensional)
- [访问map中不存在的key](#AccessingNon-ExistingMapKeys)
- [字符串是不可变的](#StringsAreImmutable)
- [字符串和字节切片的转换](#ConversionsBetweenStringsandByteSlices)
- [字符串的序号操作](#StringsIndexOperator)
- [字符串不总是UTF8文本](#StringsAreNotAlwaysUTF8Text)
- [字符串长度](#StringLength)
- [在多行的切片，map，结构体中漏了逗号](#MissingCommaInMulti-Line)
- [log.Fatal和log.pPanic不只是写日志](#logFatallogPanic)
- [内建的数据结构操作都不是同步的](#Built-inDataStructureOperationsAreNotSynchronized)
- [使用for...range...语法迭代map](#IteratingThroughMapUsingRange)
- [Go中的fallthrough](#FallthroughBehaviorSwitchStatements)
- [自增和自减](#IncrementsDecrements)
- [按位非操作](#BitwiseNOTOperator)
- [运算符顺序区别](#OperatorPrecedenceDifferences)
- [未输出的结构体字段不会进行序列化编码](#UnexportedStructureFieldsAreNotEncoded)
- [应用程序会在仍然包含活动协程的时候退出](#AppExitsWithActiveGoroutines)
- [向非缓冲通道发送消息时如果接收对象未准备好会立即返回](#SendingUnbufferedChannelReturns)
- [向已经关闭的通道发送消息会发生严重错误退出](#SendingClosedChannelCausesPanic)
- [使用nil通道](#UsingnilChannels)
- [方法的接收者如果是传值方式定义的无法改变原始值](#MethodsValueReceivers)

### 中级初学者:

- [关闭HTTP响应体](#ClosingHTTPResponseBody)
- [关闭HTTP连接](#ClosingHTTPConnections)
- [JSON编码器会添加一个换行符](#JSONEncoderAddsNewlineCharacter)
- [JSON会将HTML特殊字符编码成逃逸字符](#JSONEscapesSpecialHTMLCharacters)
- [使用interface反序列化JSON中的数字](#UnmarshallingJSONNumbersInterfaceValues)
- [JSON字符串值不允许使用16进制字符或其他非UTF8逃逸序列](#JSONStringValues)
- [比较结构体、数组、slices和maps](#ComparingStructsArraysSlicesMaps)
- [从Panic中恢复](#RecoveringFromPanic)
- [在slice，数组和map的range语法中更新或取址元素值](#UpdatingReferencingItemValues)
- [slices中的隐藏数据](#HiddenDataSlices)
- [损坏的slice数据](#SliceDataCorruption)
- [过时的slice](#StaleSlices)
- [类型声明和方法](#TypeDeclarationsMethods)
- [实际类型转为接口类型时仍然会保留它们的方法](#InterfaceRetainMethodSets)
- [跳出for switch和for select结构块](#BreakingOutSwitchSelect)
- [for结构内的迭代变量和闭包](#IterationVariablesClosuresFor)
- [defer函数调用参数定值](#DeferredFunctionCallArgumentEvaluation)
- [defer函数的执行](#DeferredFunctionCallExecution)
- [失败的类型断言](#FailedTypeAssertions)
- [阻塞的goroutine和资源泄露](#BlockedGoroutinesResourceLeaks)
- [不同的零长度变量使用同样的地址](#SameAddressDifferentZeroSizedVariables)
- [第一次使用的iota不总是从0开始的](#iotaDoesntAlwaysStartZero)

### 高级初学者:

- [在指针类型接收者的方法中使用了值](#UsingPointerReceiverMethodsOnValue)
- [更新map的值字段](#UpdatingMapValueFields)
- [nil接口和nil接口的值](#nilInterfacesAndValues)
- [栈和堆变量](#StackHeapVariables)
- [GOMAXPROCS设置，并发和并行](#GOMAXPROCSConcurrencyParallelism)
- [读写操作重排序](#ReadWriteOperationReordering)
- [先发制人的调度](#PreemptiveScheduling)

### Cgo (又叫勇敢的初学者):

- [导入C库和多行导入代码块](#ImportCMultilineImportBlocks)
- [C包的import语句和Cgo的注释之间不能有空行](#NoblanklinesBetweenImportCandCgoComments)
- [不能调用C中的可变参数函数](#CantCallCFunctionswithVariableArguments)

## 陷阱、坑和一些常犯错误

### <a name="OpenBraceOnSepLine"></a>左大括号不能独立一行书写

> 级别: 菜鸟

> 大多数使用大括号的语言，你都可以自行选择左大括号的位置。Go不一样。感谢提供了自动分号;补充的功能，Go的左大括号不能独立写一行。是的，Go确实有分号 :-)

失败:

```go
package main

import "fmt"

func main()  
{ //error, can't have the opening brace on a separate line
    fmt.Println("hello there!")
}
```

    Compile Error:
    /tmp/sandbox826898458/main.go:6: syntax error: unexpected semicolon or newline before {

成功:

```go
package main

import "fmt"

func main() {  
    fmt.Println("works!")
}
```

### <a name="UnusedVariables"></a>未使用的变量

> 级别: 菜鸟

> 如果你的程序里有一个未使用的变量，编译会出错。但是这里有个例外，对于函数当中的变量你必须使用，但是对于全局变量来说未使用过是ok的。对于函数的实参来说，未使用也是ok的。

> 即使你给未使用过的变量赋一个新的值，你的程序依然无法通过编译。你必须在*使用*这个变量才能够让编译器满意。

失败:

```go
package main

var gvar int //not an error

func main() {  
    var one int   //error, unused variable
    two := 2      //error, unused variable
    var three int //error, even though it's assigned 3 on the next line
    three = 3

    func(unused string) {
        fmt.Println("Unused arg. No compile error")
    }("what?")
}
```

    Compile Errors:
    /tmp/sandbox473116179/main.go:6: one declared and not used /tmp/sandbox473116179/main.go:7: two declared and not used /tmp/sandbox473116179/main.go:8: three declared and not used

成功:

```go
package main

import "fmt"

func main() {  
    var one int
    _ = one

    two := 2 
    fmt.Println(two)

    var three int 
    three = 3
    one = three

    var four int
    four = four
}
```

当然，你还有一个选择是注释那些未使用过的变量的代码以求通过编译。

### <a name="UnusedImports"></a> 未使用的引用

> 级别: 菜鸟

> 如果你引用了一个包，但是却没有使用任何这个包导出的函数、接口、结构体或变量的话，程序也通不过编译。

> 如果你确实需要引入这个包，你可以使用_作为引入后的包名，这样可以避免编译错误。空标识作为包名引入的包只会获得程序包的副作用。

失败:

```go
package main

import (  
    "fmt"
    "log"
    "time"
)

func main() {  
}
```

    Compile Errors:
    /tmp/sandbox627475386/main.go:4: imported and not used: "fmt" /tmp/sandbox627475386/main.go:5: imported and not used: "log" /tmp/sandbox627475386/main.go:6: imported and not used: "time"

成功:

```go
package main

import (  
    _ "fmt"
    "log"
    "time"
)

var _ = log.Println

func main() {  
    _ = time.Now
}
```

还有一个选择是删除或者注释掉未使用的引用 :-) goimports工具可以帮助你完成这件工作。

### <a name="ShortVariableInsideFunctions"></a>短变量声明应该仅限于函数内部

> 级别: 菜鸟

失败:

```go
package main

myvar := 1 //error

func main() {  
}
```

    Compile Error:
    /tmp/sandbox265716165/main.go:3: non-declaration statement outside function body

成功:

```go
package main

var myvar = 1

func main() {  
}
```

### <a name="RedeclaringVariableUsingShortVariableDeclarations"></a>使用短变量声明重新声明变量

> 级别: 菜鸟

> Go不允许使用短变量声明方式重新声明变量，但是如果声明中有多个变量且至少一个变量是新声明的变量，则是ok的。

> 重新声明的变量和原变量必须处在同一个结构块中，否则你就会得到一个覆盖了的变量。

失败:

```go
package main

func main() {  
    one := 0
    one := 1 //error
}
```

    Compile Error:
    /tmp/sandbox706333626/main.go:5: no new variables on left side of :=

成功:

```go
package main

func main() {  
    one := 0
    one, two := 1,2

    one,two = two,one
}
```

### <a name="ShortVariableDeclarationsFieldValues"></a> 不能使用短变量声明给结构体属性赋值

> 级别: 菜鸟

失败:

```go
package main

import (  
  "fmt"
)

type info struct {  
  result int
}

func work() (int,error) {  
    return 13,nil  
  }

func main() {  
  var data info

  data.result, err := work() //error
  fmt.Printf("info: %+v\n",data)
}
```

    Compile Error:
    prog.go:18: non-name data.result on left side of := 

虽然有人已经提过这个坑，但是基本不会有改变因为Rob Pike喜欢现在的做法。

使用临时变量或者预先声明所有变量，然后使用普通的赋值语法解决这个问题。

成功:

```go
package main

import (  
  "fmt"
)

type info struct {  
  result int
}

func work() (int,error) {  
    return 13,nil  
  }

func main() {  
  var data info

  var err error
  data.result, err = work() //ok
  if err != nil {
    fmt.Println(err)
    return
  }

  fmt.Printf("info: %+v\n",data) //prints: info: {result:13}
}
```

### <a name="AccidentalVariableShadowing"></a>意外的变量覆盖

> 级别: 菜鸟

> 短变量声明太方便了（尤其是对于那些从动态类型语言来到go的程序员来说），使得很多人会将它当成是一个标准的赋值语句。但是如果你在新的代码块里面应用了它，编译器不会报错，但是你的程序将不会按照你的期望运行。

失败:

```go
package main

import "fmt"

func main() {  
    x := 1
    fmt.Println(x)     //prints 1
    {
        fmt.Println(x) //prints 1
        x := 2
        fmt.Println(x) //prints 2
    }
    fmt.Println(x)     //prints 1 (bad if you need 2)
}
```

这是一个非常普遍的坑，即使对于有经验的go开发人员来说也是。这个坑非常容易踩到，并且十分难以定位。

你可以使用vet命令来找到这样的问题。默认vet不会进行变量覆盖检查。确认你使用了`-shadow`标记。

    go tool vet -shadow your_file.go

注意vet命令并不会找到所有的变量覆盖问题。使用`go-nyet`可以获得更加全面的问题检测。

### <a name="NilInitializeVariable"></a>不能使用nil来初始化一个未指定类型的变量

> 级别: 菜鸟

> nil可以用来作为接口interface，函数function，指针pointer，表map，切片slice和通道channel的零值。如果你为指定变量的类型，编译器会拒绝通过因为它猜不出这将会是什么类型。

失败:

```go
package main

func main() {  
    var x = nil //error

    _ = x
}
```

    Compile Error:
    /tmp/sandbox188239583/main.go:4: use of untyped nil

成功:

```go
package main

func main() {  
    var x interface{} = nil

    _ = x
}
```

### <a name="UsingNilSlicesMaps"></a>使用nil的切片slice和表map

> 级别: 菜鸟

> 给一个nil的slice添加项目是ok的，但是如果给一个nil的map设置值将会导致panic。

成功:

```go
package main

func main() {  
    var s []int
    s = append(s,1)
}
```

失败:

```go
package main

func main() {  
    var m map[string]int
    m["one"] = 1 //error

}
```

### <a name="MapCapacity"></a>表map容量

> 级别: 菜鸟

> 你可以在创建map的时候设定容量，但是不能在map上使用内建cap函数。

失败:

```go
package main

func main() {  
    m := make(map[string]int,99)
    cap(m) //error
}
```

    Compile Error:
    /tmp/sandbox326543983/main.go:5: invalid argument m (type map[string]int) for cap

### <a name="StringsCantBeNil"></a>字符串String不能为nil

> 级别: 菜鸟

> 这是对于一些习惯为string设置为控制nil的开发者来说的一个坑。

失败:

```go
package main

func main() {  
    var x string = nil //error

    if x == nil { //error
        x = "default"
    }
}
```

    Compile Errors:
    /tmp/sandbox630560459/main.go:4: cannot use nil as type string in assignment /tmp/sandbox630560459/main.go:6: invalid operation: x == nil (mismatched types string and nil)

成功:

```go
package main

func main() {  
    var x string //defaults to "" (zero value)

    if x == "" {
        x = "default"
    }
}
```

### <a name="ArrayFunctionArguments"></a>数组函数参数

> 级别: 菜鸟

> 如果你是一个C或者C++的开发者，你会习惯于数组就是指针的概念。当你将数组传递给函数的时候，实际上你传递的是数组的引用，因此你可以在函数中改变数组的内容。但是在Go当中，数组是值传递的，因此当你将数组传递给函数时，实际上得到的是数组的拷贝，如果你在函数中试图改变数组的内容时，就会发生错误。

```go
package main

import "fmt"

func main() {  
    x := [3]int{1,2,3}

    func(arr [3]int) {
        arr[0] = 7
        fmt.Println(arr) //prints [7 2 3]
    }(x)

    fmt.Println(x) //prints [1 2 3] (not ok if you need [7 2 3])
}
```

如果你需要改变原数组的内容，使用数组的指针类型。

```go
package main

import "fmt"

func main() {  
    x := [3]int{1,2,3}

    func(arr *[3]int) {
        (*arr)[0] = 7
        fmt.Println(arr) //prints &[7 2 3]
    }(&x)

    fmt.Println(x) //prints [7 2 3]
}
```

还有一个选择是使用切片slice。即使函数获得的是slice的拷贝，但是它依然指向同一个数组。

```go
package main

import "fmt"

func main() {  
    x := []int{1,2,3}

    func(arr []int) {
        arr[0] = 7
        fmt.Println(arr) //prints [7 2 3]
    }(x)

    fmt.Println(x) //prints [7 2 3]
}
```

### <a name="UnexpectedValuesRangeClauses"></a>奇怪的range

> 级别: 菜鸟
> 这个坑经常出现在习惯于在其他语言中是用`"for-in"`或者`"foreach"`语法的开发者身上。`"range"`在Go中是一个不同的语法，这个生成器将会生成两个值：第一个值是序号，第二个值才是数据本身。

失败:

```go
package main

import "fmt"

func main() {  
    x := []string{"a","b","c"}

    for v := range x {
        fmt.Println(v) //prints 0, 1, 2
    }
}
```

成功:

```go
package main

import "fmt"

func main() {  
    x := []string{"a","b","c"}

    for _, v := range x {
        fmt.Println(v) //prints a, b, c
    }
}
```

### <a name="SlicesAndArraysAreOne-Dimensional"></a>数组和切片都是一维的

> 级别: 菜鸟

> Go看起来好像支持多维的数组和切片slice，但事实上并没有。虽然创建数组的数组和切片的切片是可能的，但是对于那些依赖与动态多维数组数值计算的应用程序来说，无论是性能还是复杂性都远未达理想。

> 你可以使用原始一维数组、独立切片组成的切片，共享数据的切片组成的切片构建动态多维数组。

> 如果你使用的是原始一维数组进行构建，那么你就需要自己负责寻址、边界检查和内存重分配如果数组需要扩容的话。

> 使用独立切片组成的切片构建动态多维数组包括两步。首先，你需要创建外部切片slice，然后你需要逐个构建内部切片slice。因为内部切片都是独立的，所以当你伸缩某个切片时其他切片不会受到影响。

```go
package main

func main() {  
    x := 2
    y := 4

    table := make([][]int,x)
    for i:= range table {
        table[i] = make([]int,y)
    }
}
```

> 使用共享数据的切片组成的切片创建的动态多维数组需要三步。首先你需要创建存放数据的容器，然后你创建外层的切片，最后你需要使用重新切片的方法将真实的数据分布在内层的切片中。

```go
package main

import "fmt"

func main() {  
    h, w := 2, 4

    raw := make([]int,h*w)
    for i := range raw {
        raw[i] = i
    }
    fmt.Println(raw,&raw[4])
    //prints: [0 1 2 3 4 5 6 7] <ptr_addr_x>

    table := make([][]int,h)
    for i:= range table {
        table[i] = raw[i*w:i*w + w]
    }

    fmt.Println(table,&table[1][0])
    //prints: [[0 1 2 3] [4 5 6 7]] <ptr_addr_x>
}
```

目前有提出让语言支持多维数组或切片的建议，但看起来这会是一个优先级较低的特性。

### <a name="AccessingNon-ExistingMapKeys"></a>访问map中不存在的key

> 级别: 菜鸟

> 对于很多习惯了其他语言的开发者来说，他们会期望访问map中不存在的key的时候，返回nil。事实上，访问map不存在的key时，返回的值将会是值类型的零值（zero value）。检查返回值是否为零值来验证key是否存在于map中是不保险的（比方说，map中值类型为布尔时，你无法通过零值false来判断key是否存在）。最保险的方法是使用返回的第二个ok值进行判定。

失败:

```go
package main

import "fmt"

func main() {  
    x := map[string]string{"one":"a","two":"","three":"c"}

    if v := x["two"]; v == "" { //incorrect
        fmt.Println("no entry")
    }
}
```

成功:

```go
package main

import "fmt"

func main() {  
    x := map[string]string{"one":"a","two":"","three":"c"}

    if _,ok := x["two"]; !ok {
        fmt.Println("no entry")
    }
}
```

### <a name="StringsAreImmutable"></a>字符串是不可变的

> 级别: 菜鸟

> 试图使用index序号去修改字符串中的某个字符会发生错误。字符串是只读的字节切片（当然还有一些额外属性）。如果你需要改变字符串，你应该使用字节切片`[]byte`，然后在需要的时候再转为字符串。

失败:

```go
package main

import "fmt"

func main() {  
    x := "text"
    x[0] = 'T'

    fmt.Println(x)
}
```

    Compile Error:
    /tmp/sandbox305565531/main.go:7: cannot assign to x[0]

成功:

```go
package main

import "fmt"

func main() {  
    x := "text"
    xbytes := []byte(x)
    xbytes[0] = 'T'

    fmt.Println(string(xbytes)) //prints Text
}
```

值得注意的是，当你需要修改字符串中的字符的时候修改字节切片的做法并不是正确的，因为一个字符通常会使用几个字节来存储。甚至转换为字符切片`[]rune`也不能保证一个字符是否会占据多个rune。这也是Go最终将字符串实现成`[]byte`的原因。

### <a name="ConversionsBetweenStringsandByteSlices"></a> 字符串和字节切片的转换

> 级别: 菜鸟

> 当你进行字符串和字节切片`[]byte`之间的转换时，你会得到原数据的一分完整拷贝。这和其他语言的类型转换`cast`是不同的，这也不是新创建了一个切片并指向了原数据。

> Go确实有些操作对两者的转换进行了优化，避免额外的内存消耗（在todo list上面还有更多的优化内容）。

> 第一个优化是当使用字节切片`[]byte`作为key查找map中的string key时：`map[string([]byte)]`。

> 第二个优化是当使用range来遍历一个string产生的[]byte时：`for i,v := range []byte(str) {...}`。

### <a name="StringsIndexOperator"></a>字符串的序号操作

> 级别: 菜鸟

> 字符串的序号操作返回的是字节，而不是像其他语言那样返回字符。

```go
package main

import "fmt"

func main() {  
    x := "text"
    fmt.Println(x[0]) //print 116
    fmt.Printf("%T",x[0]) //prints uint8
}
```

如果你需要访问字符串中的字符，使用`for range`语法。标准库中`unicode/utf8`和实验库中`golang.org/x/exp/utf8string`也很有用，utf8string包提供了At方法。将字符串转换为字符切片`[]rune`也是一个选择。

### <a name="StringsAreNotAlwaysUTF8Text"></a>字符串不总是UTF8文本

> 级别: 菜鸟

> 字符串内容并无要求必须为UTF8文本。实际上字符串能包含任意字节内容。

> 要知道一个字符串是否是一个UTF8文本字符串，可以使用`unicode/utf8`包中的`ValidString()`函数。

```go
package main

import (  
    "fmt"
    "unicode/utf8"
)

func main() {  
    data1 := "ABC"
    fmt.Println(utf8.ValidString(data1)) //prints: true

    data2 := "A\xfeC"
    fmt.Println(utf8.ValidString(data2)) //prints: false
}
```

### <a name="StringLength"></a>字符串长度

> 级别: 菜鸟

> 如果说你是个python开发者，然后你有下面的代码：

```python
data = u'♥'  
print(len(data)) #prints: 1  
```

> 当你将上面代码转为相似的Go代码的话，你会惊讶结果。

```go
package main

import "fmt"

func main() {  
    data := "♥"
    fmt.Println(len(data)) //prints: 3
}
```

内建的`len()`函数返回的是字节的个数而不是字符的个数。

要获得与python代码一样的结果，你需要使用`unicode/utf8`包中的`RuneCountInString()`函数。

```go
package main

import (  
    "fmt"
    "unicode/utf8"
)

func main() {  
    data := "♥"
    fmt.Println(utf8.RuneCountInString(data)) //prints: 1
}
```

纯技术上说，`RuneCountInString()`函数也不是返回字符的个数，因为一个字符可能会占据多个符号rune。

```go
package main

import (  
    "fmt"
    "unicode/utf8"
)

func main() {  
    data := "é"
    fmt.Println(len(data))                    //prints: 3
    fmt.Println(utf8.RuneCountInString(data)) //prints: 2
}
```

### <a name="MissingCommaInMulti-Line"></a>在多行的切片，map，结构体中漏了逗号

> 级别: 菜鸟

失败:

```go
package main

func main() {  
    x := []int{
    1,
    2 //error
    }
    _ = x
}
```

    Compile Errors:
    /tmp/sandbox367520156/main.go:6: syntax error: need trailing comma before newline in composite literal /tmp/sandbox367520156/main.go:8: non-declaration statement outside function body /tmp/sandbox367520156/main.go:9: syntax error: unexpected }

成功:

```go
package main

func main() {  
    x := []int{
    1,
    2,
    }
    x = x

    y := []int{3,4,} //no error
    y = y
}
```

如果你将这些初始化声明卸载一行代码当中，编译器就不会产生错误。

###　<a name="logFatallogPanic"></a>log.Fatal和log.pPanic不只是写日志

> 级别: 菜鸟

> 日志库通常会提供多种不同的日志级别。Go的日志库不止提供日志功能，当你使用`log.Fatal*()`或`log.Panic*()`函数时，Go还会终止你的应用程序。

```go
package main

import "log"

func main() {  
    log.Fatalln("Fatal Level: log entry") //app exits here
    log.Println("Normal Level: log entry")
}
```

### <a name="Built-inDataStructureOperationsAreNotSynchronized"></a>内建的数据结构操作都不是同步的

> 级别: 菜鸟

> 虽然Go有许多原生支付并发的特性，并发安全性却不是其中之一。保证并发集合操作的原子性是开发者自己的责任。协程和通道是推荐使用的方法，你也可以评估`sync`包的使用，如果你的程序需要的话。

### <a name="IterationValuesForStrings"></a>使用range遍历string时候的值

> 级别: 菜鸟

> 序号值（range操作返回的第一个值）是当前字符（unicode符号）的首个字节所在的序号值。字符是range操作返回的第二个值。这个序号值与其他语言中返回的是字符的序号是不一样的。请记住，真实的字符可能是由多个符号rune组成的。当你需要完整操作字符时，你应该使用`golang.org/x/text/unicode/norm`包。

> `for...range...`语法会试图将数据按照UTF8文本进行读取。在这个过程中，如果碰到任何无法理解的字节时，该语法将会使用`0xfffd`（也就是unicode中的替代字符）作为第二个返回值。如果你的string当中存储的包括非UTF8文本内容，这种情况下，就会得不到你想要的字节数据，因此这种情况下，应该首先将string转换为byte切片再带入range中去迭代。

```go
package main

import "fmt"

func main() {  
    data := "A\xfe\x02\xff\x04"
    for _,v := range data {
        fmt.Printf("%#x ",v)
    }
    //prints: 0x41 0xfffd 0x2 0xfffd 0x4 (not ok)

    fmt.Println()
    for _,v := range []byte(data) {
        fmt.Printf("%#x ",v)
    }
    //prints: 0x41 0xfe 0x2 0xff 0x4 (good)
}
```

### <a name="IteratingThroughMapUsingRange">使用for...range...语法迭代map

> 级别: 菜鸟

> 如果你希望从map中迭代的时候会出现一定的顺序（如按照key value进行排序），你就踩到坑了。每次map的迭代都会产生不同的结果。Go的runtime会在迭代map的时候试图执行一次轻量的随机。但这并不是确保成功的，因此你也有可能获得相同的迭代顺序。

```go
package main

import "fmt"

func main() {  
    m := map[string]int{"one":1,"two":2,"three":3,"four":4}
    for k,v := range m {
        fmt.Println(k,v)
    }
}
```

如果你是在使用[go游乐场](https://play.golang.org/)的话，你都会得到同一个结果，因为游乐场只有当你修改了代码之后才会重新编译一次。

### <a name="FallthroughBehaviorSwitchStatements"></a>Go中的fallthrough

> 级别: 菜鸟

> 在Go语言中，switch里面的case结构默认会跳出`break`。这和其他语言当中的默认向后执行`fallthrough`是不一样的。

```go
package main

import "fmt"

func main() {  
    isSpace := func(ch byte) bool {
        switch(ch) {
        case ' ': //error
        case '\t':
            return true
        }
        return false
    }

    fmt.Println(isSpace('\t')) //prints true (ok)
    fmt.Println(isSpace(' '))  //prints false (not ok)
}
```

你可以在每个case结构块的最后加上`fallthrough`语句强制向后执行。你也可以重写case条件，将多个条件设置为条件列表的方式。

```go
package main

import "fmt"

func main() {  
    isSpace := func(ch byte) bool {
        switch(ch) {
        case ' ', '\t':
            return true
        }
        return false
    }

    fmt.Println(isSpace('\t')) //prints true (ok)
    fmt.Println(isSpace(' '))  //prints true (ok)
}
```

### <a name="IncrementsDecrements"></a>自增和自减

> 级别: 菜鸟

> 许多语言都提供自增和自减操作。Go与其他语言不太一样，不支持自增自减运算的前缀写法，也不支持将自增自减操作写在其他表达式中。

失败:

```go
package main

import "fmt"

func main() {  
    data := []int{1,2,3}
    i := 0
    ++i //error
    fmt.Println(data[i++]) //error
}
```

    Compile Errors:
    /tmp/sandbox101231828/main.go:8: syntax error: unexpected ++ /tmp/sandbox101231828/main.go:9: syntax error: unexpected ++, expecting :

成功:

```go
package main

import "fmt"

func main() {  
    data := []int{1,2,3}
    i := 0
    i++
    fmt.Println(data[i])
}
```

### <a name="BitwiseNOTOperator"></a>按位非操作

> 级别: 菜鸟

> 许多语言使用`～`作为按位非操作的运算符。Go使用的是和异或运算一样的符号`^`来表示。

失败:

```go
package main

import "fmt"

func main() {  
    fmt.Println(~2) //error
}
```

    Compile Error:
    /tmp/sandbox965529189/main.go:6: the bitwise complement operator is ^

成功:

```go
package main

import "fmt"

func main() {  
    var d uint8 = 2
    fmt.Printf("%08b\n",^d)
}
```

Go仍然使用`^`符号作为异或运算符，对于一些人来说也有一定疑惑性。

你可以使用二元异或运算（如0x02 XOR 0xff）表示一元非运算（如NOT 0x02）。这就能解释为什么`^`在Go中被用来表示一元非运算。

Go还有一个特殊的与非位运算符`&^`，更加增加了非运算的疑惑性。这个运算符有点像一个特性为了支持类似A AND (NOT B)，然后不需要括号运算。

```go
package main

import "fmt"

func main() {  
    var a uint8 = 0x82
    var b uint8 = 0x02
    fmt.Printf("%08b [A]\n",a)
    fmt.Printf("%08b [B]\n",b)

    fmt.Printf("%08b (NOT B)\n",^b)
    fmt.Printf("%08b ^ %08b = %08b [B XOR 0xff]\n",b,0xff,b ^ 0xff)

    fmt.Printf("%08b ^ %08b = %08b [A XOR B]\n",a,b,a ^ b)
    fmt.Printf("%08b & %08b = %08b [A AND B]\n",a,b,a & b)
    fmt.Printf("%08b &^%08b = %08b [A 'AND NOT' B]\n",a,b,a &^ b)
    fmt.Printf("%08b&(^%08b)= %08b [A AND (NOT B)]\n",a,b,a & (^b))
}
```

### <a name="OperatorPrecedenceDifferences"></a>运算符顺序区别

> 级别: 菜鸟

> 除了“位清除”运算符`&^`，Go也有一整套和别的语言一致的标准运算，但是运算顺序和其他语言不一定相同。

```go
package main

import "fmt"

func main() {  
    fmt.Printf("0x2 & 0x2 + 0x4 -> %#x\n",0x2 & 0x2 + 0x4)
    //prints: 0x2 & 0x2 + 0x4 -> 0x6
    //Go:    (0x2 & 0x2) + 0x4
    //C++:    0x2 & (0x2 + 0x4) -> 0x2

    fmt.Printf("0x2 + 0x2 << 0x1 -> %#x\n",0x2 + 0x2 << 0x1)
    //prints: 0x2 + 0x2 << 0x1 -> 0x6
    //Go:     0x2 + (0x2 << 0x1)
    //C++:   (0x2 + 0x2) << 0x1 -> 0x8

    fmt.Printf("0xf | 0x2 ^ 0x2 -> %#x\n",0xf | 0x2 ^ 0x2)
    //prints: 0xf | 0x2 ^ 0x2 -> 0xd
    //Go:    (0xf | 0x2) ^ 0x2
    //C++:    0xf | (0x2 ^ 0x2) -> 0xf
}
```

### <a name="UnexportedStructureFieldsAreNotEncoded"></a>未输出的结构体字段不会进行序列化编码

> 级别: 菜鸟

> 结构体的小写字母开头的字段名称字段不会被（json, xml, gob等）编码，因此当你解码这个结构体数据的时候，相应的未输出字段会被设置为对应的零值。

```go
package main

import (  
    "fmt"
    "encoding/json"
)

type MyData struct {  
    One int
    two string
}

func main() {  
    in := MyData{1,"two"}
    fmt.Printf("%#v\n",in) //prints main.MyData{One:1, two:"two"}

    encoded,_ := json.Marshal(in)
    fmt.Println(string(encoded)) //prints {"One":1}

    var out MyData
    json.Unmarshal(encoded,&out)

    fmt.Printf("%#v\n",out) //prints main.MyData{One:1, two:""}
}
```

### <a name="AppExitsWithActiveGoroutines"></a>应用程序会在仍然包含活动协程的时候退出

> 级别: 菜鸟

> 应用程序不会等待所有协程完成之后才退出。这是对于初学者来说很普遍会犯的错误。

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    workerCount := 2

    for i := 0; i < workerCount; i++ {
        go doit(i)
    }
    time.Sleep(1 * time.Second)
    fmt.Println("all done!")
}

func doit(workerId int) {  
    fmt.Printf("[%v] is running\n",workerId)
    time.Sleep(3 * time.Second)
    fmt.Printf("[%v] is done\n",workerId)
}
```

输出结果:

    [0] is running 
    [1] is running 
    all done!

最通用的解决办法是使用"WaitGroup"变量。这种方式可以让主协程等待所有的工作协程都完成。如果你的应用程序有较长的运行协程，并带有消息处理机制的话，你需要一种方法来消息通知那些协程停止运行。你可以发送一个"kill"消息给每个工作协程。另一种选择是可以关闭一个所有工作协程都接收的通道channel。这是消息同时通知所有协程的一个简单的方法。

```go
package main

import (  
    "fmt"
    "sync"
)

func main() {  
    var wg sync.WaitGroup
    done := make(chan struct{})
    workerCount := 2

    for i := 0; i < workerCount; i++ {
        wg.Add(1)
        go doit(i,done,wg)
    }

    close(done)
    wg.Wait()
    fmt.Println("all done!")
}

func doit(workerId int,done <-chan struct{},wg sync.WaitGroup) {  
    fmt.Printf("[%v] is running\n",workerId)
    defer wg.Done()
    <- done
    fmt.Printf("[%v] is done\n",workerId)
}
```

运行之后，你会看到输出:

    [0] is running 
    [0] is done 
    [1] is running 
    [1] is done

看起来工作协程在主协程退出前都完成了。但是你也会看到以下输出：

    fatal error: all goroutines are asleep - deadlock!

这就有点坑爹了:-) 发生了什么？为什么会有死锁？工作协程完成之后会执行`wg.Done()`。程序应该正常才对。

死锁发生是因为每个工作协程都获得了一个原始"WaitGroup"变量的副本。当工作协程执行`wg.Done()`时，实际上是在副本上执行的，因此没有对主协程的"WaitGroup"变量产生影响。

```go
package main

import (  
    "fmt"
    "sync"
)

func main() {  
    var wg sync.WaitGroup
    done := make(chan struct{})
    wq := make(chan interface{})
    workerCount := 2

    for i := 0; i < workerCount; i++ {
        wg.Add(1)
        go doit(i,wq,done,&wg)
    }

    for i := 0; i < workerCount; i++ {
        wq <- i
    }

    close(done)
    wg.Wait()
    fmt.Println("all done!")
}

func doit(workerId int, wq <-chan interface{},done <-chan struct{},wg *sync.WaitGroup) {  
    fmt.Printf("[%v] is running\n",workerId)
    defer wg.Done()
    for {
        select {
        case m := <- wq:
            fmt.Printf("[%v] m => %v\n",workerId,m)
        case <- done:
            fmt.Printf("[%v] is done\n",workerId)
            return
        }
    }
}
```

现在程序按照期望执行了 :-)

### <a name="SendingUnbufferedChannelReturns"></a>向非缓冲通道发送消息时如果接收对象为准备好会立即返回

> 级别: 菜鸟

> 发送者不会在接收者处理消息之前阻塞。在发送者继续执行代码之前接收者的协程是否有足够时间处理消息取决于运行的机器。

```go
package main

import "fmt"

func main() {  
    ch := make(chan string)

    go func() {
        for m := range ch {
            fmt.Println("processed:",m)
        }
    }()

    ch <- "cmd.1"
    ch <- "cmd.2" //won't be processed
}
```

### <a name="SendingClosedChannelCausesPanic"></a>向已经关闭的通道发送消息会发生严重错误退出(Panic)

> 级别: 菜鸟

> 从已经关闭的通道读取消息是安全的。第二个返回值ok会在通道读不到消息时候返回false。如果你试图从带缓冲的通道中读取消息的话，你会先读到所有已经缓冲的消息，如果通道关闭之后，ok会返回false。

> 向已经关闭的通道发送消息会Panic。这是标准定义了的，但是对于一个初学者来说，读和写已经关闭的通道造成的两种完全不同的结果通常很令人迷惑。

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    ch := make(chan int)
    for i := 0; i < 3; i++ {
        go func(idx int) {
            ch <- (idx + 1) * 2
        }(i)
    }

    //get the first result
    fmt.Println(<-ch)
    close(ch) //not ok (you still have other senders)
    //do other work
    time.Sleep(2 * time.Second)
}
```

解决方案取决于你的应用程序。有可能只需要很少的修改或者有可能需要对整个应用程序进行设计。无论如何，你都必须保证你的应用程序不要向已经关闭的通道写入消息。

上述的错误例子可以使用一个特殊的取消通道来向剩下的工作协程发送取消的消息，告诉那些仍试图发送消息的协程他们不应再向已经关闭的通道发送消息。

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    ch := make(chan int)
    done := make(chan struct{})
    for i := 0; i < 3; i++ {
        go func(idx int) {
            select {
            case ch <- (idx + 1) * 2: fmt.Println(idx,"sent result")
            case <- done: fmt.Println(idx,"exiting")
            }
        }(i)
    }

    //get first result
    fmt.Println("result:",<-ch)
    close(done)
    //do other work
    time.Sleep(3 * time.Second)
}
```

### <a name="UsingnilChannels"></a>使用nil通道

> 级别: 菜鸟

> 向nil通道发送或者接受消息将会永远阻塞，这是标准严格定义了的，但是对于新手来说这是一个很难以理解的行为。

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    var ch chan int
    for i := 0; i < 3; i++ {
        go func(idx int) {
            ch <- (idx + 1) * 2
        }(i)
    }

    //get first result
    fmt.Println("result:",<-ch)
    //do other work
    time.Sleep(2 * time.Second)
}
```

如果你试图运行这个程序，你会看到类似的输出：`fatal error: all goroutines are asleep - deadlock!`

这个行为可以用来动态的激活或者失效select语句中的case结构块。

```go
package main

import "fmt"  
import "time"

func main() {  
    inch := make(chan int)
    outch := make(chan int)

    go func() {
        var in <- chan int = inch
        var out chan <- int
        var val int
        for {
            select {
            case out <- val:
                out = nil
                in = inch
            case val = <- in:
                out = outch
                in = nil
            }
        }
    }()

    go func() {
        for r := range outch {
            fmt.Println("result:",r)
        }
    }()

    time.Sleep(0)
    inch <- 1
    inch <- 2
    time.Sleep(3 * time.Second)
}
```

### <a name="MethodsValueReceivers"></a>方法的接收者如果是传值方式定义的无法改变原始值

> 级别: 菜鸟

> 方法的接收者就像正常的函数参数一样。如果定义的时候是使用传值方式定义的，则你的方法将会得到一个接收者的副本。这意味着在副本上做出的改变不会影响到原始值。除非你需要改变的是map或slice中的值，又或者是结构体里的字段值。

```go
package main

import "fmt"

type data struct {  
    num int
    key *string
    items map[string]bool
}

func (this *data) pmethod() {  
    this.num = 7
}

func (this data) vmethod() {  
    this.num = 8
    *this.key = "v.key"
    this.items["vmethod"] = true
}

func main() {  
    key := "key.1"
    d := data{1,&key,make(map[string]bool)}

    fmt.Printf("num=%v key=%v items=%v\n",d.num,*d.key,d.items)
    //prints num=1 key=key.1 items=map[]

    d.pmethod()
    fmt.Printf("num=%v key=%v items=%v\n",d.num,*d.key,d.items) 
    //prints num=7 key=key.1 items=map[]

    d.vmethod()
    fmt.Printf("num=%v key=%v items=%v\n",d.num,*d.key,d.items)
    //prints num=7 key=v.key items=map[vmethod:true]
}
```

### <a name="ClosingHTTPResponseBody"></a>关闭HTTP响应体

> 级别: 中级初学者

> 当你使用标准http库并获得http响应时，如果你不读取响应体你仍然需要关闭它。需要注意的是，关闭操作即使对于空响应体时也是需要的。对于初学者来说，这是非常容易遗忘的地方。

> 有些初学者确实试图关闭响应体，但是他们在错误的位置使用了关闭操作。

```go
package main

import (  
    "fmt"
    "net/http"
    "io/ioutil"
)

func main() {  
    resp, err := http.Get("https://api.ipify.org?format=json")
    defer resp.Body.Close()//not ok
    if err != nil {
        fmt.Println(err)
        return
    }

    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println(string(body))
}
```

这段代码对于成功的请求时是没问题的，但是如果http请求失败了，resp变量可能为nil，这样会导致Panic。

最通常的做法是在检查了http响应错误之后再关闭响应体变量。

```go
package main

import (  
    "fmt"
    "net/http"
    "io/ioutil"
)

func main() {  
    resp, err := http.Get("https://api.ipify.org?format=json")
    if err != nil {
        fmt.Println(err)
        return
    }

    defer resp.Body.Close()//ok, most of the time :-)
    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println(string(body))
}
```

大多数情况下，当你的http请求失败时，resp会是非nil，而err会是nil。但是如果服务器返回了一个重定向响应，这两个返回值都可能是非空的。这意味着这种情况下你仍然会产生一个内存泄漏。

你可以通过在err判断的代码中增加一个对resp的非nil判断并关闭响应体来修复这个泄漏。另一种更加方便地选择是对于所有非nil的响应体统一进行关闭操作处理。

```go
package main

import (  
    "fmt"
    "net/http"
    "io/ioutil"
)

func main() {  
    resp, err := http.Get("https://api.ipify.org?format=json")
    if resp != nil {
        defer resp.Body.Close()
    }

    if err != nil {
        fmt.Println(err)
        return
    }

    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println(string(body))
}
```

老版本的`resp.Body.Close()`的实现会读取并丢弃剩下的响应体数据。如果http连接上设置了keepalive属性，这可以保证这个http连接可以下次被另一个请求重用。最新的http客户端的实现已经发生了改变。现在是你的责任来读取和丢弃响应体的剩余数据。如果你不这样做，那么http连接将会被关闭而不是重用。这个改变会在Go 1.5的标准文档中声明。

如果对于你的应用程序来说重用连接非常重要，你应该加入如下的语句来处理响应体中剩余的数据：

```go
_, err = io.Copy(ioutil.Discard, resp.Body) 
```
    
这种做法在你如果并不会立刻读取整个响应体数据的时候是必须的。例如当你在使用如下的代码处理json API的时候：

```go
json.NewDecoder(resp.Body).Decode(&data) 
```

### <a name="ClosingHTTPConnections"></a>关闭HTTP连接

> 级别: 中级初学者

> 有些HTTP服务器会保持网络连接一段时间（基于HTTP 1.1标准和服务器的"keep-alive"设置）。默认情况下，标准库中的http仅会在服务器要求关闭连接时才关闭。这意味着在某些情况下，你的应用程序有可能会用光套接字或文件描述符资源。

> 你可以将request变量的Close字段设置为true来要求http库在完成请求后关闭网络连接。

> 另一个选择是在请求头部设置一个Connection属性并设置为close。HTTP服务器应该在响应中也会加入一个Connection头并设置值close。当http库读取到这个头部数据时，就会关闭网络连接。

```go
package main

import (  
    "fmt"
    "net/http"
    "io/ioutil"
)

func main() {  
    req, err := http.NewRequest("GET","http://golang.org",nil)
    if err != nil {
        fmt.Println(err)
        return
    }

    req.Close = true
    //or do this:
    //req.Header.Add("Connection", "close")

    resp, err := http.DefaultClient.Do(req)
    if resp != nil {
        defer resp.Body.Close()
    }

    if err != nil {
        fmt.Println(err)
        return
    }

    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println(len(string(body)))
}
```

你也可以在全局关闭http连接重用属性。这样你就需要创建自定义的http传输属性Transport。

```go
package main

import (  
    "fmt"
    "net/http"
    "io/ioutil"
)

func main() {  
    tr := &http.Transport{DisableKeepAlives: true}
    client := &http.Client{Transport: tr}

    resp, err := client.Get("http://golang.org")
    if resp != nil {
        defer resp.Body.Close()
    }

    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println(resp.StatusCode)

    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println(len(string(body)))
}
```

如果你会对同一个HTTP服务进行许多次的请求，那么保持网络连接是正确的。但是，如果你的程序会在短时间内对多个HTTP服务器进行一两次的少量请求，那么每次收到响应之后立即关闭网络连接会比较好。增加套接字和文件描述符的限制也可能是一个好方法。正确的解决方案取决于你的应用程序需要。

### <a name="JSONEncoderAddsNewlineCharacter"></a>JSON编码器会添加一个换行符

> 级别: 中级初学者

> 你为你的JSON编码器函数编写了一个单元测试，然而你却发现你的测试失败了因为你没有获得你期望的JSON字符串值。这是因为你在使用JSON编码器，因此会在编码后的JSON字符串后面添加一个换行符。

```go
package main

import (
  "fmt"
  "encoding/json"
  "bytes"
)

func main() {
  data := map[string]int{"key": 1}
  
  var b bytes.Buffer
  json.NewEncoder(&b).Encode(data)

  raw,_ := json.Marshal(data)
  
  if b.String() == string(raw) {
    fmt.Println("same encoded data")
  } else {
    fmt.Printf("'%s' != '%s'\n",raw,b.String())
    //prints:
    //'{"key":1}' != '{"key":1}\n'
  }
}
```

JSON编码器的设计是用于流处理的。JSON的流式处理通常意味着使用一个换行符分隔多个JSON对象，这就是为什么JSON编码器会在最后添加一个换行符的原因。这是语言标准中定义的，但是很容易被遗忘。

### <a name="JSONEscapesSpecialHTMLCharacters"></a>JSON会将HTML特殊字符编码成逃逸字符

> 级别: 中级初学者

> 这是一个语言标准定义的特性。`SetEscapHTML`方法的描述中说明了Go中JSON编码对于诸如and, 小于, 大于这类HTML特殊符号的默认编码行为。

> 事实上，这是Go team处于一些原因对于JSON处理的不特别理想的设计。首先，你无法在`json.Marshall`方法中关闭这种特性。其次，这是一个对于安全特性不太良好的设计，因为这种做法的出发点是认为只要处理了HTML特殊字符编码就可以保护所有的XSS攻击。实际上存在着许多的场景，这些场景的数据安全都需要其自身的编码方式。最后，这个做法认为JSON的主要应用场景是在web应用上，这和REST的设计初衷是违背的。

```go
package main

import (
  "fmt"
  "encoding/json"
  "bytes"
)

func main() {
  data := "x < y"
  
  raw,_ := json.Marshal(data)
  fmt.Println(string(raw))
  //prints: "x \u003c y" <- probably not what you expected
  
  var b1 bytes.Buffer
  json.NewEncoder(&b1).Encode(data)
  fmt.Println(b1.String())
  //prints: "x \u003c y" <- probably not what you expected
  
  var b2 bytes.Buffer
  enc := json.NewEncoder(&b2)
  enc.SetEscapeHTML(false)
  enc.Encode(data)
  fmt.Println(b2.String())
  //prints: "x < y" <- looks better
}
```
给Go team的一个建议，将这个特性作为可选的吧。

### <a name="UnmarshallingJSONNumbersInterfaceValues"></a>使用interface反序列化JSON中的数字

> 级别: 中级初学者
> 采用interface反序列化JSON数据时，Go默认会将JSON中的数字解码为float64。这意味着一下的代码会panic:

```go
package main

import (  
  "encoding/json"
  "fmt"
)

func main() {  
  var data = []byte(`{"status": 200}`)

  var result map[string]interface{}
  if err := json.Unmarshal(data, &result); err != nil {
    fmt.Println("error:", err)
    return
  }

  var status = result["status"].(int) //error
  fmt.Println("status value:",status)
}
```

    Runtime Panic:
    panic: interface conversion: interface is float64, not int 

如果你JSON数据中的数字本身是一个整数时，你有下面的选择。

选择一: 就使用float64类型 :-)

选择二: 将interface推断为float64之后再转换为整数。

```go
package main

import (  
  "encoding/json"
  "fmt"
)

func main() {  
  var data = []byte(`{"status": 200}`)

  var result map[string]interface{}
  if err := json.Unmarshal(data, &result); err != nil {
    fmt.Println("error:", err)
    return
  }

  var status = uint64(result["status"].(float64)) //ok
  fmt.Println("status value:",status)
}
```

选择三: 使用一个解码器类型来反序列化JSON并且告诉解码器使用Number interface类型来表示JSON中的数字。

```go
package main

import (  
  "encoding/json"
  "bytes"
  "fmt"
)

func main() {  
  var data = []byte(`{"status": 200}`)

  var result map[string]interface{}
  var decoder = json.NewDecoder(bytes.NewReader(data))
  decoder.UseNumber()

  if err := decoder.Decode(&result); err != nil {
    fmt.Println("error:", err)
    return
  }

  var status,_ = result["status"].(json.Number).Int64() //ok
  fmt.Println("status value:",status)
}
```

你可以将数字表示成字符串形式，然后将它反序列化成另一种数字类型：

```go
package main

import (  
  "encoding/json"
  "bytes"
  "fmt"
)

func main() {  
  var data = []byte(`{"status": 200}`)

  var result map[string]interface{}
  var decoder = json.NewDecoder(bytes.NewReader(data))
  decoder.UseNumber()

  if err := decoder.Decode(&result); err != nil {
    fmt.Println("error:", err)
    return
  }

  var status uint64
  if err := json.Unmarshal([]byte(result["status"].(json.Number).String()), &status); err != nil {
    fmt.Println("error:", err)
    return
  }

  fmt.Println("status value:",status)
}
```

选择四：使用结构体类型然后将JSON数字映射成你所需要的类型。

```go
package main

import (  
  "encoding/json"
  "bytes"
  "fmt"
)

func main() {  
  var data = []byte(`{"status": 200}`)

  var result struct {
    Status uint64 `json:"status"`
  }

  if err := json.NewDecoder(bytes.NewReader(data)).Decode(&result); err != nil {
    fmt.Println("error:", err)
    return
  }

  fmt.Printf("result => %+v",result)
  //prints: result => {Status:200}
}
```

选择五：使用一个结构体将JSON数据映射成`json.RawMessage`类型，这种方式对于JSON格式可能会变化要进行动态反序列化时很有用。

```go
package main

import (  
  "encoding/json"
  "bytes"
  "fmt"
)

func main() {  
  records := [][]byte{
    []byte(`{"status": 200, "tag":"one"}`),
    []byte(`{"status":"ok", "tag":"two"}`),
  }

  for idx, record := range records {
    var result struct {
      StatusCode uint64
      StatusName string
      Status json.RawMessage `json:"status"`
      Tag string             `json:"tag"`
    }

    if err := json.NewDecoder(bytes.NewReader(record)).Decode(&result); err != nil {
      fmt.Println("error:", err)
      return
    }

    var sstatus string
    if err := json.Unmarshal(result.Status, &sstatus); err == nil {
      result.StatusName = sstatus
    }

    var nstatus uint64
    if err := json.Unmarshal(result.Status, &nstatus); err == nil {
      result.StatusCode = nstatus
    }

    fmt.Printf("[%v] result => %+v\n",idx,result)
  }
}
```

### <a name="JSONStringValues"></a> JSON字符串值不允许使用16进制字符或其他非UTF8逃逸序列

> 级别: 中级初学者

> Go默认字符串值是UTF8编码的。这意味着你不能在JSON字符串中使用任意的16进制数据（且你也需要将反斜杠转义）。这是Go继承的一个JSON的坑，但由于它经常会在程序里面用到，因此很有必要做出说明。

```go
package main

import (
  "fmt"
  "encoding/json"
)

type config struct {
  Data string `json:"data"`
}

func main() {
  raw := []byte(`{"data":"\xc2"}`)
  var decoded config

  if err := json.Unmarshal(raw, &decoded); err != nil {
        fmt.Println(err)
    //prints: invalid character 'x' in string escape code
    }
  
}
```

如果Go发现有十六进制转义字符出现，Unmarshall/Decode调用会失败。如果你在字符串中确实需要使用反斜杠的话，请记住使用另一个反斜杠转义它。如果你确实需要在JSON中使用十六进制转义字符，使用两个反斜杠，然后自己完成后面十六进制的转义解码工作。

```go
package main

import (
  "fmt"
  "encoding/json"
)

type config struct {
  Data string `json:"data"`
}

func main() {
  raw := []byte(`{"data":"\\xc2"}`)
  
  var decoded config
  
  json.Unmarshal(raw, &decoded)
  
  fmt.Printf("%#v",decoded) //prints: main.config{Data:"\\xc2"}
  //todo: do your own hex escape decoding for decoded.Data  
}
```

还有一个选择是不要使用string，而是使用byte数组或者slice数据类型作为JSON的字段类型，但是二进制数据需要进行base64编码。

```go
package main

import (
  "fmt"
  "encoding/json"
)

type config struct {
  Data []byte `json:"data"`
}

func main() {
  raw := []byte(`{"data":"wg=="}`)
  var decoded config
  
  if err := json.Unmarshal(raw, &decoded); err != nil {
          fmt.Println(err)
      }
  
  fmt.Printf("%#v",decoded) //prints: main.config{Data:[]uint8{0xc2}}
}
```

还需要注意的是Unicode替代字符(U+FFFD)。Go会使用这个字符代替错误的UTF8字符，这样Unmarshall/Decode调用不会发生错误，但是你可能并没有得到想要的字符串。

### <a name="ComparingStructsArraysSlicesMaps"></a> 比较结构体、数组、slices和maps

> 级别： 中级初学者

> 你可以使用相等比较，==符号，来比较结构体，前提是结构体中每个字段都可以使用相等进行比较。

```go
package main

import "fmt"

type data struct {  
    num int
    fp float32
    complex complex64
    str string
    char rune
    yes bool
    events <-chan string
    handler interface{}
    ref *byte
    raw [10]byte
}

func main() {  
    v1 := data{}
    v2 := data{}
    fmt.Println("v1 == v2:",v1 == v2) //prints: v1 == v2: true
}
```

如果结构体的字段不能使用相等比较，编译会给出错误。注意数组可以比较仅当数据项可以比较。

```go
package main

import "fmt"

type data struct {  
    num int                //ok
    checks [10]func() bool //not comparable
    doit func() bool       //not comparable
    m map[string] string   //not comparable
    bytes []byte           //not comparable
}

func main() {  
    v1 := data{}
    v2 := data{}
    fmt.Println("v1 == v2:",v1 == v2)
}
```

Go给出了一些工具函数用来比较变量，如果这些变量不能使用比较符号进行运算时。

最通用的解决办法就是使用reflect包中DeepEqual()函数。

```go
package main

import (  
    "fmt"
    "reflect"
)

type data struct {  
    num int                //ok
    checks [10]func() bool //not comparable
    doit func() bool       //not comparable
    m map[string] string   //not comparable
    bytes []byte           //not comparable
}

func main() {  
    v1 := data{}
    v2 := data{}
    fmt.Println("v1 == v2:",reflect.DeepEqual(v1,v2)) //prints: v1 == v2: true

    m1 := map[string]string{"one": "a","two": "b"}
    m2 := map[string]string{"two": "b", "one": "a"}
    fmt.Println("m1 == m2:",reflect.DeepEqual(m1, m2)) //prints: m1 == m2: true

    s1 := []int{1, 2, 3}
    s2 := []int{1, 2, 3}
    fmt.Println("s1 == s2:",reflect.DeepEqual(s1, s2)) //prints: s1 == s2: true
}
```

除了比较慢之外，DeepEqual()也有它自己的坑。

```go
package main

import (  
    "fmt"
    "reflect"
)

func main() {  
    var b1 []byte = nil
    b2 := []byte{}
    fmt.Println("b1 == b2:",reflect.DeepEqual(b1, b2)) //prints: b1 == b2: false
}
```

DeepEqual()不会将一个空的slice和一个nil的slice当成是一样的。这个行为与bytes.Equal()函数不一致，bytes.Equal()认为nil和空的slice是相等的。

```go
package main

import (  
    "fmt"
    "bytes"
)

func main() {  
    var b1 []byte = nil
    b2 := []byte{}
    fmt.Println("b1 == b2:",bytes.Equal(b1, b2)) //prints: b1 == b2: true
}
```

DeepEqual()在比较slice的时候并不总是完美的。

```go
package main

import (  
    "fmt"
    "reflect"
    "encoding/json"
)

func main() {  
    var str string = "one"
    var in interface{} = "one"
    fmt.Println("str == in:",str == in,reflect.DeepEqual(str, in)) 
    //prints: str == in: true true

    v1 := []string{"one","two"}
    v2 := []interface{}{"one","two"}
    fmt.Println("v1 == v2:",reflect.DeepEqual(v1, v2)) 
    //prints: v1 == v2: false (not ok)

    data := map[string]interface{}{
        "code": 200,
        "value": []string{"one","two"},
    }
    encoded, _ := json.Marshal(data)
    var decoded map[string]interface{}
    json.Unmarshal(encoded, &decoded)
    fmt.Println("data == decoded:",reflect.DeepEqual(data, decoded)) 
    //prints: data == decoded: false (not ok)
}
```

如果你的byte slices（或者strings）包含文本数据，又需要进行大小写不敏感的比较时，你可能会尝试使用"bytes"和"strings"包中的ToUpper()或者ToLower()函数。这中方法对于英语有效，但对于大多数其他语言都无效。
你应该使用strings.EqualFold()和bytes.EqualFold()。

如果你的byte slices包含秘密（比如，加密的哈希值，令牌等），需要和用户提供的数据进行比较，这时不要使用reflect.DeepEqual()，bytes.Equal()或者bytes.Compare()。因为这些函数会导致你的程序容易受到时序攻击。为了避免泄露时序信息，你应该使用"crypto/subtle"包（如subtle.ConstantTimeCompare()）。

### <a name="RecoveringFromPanic"></a> 从Panic中恢复

> 级别: 中级初学者

> recover()函数可以用来捕获或阻止panic。但是仅当recover()被用在一个defer函数中才会生效。

错误:

```go
package main

import "fmt"

func main() {  
    recover() //doesn't do anything
    panic("not good")
    recover() //won't be executed :)
    fmt.Println("ok")
}
```

成功:

```go
package main

import "fmt"

func main() {  
    defer func() {
        fmt.Println("recovered:",recover())
    }()

    panic("not good")
}
```

recover()要生效必须直接在defer的函数中使用。

失败:

```go
package main

import "fmt"

func doRecover() {  
    fmt.Println("recovered =>",recover()) //prints: recovered => <nil>
}

func main() {  
    defer func() {
        doRecover() //panic is not recovered
    }()

    panic("not good")
}
```

### <a name="UpdatingReferencingItemValues"></a> 在slice，数组和map的range语法中更新或取址元素值

> 级别: 中级初学者

> 在range语法中产生的数据值是真实集合元素的拷贝。他们不会指向本来的元素。这意味着，更新这些值不会改变原始值。也意味着取这些值的地址不会真的指向原始元素。

```go
package main

import "fmt"

func main() {  
    data := []int{1,2,3}
    for _,v := range data {
        v *= 10 //original item is not changed
    }

    fmt.Println("data:",data) //prints data: [1 2 3]
}
```

如果你需要更新原始集合元素值，使用range返回的第一个index值来访问元素。

```go
package main

import "fmt"

func main() {  
    data := []int{1,2,3}
    for i,_ := range data {
        data[i] *= 10
    }

    fmt.Println("data:",data) //prints data: [10 20 30]
}
```

如果你的集合保存的就是指针那么规则稍微有点不同。你仍然需要使用index来将指针指向另外的值。但是如果你只是想改变指针指向的存储值的话，你可以直接使用range返回的第二个参数。

```go
package main

import "fmt"

func main() {  
    data := []*struct{num int} {{1},{2},{3}}

    for _,v := range data {
        v.num *= 10
    }

    fmt.Println(data[0],data[1],data[2]) //prints &{10} &{20} &{30}
}
```

### <a name="HiddenDataSlices"></a> slices中的隐藏数据

> 级别: 中级初学者

> 当你重新切片一个slice，新的slice只是原slice的一个引用。如果你忘记了这一点，你有可能会临时创建了一个很大的slice占用了内存，而实际上你只是用了其中的很小一部分。

```go
package main

import "fmt"

func get() []byte {  
    raw := make([]byte,10000)
    fmt.Println(len(raw),cap(raw),&raw[0]) //prints: 10000 10000 <byte_addr_x>
    return raw[:3]
}

func main() {  
    data := get()
    fmt.Println(len(data),cap(data),&data[0]) //prints: 3 10000 <byte_addr_x>
}
```

为了避免这种情况，确保你将你需要的数据从临时的slice中copy并返回，而不是重新切片。

```go
package main

import "fmt"

func get() []byte {  
    raw := make([]byte,10000)
    fmt.Println(len(raw),cap(raw),&raw[0]) //prints: 10000 10000 <byte_addr_x>
    res := make([]byte,3)
    copy(res,raw[:3])
    return res
}

func main() {  
    data := get()
    fmt.Println(len(data),cap(data),&data[0]) //prints: 3 3 <byte_addr_y>
}
```

### <a name="SliceDataCorruption"></a> 损坏的slice数据

> 级别: 中级初学者

> 我们想象一下，希望将一个存储在slice中的路径重新组合一下。比如你希望将第一个目录名称修改一下，然后重新组合其他的目录名称，来生成一个新的路径。

```go
package main

import (  
    "fmt"
    "bytes"
)

func main() {  
    path := []byte("AAAA/BBBBBBBBB")
    sepIndex := bytes.IndexByte(path,'/')
    dir1 := path[:sepIndex]
    dir2 := path[sepIndex+1:]
    fmt.Println("dir1 =>",string(dir1)) //prints: dir1 => AAAA
    fmt.Println("dir2 =>",string(dir2)) //prints: dir2 => BBBBBBBBB

    dir1 = append(dir1,"suffix"...)
    path = bytes.Join([][]byte{dir1,dir2},[]byte{'/'})

    fmt.Println("dir1 =>",string(dir1)) //prints: dir1 => AAAAsuffix
    fmt.Println("dir2 =>",string(dir2)) //prints: dir2 => uffixBBBB (not ok)

    fmt.Println("new path =>",string(path))
}
```

很显然，程序并没有按照你期望的方式运行。你想要获得结果"AAAAsuffix/BBBBBBBBB"，结果你却得到了"AAAAsuffix/uffixBBBB"。原因是slice中的所有目录元素其实都是指向的同一个数组结构。这意味着原始的路径也会被修改了。

这个问题可以采用分配一个新的slices然后复制你需要的数据过去来解决。还有一种办法是使用完整slice表达式。当使用了完整slice表达式后，因为可用空间cap不足，会强制分配一块新的空间，这样就不会造成数据覆盖。

```go
package main

import (  
    "fmt"
    "bytes"
)

func main() {  
    path := []byte("AAAA/BBBBBBBBB")
    sepIndex := bytes.IndexByte(path,'/')
    dir1 := path[:sepIndex:sepIndex] //full slice expression
    dir2 := path[sepIndex+1:]
    fmt.Println("dir1 =>",string(dir1)) //prints: dir1 => AAAA
    fmt.Println("dir2 =>",string(dir2)) //prints: dir2 => BBBBBBBBB

    dir1 = append(dir1,"suffix"...)
    path = bytes.Join([][]byte{dir1,dir2},[]byte{'/'})

    fmt.Println("dir1 =>",string(dir1)) //prints: dir1 => AAAAsuffix
    fmt.Println("dir2 =>",string(dir2)) //prints: dir2 => BBBBBBBBB (ok now)

    fmt.Println("new path =>",string(path))
}
```

### <a name="StaleSlices"></a> 过时的slice

> 级别: 中级初学者

> 多个slices可以指向同一个数据存储区域。这会造成如果你从一个已经存在的slice当中创建了一个slice，而你的程序又依赖于数据的正确性的话，你需要担心slice数据是否已经过时。

> 在一些情况下，为slices增加一些数据可能会导致一个新的内存空间的分配，然而你的slice却仍然指向原来的数组数据。

```go
import "fmt"

func main() {  
    s1 := []int{1,2,3}
    fmt.Println(len(s1),cap(s1),s1) //prints 3 3 [1 2 3]

    s2 := s1[1:]
    fmt.Println(len(s2),cap(s2),s2) //prints 2 2 [2 3]

    for i := range s2 { s2[i] += 20 }

    //still referencing the same array
    fmt.Println(s1) //prints [1 22 23]
    fmt.Println(s2) //prints [22 23]

    s2 = append(s2,4)

    for i := range s2 { s2[i] += 10 }

    //s1 is now "stale"
    fmt.Println(s1) //prints [1 22 23]
    fmt.Println(s2) //prints [32 33 14]
}
```

### <a name="TypeDeclarationsMethods"></a> 类型声明和方法

> 级别: 中等初学者

> 当你从一个已有的类型（非接口）中创建了一个类型声明，你并不会继承这个类型的方法。

失败:

```go
package main

import "sync"

type myMutex sync.Mutex

func main() {  
    var mtx myMutex
    mtx.Lock() //error
    mtx.Unlock() //error  
}
```

编译错误:

    /tmp/sandbox106401185/main.go:9: mtx.Lock undefined (type myMutex has no field or method Lock) /tmp/sandbox106401185/main.go:10: mtx.Unlock undefined (type myMutex has no field or method Unlock)

如果你确实需要原类型的方法的话，你可以定义一个新的结构体，然后将原类型作为一个匿名字段放在结构体内。

成功:

```go
package main

import "sync"

type myLocker struct {  
    sync.Mutex
}

func main() {  
    var lock myLocker
    lock.Lock() //ok
    lock.Unlock() //ok
}
```

### <a name="InterfaceRetainMethodSets"></a> 实际类型转为接口类型时仍然会保留它们的方法

成功:

```go
package main

import "sync"

type myLocker sync.Locker

func main() {  
    var lock myLocker = new(sync.Mutex)
    lock.Lock() //ok
    lock.Unlock() //ok
}
```

### <a name="BreakingOutSwitchSelect"></a> 跳出for switch和for select结构块

> 级别: 中级初学者

> 使用break只会让你跳出最内层的switch/select结构体。如果无法使用return的话，在最外层循环加一个标签然后使用带标签的break会是次优的选择。

```go
package main

import "fmt"

func main() {  
    loop:
        for {
            switch {
            case true:
                fmt.Println("breaking out...")
                break loop
            }
        }

    fmt.Println("out!")
}
```

当然，"goto"语句也可以完成这一点。

### <a name="IterationVariablesClosuresFor"></a> for结构内的迭代变量和闭包

> 级别: 中级初学者

> 这是Go当中最常见的坑了。迭代变量在for循环中每次都会重复使用。这意味着每个在for循环中的闭包（内部函数）都会指向同一个变量值，这会造成实际闭包在goroutine中执行时，使用的是当前循环时迭代变量的值。

失败:

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    data := []string{"one","two","three"}

    for _,v := range data {
        go func() {
            fmt.Println(v)
        }()
    }

    time.Sleep(3 * time.Second)
    //goroutines print: three, three, three
}
```

最简单的解决方案（不需要对goroutine进行任何修改）是将当前的迭代变量保存到另一个变量当中。

成功:

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    data := []string{"one","two","three"}

    for _,v := range data {
        vcopy := v //
        go func() {
            fmt.Println(vcopy)
        }()
    }

    time.Sleep(3 * time.Second)
    //goroutines print: one, two, three
}
```

另外一个解决方案是将当前的迭代变量作为参数代入到goroutine的闭包中执行。

成功:

```go
package main

import (  
    "fmt"
    "time"
)

func main() {  
    data := []string{"one","two","three"}

    for _,v := range data {
        go func(in string) {
            fmt.Println(in)
        }(v)
    }

    time.Sleep(3 * time.Second)
    //goroutines print: one, two, three
}
```

下面有一个这个坑复杂一点的表现方式。

失败:

```go
package main

import (  
    "fmt"
    "time"
)

type field struct {  
    name string
}

func (p *field) print() {  
    fmt.Println(p.name)
}

func main() {  
    data := []field{{"one"},{"two"},{"three"}}

    for _,v := range data {
        go v.print()
    }

    time.Sleep(3 * time.Second)
    //goroutines print: three, three, three
}
```

成功:

```go
package main

import (  
    "fmt"
    "time"
)

type field struct {  
    name string
}

func (p *field) print() {  
    fmt.Println(p.name)
}

func main() {  
    data := []field{{"one"},{"two"},{"three"}}

    for _,v := range data {
        v := v
        go v.print()
    }

    time.Sleep(3 * time.Second)
    //goroutines print: one, two, three
}
```

你认为下面的代码会输出什么？为什么？

```go
package main

import (  
    "fmt"
    "time"
)

type field struct {  
    name string
}

func (p *field) print() {  
    fmt.Println(p.name)
}

func main() {  
    data := []*field{{"one"},{"two"},{"three"}}

    for _,v := range data {
        go v.print()
    }

    time.Sleep(3 * time.Second)
}
```

### <a name="DeferredFunctionCallArgumentEvaluation"></a> defer函数调用参数定值

> 级别: 中级初学者

> defer函数的调用参数值是在语句被评估定值时就确定的，而不是在真正执行defer函数时才确定。这个规则在当你defer函数被评估定值时，结构体的值和函数内部的变量值也会直接保存并等待执行。

```go
package main

import "fmt"

func main() {  
    var i int = 1

    defer fmt.Println("result =>",func() int { return i * 2 }())
    i++
    //prints: result => 2 (not ok if you expected 4)
}
```

如果defer函数使用的是指针类型的参数，那么你可以在defer函数评估定值和真实执行之间改变这个指针指向的数据值。

```go
package main

import (
  "fmt"
)

func main() {
  i := 1
  defer func (in *int) { fmt.Println("result =>", *in) }(&i)
  
  i = 2
  //prints: result => 2
}
```

### <a name="DeferredFunctionCallExecution"></a> defer函数的执行

> 级别: 中级初学者

> defer函数是在包含defer语句的函数的结尾执行（如果有多个，按照逆序），不是在包含defer语句的代码块的末尾执行。对于Go初学者来说，很容易混淆defer语句的执行规则和变量的作用域。这对于如果函数中有一个很长的for循环然后试图在每次循环中defer方式来释放资源的时候，尤其需要注意。

```go
package main

import (  
    "fmt"
    "os"
    "path/filepath"
)

func main() {  
    if len(os.Args) != 2 {
        os.Exit(-1)
    }

    start, err := os.Stat(os.Args[1])
    if err != nil || !start.IsDir(){
        os.Exit(-1)
    }

    var targets []string
    filepath.Walk(os.Args[1], func(fpath string, fi os.FileInfo, err error) error {
        if err != nil {
            return err
        }

        if !fi.Mode().IsRegular() {
            return nil
        }

        targets = append(targets,fpath)
        return nil
    })

    for _,target := range targets {
        f, err := os.Open(target)
        if err != nil {
            fmt.Println("bad target:",target,"error:",err) //prints error: too many open files
            break
        }
        defer f.Close() //will not be closed at the end of this code block
        //do something with the file...
    }
}
```

一种解决办法就是将循环体的语句块封装成一个函数。

```go
package main

import (  
    "fmt"
    "os"
    "path/filepath"
)

func main() {  
    if len(os.Args) != 2 {
        os.Exit(-1)
    }

    start, err := os.Stat(os.Args[1])
    if err != nil || !start.IsDir(){
        os.Exit(-1)
    }

    var targets []string
    filepath.Walk(os.Args[1], func(fpath string, fi os.FileInfo, err error) error {
        if err != nil {
            return err
        }

        if !fi.Mode().IsRegular() {
            return nil
        }

        targets = append(targets,fpath)
        return nil
    })

    for _,target := range targets {
        func() {
            f, err := os.Open(target)
            if err != nil {
                fmt.Println("bad target:",target,"error:",err)
                return
            }
            defer f.Close() //ok
            //do something with the file...
        }()
    }
}
```

另外一种解决办法就是避免在这种情况下使用defer语句。

### <a name="FailedTypeAssertions"></a> 失败的类型断言

> 级别: 中级初学者

> 如果一个类型断言失败了，会返回目标类型的“零值”。如果这时你使用了同名变量遮盖了原变量的话，可能会造成无法意料的行为。

失败:

```go
package main

import "fmt"

func main() {  
    var data interface{} = "great"

    if data, ok := data.(int); ok {
        fmt.Println("[is an int] value =>",data)
    } else {
        fmt.Println("[not an int] value =>",data) 
        //prints: [not an int] value => 0 (not "great")
    }
}
```

成功:

```go
package main

import "fmt"

func main() {  
    var data interface{} = "great"

    if res, ok := data.(int); ok {
        fmt.Println("[is an int] value =>",res)
    } else {
        fmt.Println("[not an int] value =>",data) 
        //prints: [not an int] value => great (as expected)
    }
}
```

### <a name="BlockedGoroutinesResourceLeaks"></a> 阻塞的goroutine和资源泄露

> 级别: 中级初学者

> Rob Pike在Google I/O 2012 上做了"Go并发模式"的讲演，其中谈到了许多基础的并发模式。在众多目标中取回第一个结果是其中之一。

```go
func First(query string, replicas ...Search) Result {  
    c := make(chan Result)
    searchReplica := func(i int) { c <- replicas[i](query) }
    for i := range replicas {
        go searchReplica(i)
    }
    return <-c
}
```

这个函数对于每个搜索副本都会开启一个goroutine。每个goroutine会将它搜索到的结果发送给result channel。然后函数返回channel中的第一个结果。

其他goroutine的结果呢？其他goroutine本身呢？

因为上述函数中，result channel是非缓冲的。这会造成只有一个goroutine能够返回结果并写入result channel，其他所有的goroutine都会被阻塞。这意味着如果副本数大于1，每次调用这个函数都会造成资源泄露。

为了避免资源泄露，也就是保证每个goroutine都能正常退出。一个可能的做法就是使用一个缓冲的result channel，channel的大小足够放下所有的结果。

```go
func First(query string, replicas ...Search) Result {  
    c := make(chan Result,len(replicas))
    searchReplica := func(i int) { c <- replicas[i](query) }
    for i := range replicas {
        go searchReplica(i)
    }
    return <-c
}
```

还有一种可能的做法是使用一个select语句带有default分支以及一个缓冲的result channel但是只能保存一个结果。default分支可以保证那些goroutine在result channel已经不能接收消息的时候不会被堵塞。

```go
func First(query string, replicas ...Search) Result {  
    c := make(chan Result,1)
    searchReplica := func(i int) { 
        select {
        case c <- replicas[i](query):
        default:
        }
    }
    for i := range replicas {
        go searchReplica(i)
    }
    return <-c
}
```

你还可以使用一个特殊的取消通道(cancellation channel)来中断goroutine。

```go
func First(query string, replicas ...Search) Result {  
    c := make(chan Result)
    done := make(chan struct{})
    defer close(done)
    searchReplica := func(i int) { 
        select {
        case c <- replicas[i](query):
        case <- done:
        }
    }
    for i := range replicas {
        go searchReplica(i)
    }

    return <-c
}
```

为什么那个讲演有这个bug？Rob Pike仅仅是不希望复杂化他的幻灯片，这是说得过去的。但是如果一个Go新手开发者直接使用这段代码却没有想到存在的问题的话，可能会有麻烦。

### <a name="SameAddressDifferentZeroSizedVariables"></a> 不同的零长度变量使用同样的地址

> 级别: 中级初学者

> 如果你有两个不同的变量，你可能会认为它们理所当然应该有两个不同的地址。额，Go里面不是这样的。如果这两个变量都是零长度的变量，它们有可能共享相同的内存地址。

```go
package main

import (
  "fmt"
)

type data struct {
}

func main() {
  a := &data{}
  b := &data{}
  
  if a == b {
    fmt.Printf("same address - a=%p b=%p\n",a,b)
    //prints: same address - a=0x1953e4 b=0x1953e4
  }
}
```

### <a name="iotaDoesntAlwaysStartZero"></a> 第一次使用的iota不总是从0开始的

> 级别: 中级初学者

> iota修饰符看起来像一个递增的操作符。你定义一个常量结构块，第一次使用了iota会得到0，然后得到1，以此类推。但是这不一定都是对的。

```go
package main

import (
  "fmt"
)

const (
  azero = iota
  aone  = iota
)

const (
  info  = "processing"
  bzero = iota
  bone  = iota
)

func main() {
  fmt.Println(azero,aone) //prints: 0 1
  fmt.Println(bzero,bone) //prints: 1 2
}
```

实际上iota是常量结构块中的行序号，因此如果你首先使用的iota并不在第一行的话，那它的初始值就不是0。

### <a name="UsingPointerReceiverMethodsOnValue"></a> 在指针类型接收者的方法中使用了值

> 级别: 高级初学者

> 如果值是能够支持取址操作的话，在一个指针类型接收者的方法当中使用值是没问题的。换句话说，在这种情况下，你不需要定义一个使用值接收者的相同方法。

> 但是不是所有变量都可以取址的。map元素是不能取址的。通过接口指向的变量也是不能取址的。

```go
package main

import "fmt"

type data struct {  
    name string
}

func (p *data) print() {  
    fmt.Println("name:",p.name)
}

type printer interface {  
    print()
}

func main() {  
    d1 := data{"one"}
    d1.print() //ok

    var in printer = data{"two"} //error
    in.print()

    m := map[string]data {"x":data{"three"}}
    m["x"].print() //error
}
```

编译错误:

    /tmp/sandbox017696142/main.go:21: cannot use data literal (type data) as type printer in assignment: data does not implement printer (print method has pointer receiver)
    /tmp/sandbox017696142/main.go:25: cannot call pointer method on m["x"] /tmp/sandbox017696142/main.go:25: cannot take the address of m["x"]

### <a name="UpdatingMapValueFields"></a> 更新map的值字段

> 级别: 高级开发者

> 如果你有一个值是结构体的map，你不能单独更新其中的结构体字段。

失败:

```go
package main

type data struct {  
    name string
}

func main() {  
    m := map[string]data {"x":{"one"}}
    m["x"].name = "two" //error
}
```

编译错误:

    /tmp/sandbox380452744/main.go:9: cannot assign to m["x"].name

这是没法编译通过的，原因是map的元素是不能取址的。

还有一个更加让Go的新手感到困惑的是，slice的元素是可以取址的。

```go
package main

import "fmt"

type data struct {  
    name string
}

func main() {  
    s := []data {{"one"}}
    s[0].name = "two" //ok
    fmt.Println(s)    //prints: [{two}]
}
```

值得注意的是，之前有过一个Go编译器（gccgo）是支持更新map元素字段的，不过很快就被修正了。这也曾经被认为是Go 1.3的一个可能特性。但是由于重要性不够，似乎不太可能短时间内支持，因此这个特性依然在todo list上。

第一种办法是使用一个临时变量。

```go
package main

import "fmt"

type data struct {  
    name string
}

func main() {  
    m := map[string]data {"x":{"one"}}
    r := m["x"]
    r.name = "two"
    m["x"] = r
    fmt.Printf("%v",m) //prints: map[x:{two}]
}
```

另一种办法是将结构体指针作为map的值。

```go
package main

import "fmt"

type data struct {  
    name string
}

func main() {  
    m := map[string]*data {"x":{"one"}}
    m["x"].name = "two" //ok
    fmt.Println(m["x"]) //prints: &{two}
}
```

接下来，如果运行下面的代码会发生什么？

```go
package main

type data struct {  
    name string
}

func main() {  
    m := map[string]*data {"x":{"one"}}
    m["z"].name = "what?" //???
}
```

### <a name="nilInterfacesAndValues"></a> nil接口和nil接口的值

> 级别: 高级初学者

> 这是Go当中第二容易踩的坑因为接口不是指针，虽然它们看起来很像指针。接口的值是nil当且仅当它的类型和值字段都是nil。

> 接口的类型和值字段是用创建该接口变量的实际变量相应的类型和值进行填充的。当你对接口变量进行nil判断时，常常会导致意料不到的结果。

```go
package main

import "fmt"

func main() {  
    var data *byte
    var in interface{}

    fmt.Println(data,data == nil) //prints: <nil> true
    fmt.Println(in,in == nil)     //prints: <nil> true

    in = data
    fmt.Println(in,in == nil)     //prints: <nil> false
    //'data' is 'nil', but 'in' is not 'nil'
}
```

当你有函数返回一个接口的时候要特别注意这个陷阱。

失败:

```go
package main

import "fmt"

func main() {  
    doit := func(arg int) interface{} {
        var result *struct{} = nil

        if(arg > 0) {
            result = &struct{}{}
        }

        return result
    }

    if res := doit(-1); res != nil {
        fmt.Println("good result:",res) //prints: good result: <nil>
        //'res' is not 'nil', but its value is 'nil'
    }
}
```

成功:

```go
package main

import "fmt"

func main() {  
    doit := func(arg int) interface{} {
        var result *struct{} = nil

        if(arg > 0) {
            result = &struct{}{}
        } else {
            return nil //return an explicit 'nil'
        }

        return result
    }

    if res := doit(-1); res != nil {
        fmt.Println("good result:",res)
    } else {
        fmt.Println("bad result (res is nil)") //here as expected
    }
}
```

### <a name="StackHeapVariables"></a> 栈和堆变量

> 级别: 高级

> 在Go中，你不能知道变量是在栈区还是在堆区创建的。在`C++`中使用new操作符创建的变量都会分配在堆区中。但在Go中，编译器决定变量分配在哪里，即使你使用了new()或者make()函数来创建变量。编译器基于变量的大小以及对于变量的"失效分析"情况来决定分配的位置。这意味着在Go中你可以安全的返回一个函数内部变量的引用，通常这种行为在其他语言如`C++`当中是不允许的。

> 如果你需要知道你的变量实在哪里分配的，在使用"go build"或"go run"的时候加上"-m"的gcflags参数（如，go run -gcflags -m app.go）。

### <a name="GOMAXPROCSConcurrencyParallelism"></a> GOMAXPROCS设置，并发和并行

> 级别: 高级初学者

> Go 1.4及之前版本使用了一个执行上下文/操作系统线程。这意味着任意时间只能有一个goroutine得到执行。从1.5开始Go将逻辑CPU核数作为线程数，这个数值可以通过runtime.NumCPU()返回得到。这个数值可能或不一定与你的CPU核数相同。这取决于在这个进程上CPU的affinity设置。你可以通过GOMAXPROCS环境变量的修改这个设置，或者通过runtime.GOMAXPROCS()函数来设置。

这里有一个通常的误解，认为GOMAXPROCS代表着Go用来执行goroutine的CPU个数。而且runtime.GOMAXPROCS()函数的文档增加了这种误会。反而[GOMAXPROCS变量](https://golang.org/pkg/runtime/)对于操作系统线程进行了很好的解释。

你可以设置GOMAXPROCS为超过CPU核数的一个值。在1.10版中，这个值是没有限制的。之前最大的GOMAXPROCS值为256，并且在1.9版中升至1024。

```go
package main

import (  
    "fmt"
    "runtime"
)

func main() {  
    fmt.Println(runtime.GOMAXPROCS(-1)) //prints: X (1 on play.golang.org)
    fmt.Println(runtime.NumCPU())       //prints: X (1 on play.golang.org)
    runtime.GOMAXPROCS(20)
    fmt.Println(runtime.GOMAXPROCS(-1)) //prints: 20
    runtime.GOMAXPROCS(300)
    fmt.Println(runtime.GOMAXPROCS(-1)) //prints: 256
}
```

### <a name="ReadWriteOperationReordering"></a> 读写操作重排序

> 级别: 高级初学者

> Go会重新排序某些操作，但是它会保证在goroutine中这些操作的总结果不会发生改变。然而，这不会保证在多个goroutine中的执行顺序。

```go
package main

import (  
    "runtime"
    "time"
)

var _ = runtime.GOMAXPROCS(3)

var a, b int

func u1() {  
    a = 1
    b = 2
}

func u2() {  
    a = 3
    b = 4
}

func p() {  
    println(a)
    println(b)
}

func main() {  
    go u1()
    go u2()
    go p()
    time.Sleep(1 * time.Second)
}
```

如果你执行这段代码多次的话，你可能会看到下面这些a和b变量值的组合。

    1 
    2
    
    3 
    4
    
    0 
    2
    
    0 
    0
    
    1 
    4

最有趣的组合是"02"。它代表这b在a之前被更新了。

如果你需要保留多个goroutine中读和写的操作的顺序，你将需要使用channels或者相应的"sync"包中的方法。

### <a name="PreemptiveScheduling"></a> 先发制人的调度

> 级别: 高级初学者

> 实际上存在着一种可能的流氓goroutine会阻止其他goroutine运行。这种情况在你有一个for循环并不允许调度器运行的情况下就可能发生。

```go
package main

import "fmt"

func main() {  
    done := false

    go func(){
        done = true
    }()

    for !done {
    }
    fmt.Println("done!")
}
```

这里的for循环不一定要是空的，还有很多非空的情况也会产生同样结果。它会造成代码永远无法触发goroutine调度器的执行。

调度器会在GC，go语句，阻塞的channel操作，阻塞的系统调用和锁操作之后执行。它也会在调用一个行内函数之后得到执行。

```go
package main

import "fmt"

func main() {  
    done := false

    go func(){
        done = true
    }()

    for !done {
        fmt.Println("not done!") //not inlined
    }
    fmt.Println("done!")
}
```

为了找出一个函数调用是否inline调用，你可以在go build或go run的时候使用gcflags的-m参数(例如，go build -gcflags -m)。

另外一个选择是明确调用调度器执行。你可以使用"runtime"包中的Gosched()函数来执行调度器。

```go
package main

import (  
    "fmt"
    "runtime"
)

func main() {  
    done := false

    go func(){
        done = true
    }()

    for !done {
        runtime.Gosched()
    }
    fmt.Println("done!")
}
```

注意上述代码会有竞争。这里只是为了说明调度的坑。

### <a name="ImportCMultilineImportBlocks"></a> 导入C库和多行导入代码块

> 级别: Cgo

> 当你使用Cgo的时候，你需要导入C包。你可以使用单行的导入或在一个多行的import代码块中带入。

```go
package main

/*
#include <stdlib.h>
*/
import (
  "C"
)

import (
  "unsafe"
)

func main() {
  cs := C.CString("my go string")
  C.free(unsafe.Pointer(cs))
}
```

但是如果你将其他包放入到C包的import代码块中时，会编译不通过。

```go
package main

/*
#include <stdlib.h>
*/
import (
  "C"
  "unsafe"
)

func main() {
  cs := C.CString("my go string")
  C.free(unsafe.Pointer(cs))
}
```

编译错误:

    ./main.go:13:2: could not determine kind of name for C.free

### <a name="NoblanklinesBetweenImportCandCgoComments"></a> C包的import语句和Cgo的注释之间不能有空行

> 级别: Cgo

> Cgo的第一个坑就在于Cgo的注释和import "C"语句之间不能有空行

```go
package main

/*
#include <stdlib.h>
*/

import "C"

import (
  "unsafe"
)

func main() {
  cs := C.CString("my go string")
  C.free(unsafe.Pointer(cs))
}
```

编译错误:

    ./main.go:15:2: could not determine kind of name for C.free

确认import "C"语句之上不要有任何空行。

### <a name="CantCallCFunctionswithVariableArguments"></a> 不能调用C中的可变参数函数

> 级别: Cgo

> 你无法调用C中可变参数的函数。

```go
package main

/*
#include <stdio.h>
#include <stdlib.h>
*/
import "C"

import (
  "unsafe"
)

func main() {
  cstr := C.CString("go")
  C.printf("%s\n",cstr) //not ok
  C.free(unsafe.Pointer(cstr))
}
```

编译错误:

    ./main.go:15:2: unexpected type: ...

你必须将可变参数的C函数包装成一个已知参数个数的函数来使用。

```go
package main

/*
#include <stdio.h>
#include <stdlib.h>

void out(char* in) {
  printf("%s\n", in);
}
*/
import "C"

import (
  "unsafe"
)

func main() {
  cstr := C.CString("go")
  C.out(cstr) //ok
  C.free(unsafe.Pointer(cstr))
}
```
