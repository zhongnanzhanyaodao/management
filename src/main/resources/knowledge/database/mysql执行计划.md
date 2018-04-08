#EXPLAIN  sql
例子：EXPLAIN select * from emp;

http://www.cnblogs.com/ggjucheng/archive/2012/11/11/2765237.html


type

表示MySQL在表中找到所需行的方式，又称“访问类型”，常见类型如下：

ALL--index--range--ref--eq_ref--const,system--NULL

由左至右，由最差到最好
a.ALL：Full Table Scan， MySQL将遍历全表以找到匹配的行
b.index：Full Index Scan，index与ALL区别为index类型只遍历索引树
c.range：索引范围扫描，对索引的扫描开始于某一点，返回匹配值域的行，常见于between、<、>等的查询
d.ref：非唯一性索引扫描，返回匹配某个单独值的所有行。常见于使用非唯一索引即唯一索引的非唯一前缀进行的查找
e.eq_ref：唯一性索引扫描，对于每个索引键，表中只有一条记录与之匹配。常见于主键或唯一索引扫描
f.const、system：当MySQL对查询某部分进行优化，并转换为一个常量时，使用这些类型访问。如将主键置于where列表中，MySQL就能将该查询转换为一个常量，system是const类型的特例，当查询的表只有一行的情况下， 使用system
g.NULL：MySQL在优化过程中分解语句，执行时甚至不用访问表或索引