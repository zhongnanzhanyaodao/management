#EXPLAIN  sql
例子：EXPLAIN select * from emp;

http://www.cnblogs.com/ggjucheng/archive/2012/11/11/2765237.html


type

表示MySQL在表中找到所需行的方式，又称“访问类型”，常见类型如下：

ALL--index--range--ref--eq_ref--const,system--NULL

由左至右，由最差到最好