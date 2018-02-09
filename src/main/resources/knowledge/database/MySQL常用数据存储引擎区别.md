mysql有多种存储引擎，目前常用的是 MyISAM 和 InnoDB 这两个引擎，除了这两个引擎以为还有许多其他引擎，有官方的，也有一些公司自己研发的。这篇文章主要简单概述一下常用常见的 MySQL 引擎，一则这是面试中常被问到的问题，二则这也是数据库设计中不可忽略的问题，用合适的引擎可以更好的适应业务场景，提高业务效率。

#MyISAM
MyISAM 是 mysql 5.5.5 之前的默认引擎，它支持 B-tree/FullText/R-tree 索引类型。
锁级别为表锁，表锁优点是开销小，加锁快；缺点是锁粒度大，发生锁冲动概率较高，容纳并发能力低，这个引擎适合查询为主的业务。
此引擎不支持事务，也不支持外键。
MyISAM强调了快速读取操作。它存储表的行数，于是SELECT COUNT(*) FROM TABLE时只需要直接读取已经保存好的值而不需要进行全表扫描。

#InnoDB
InnoDB 存储引擎最大的亮点就是支持事务，支持回滚，它支持 Hash/B-tree 索引类型。
锁级别为行锁，行锁优点是适用于高并发的频繁表修改，高并发是性能优于 MyISAM。缺点是系统消耗较大，索引不仅缓存自身，也缓存数据，相比 MyISAM 需要更大的内存。
InnoDB 中不保存表的具体行数，也就是说，执行 select count(*) from table时，InnoDB 要扫描一遍整个表来计算有多少行。
支持事务，支持外键。

#Memory
Memory 是内存级别存储引擎，数据存储在内存中，所以他能够存储的数据量较小。
因为内存的特性，存储引擎对数据的一致性支持较差。锁级别为表锁，不支持事务。但访问速度非常快，并且默认使用 hash 索引。
Memory存储引擎使用存在内存中的内容来创建表，每个Memory表只实际对应一个磁盘文件，在磁盘中表现为.frm文件。

#总结

存储结构:
 	MyISAM:
 	每张表被存放在三个文件：frm-格定义,MYD(MYData)-数据文件,MYI(MYIndex)-索引文件
 	InnoDB:
 	所有的表都保存在同一个数据文件中（也可能是多个文件，或者是独立的表空间文件），InnoDB表的大小只受限于操作系统文件的大小，一般为2GB
存储空间:
 	MyISAM:
    MyISAM可被压缩，存储空间较小
 	InnoDB:
 	InnoDB的表需要更多的内存和存储，它会在主内存中建立其专用的缓冲池用于高速缓冲数据和索引
可移植性、备份及恢复:
 	MyISAM:
 	由于MyISAM的数据是以文件的形式存储，所以在跨平台的数据转移中会很方便。在备份和恢复时可单独针对某个表进行操作
 	InnoDB:
 	免费的方案可以是拷贝数据文件、备份 binlog，或者用 mysqldump，在数据量达到几十G的时候就相对痛苦了
事务安全:
 	MyISAM:
    不支持 每次查询具有原子性
 	InnoDB:
 	支持,具有事务(commit)、回滚(rollback)和崩溃修复能力(crash recovery capabilities)的事务安全(transaction-safe (ACID compliant))型表
AUTO_INCREMENT:
 	MyISAM:
    MyISAM表可以和其他字段一起建立联合索引
 	InnoDB:
 	InnoDB中必须包含只有该字段的索引
SELECT:
 	MyISAM:
    MyISAM更优
 	InnoDB:
INSERT:
 	MyISAM:
 	InnoDB:
 	InnoDB更优
UPDATE:
	MyISAM:
 	InnoDB:
 	InnoDB更优
DELETE:
	MyISAM:
 	InnoDB:
 	InnoDB更优 它不会重新建立表，而是一行一行的删除
COUNT without WHERE:
	MyISAM:
	MyISAM更优。因为MyISAM保存了表的具体行数
 	InnoDB:
 	InnoDB没有保存表的具体行数，需要逐行扫描统计，就很慢了
COUNT with WHERE:
	MyISAM:
	一样
 	InnoDB:
 	一样，InnoDB也会锁表
锁:
	MyISAM:
	只支持表锁
 	InnoDB:
 	支持表锁、行锁,行锁大幅度提高了多用户并发操作的新能。但是InnoDB的行锁，只是在WHERE的主键是有效的，非主键的WHERE都会锁全表的
外键:
	MyISAM:
    不支持
 	InnoDB:
 	支持
FULLTEXT全文索引:
	MyISAM:
	支持
 	InnoDB:
 	不支持（5.6.4以上支持英文全文索引） 可以通过使用Sphinx从InnoDB中获得全文索引，会慢一点

互联网项目中随着硬件成本的降低及缓存、中间件的应用，一般我们选择都以 InnoDB 存储引擎为主，很少再去选择 MyISAM 了。
而业务真发展的一定程度时，自带的存储引擎无法满足时，这时公司应该是有实力去自主研发满足自己需求的存储引擎或者购买商用的存储引擎了。
