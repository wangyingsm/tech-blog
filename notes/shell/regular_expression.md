# BRE ERE PCRE

## BRE

Basic Regular Expression

目前一般使用GNU BRE，其规则如下：

- 遵循POSIX标准
- 某些特殊字符需要转义，包括 `{, }, (, ), +, ?, |` ，共7个
- 不需要转义的特殊字符包括 `., \, [, ], ^, $, *`

例如：

```shell
# 匹配cat, caat, caaat
grep 'ca\{1,3\}t'

# 匹配ca{1,3}t
grep 'ca{1,3}t

# 匹配ct, cat, caat, ...
grep 'ca*t'

# 匹配cat, caat, caaat, ...
grep 'ca\+t'

# 匹配ca+t
grep 'ca+t'

# 匹配ca+t, ca++t, ca+++t, ...
grep 'ca+\+t'

# 匹配ct, c0t, c1t, ...
grep 'c[0-9]t'

# 匹配任意完整8位数
grep '[1-9][0-9]\{7\}'

# 匹配一整行为cat或dog
grep '^\(cat\)\|\(dog\)$'
```

## ERE

Extension Regular Expression

目前一般也是使用GNU ERE，所有的特殊字符都不需要转义。如使用grep，需要带-E参数，或者用egrep。

例如：

```shell
# 匹配cat, caat, caaat
egrep 'ca{1,3}t'

# 匹配ca{1,3}t
egrep 'ca\{1,3\}t'

# 匹配ct, cat, caat, ...
egrep 'ca*t'

# 匹配cat, caat, caaat, ...
egrep 'ca+t'

# 匹配ca+t
egrep 'ca\+t'

# 匹配ca+t, ca++t, ca+++t, ...
egrep 'ca\++t'

# 匹配ct, c0t, c1t, ...
egrep 'c[0-9]t'

# 匹配任意完整8位数
egrep '[1-9][0-9]{7}'

# 匹配一整行为cat或dog
egrep '^(cat|dog)$'
```

## PCRE

Perl Compatible Regular Expression

来源于Perl语言，也是目前编程语言中最流行的正则表达式。除了上述特殊字符外，还提供了许多转义特殊字符供使用。

| 转义符号 | 说明 |
| - | - |
| \d | 任何数字 |
| \D | 任何非数字 |
| \h | 任何水平方向空白字符 |
| \H | 任何非水平方向空白字符 |
| \s | 任何空白字符 |
| \S | 任何非空白字符 |
| \v | 任何垂直方向空白字符 |
| \V | 任何非垂直方向空白字符 |
| \w | 任何单词字符，包括下划线、字母或数字 |
| \W | 任何非单词字符 |
