完整文章：
https://tech.meituan.com/innodb-lock.html

隔离级别 	                脏读（Dirty Read） 	不可重复读（NonRepeatable Read） 	幻读（Phantom Read）
未提交读（Read uncommitted） 	可能 	                  可能 	                       可能
已提交读（Read committed） 	不可能 	                  可能 	                       可能
可重复读（Repeatable read） 	不可能 	                  不可能 	                   可能
可串行化（Serializable ） 	不可能 	                  不可能 	                   不可能



###Repeatable Read（可重读）
这是MySQL中InnoDB默认的隔离级别。
MySQL、ORACLE、PostgreSQL等成熟的数据库，出于性能考虑，都是使用了以乐观锁为理论基础的MVCC（多版本并发控制）来避免不可重复读和幻读。

#悲观锁
在悲观锁的情况下，为了保证事务的隔离性，就需要一致性锁定读。读取数据时给加锁，其它事务无法修改这些数据。修改删除数据时也要加锁，其它事务无法读取这些数据。

#乐观锁
在InnoDB中，会在每行数据后添加两个额外的隐藏的值来实现MVCC，这两个值一个记录这行数据何时被创建，另外一个记录这行数据何时过期（或者被删除）。


对于这种读取历史数据的方式，我们叫它快照读 (snapshot read)，而读取数据库当前版本数据的方式，叫当前读 (current read)。很显然，在MVCC中：

    快照读：就是select
        select * from table ....;
    当前读：特殊的读操作，插入/更新/删除操作，属于当前读，处理的都是当前的数据，需要加锁。
        select * from table where ? lock in share mode;
        select * from table where ? for update;
        insert;
        update ;
        delete;

事务的隔离级别实际上都是定义了当前读的级别，MySQL为了减少锁处理（包括等待其它锁）的时间，提升并发能力，引入了快照读的概念，使得select不用加锁。
而update、insert这些“当前读”，就需要另外的模块来解决了。

为了解决当前读中的幻读问题，MySQL事务使用了Next-Key锁。
####Next-Key锁
Next-Key锁是行锁和GAP（间隙锁）的合并


行锁防止别的事务修改或删除，GAP锁防止别的事务新增，行锁和GAP锁结合形成的的Next-Key锁共同解决了RR级别在写数据时的幻读问题。


不要看到select就说不会加锁了，在Serializable这个级别，还是会加锁的。

大批量update操作的where条件的字段必须建立索引，否则会有很大的性能问题。
#行级锁情况
MySQL会给整张表的所有数据行的加行锁。
这里听起来有点不可思议，但是当sql运行的过程中，MySQL并不知道哪些数据行是 class_name = '初三一班'的（没有索引嘛），
如果一个条件无法通过索引快速过滤，存储引擎层面就会将所有记录加锁后返回，再由MySQL Server层进行过滤。
但在实际使用过程当中，MySQL做了一些改进，在MySQL Server过滤条件，发现不满足后，会调用unlock_row方法，把不满足条件的记录释放锁 (违背了二段锁协议的约束)。
这样做，保证了最后只会持有满足条件记录上的锁，但是每条记录的加锁操作还是不能省略的。可见即使是MySQL，为了效率也是会违反规范的。（参见《高性能MySQL》中文第三版p181）
#GAP（间隙锁）情况
如果使用的是没有索引的字段，则给全表加入gap锁，
同时，它不能像上文中行锁一样经过MySQL Server过滤自动解除不满足条件的锁，
因为没有索引，则这些字段也就没有排序，也就没有区间。
除非该事务提交，否则其它事务无法插入任何数据。
