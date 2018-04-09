https://tech.meituan.com/kafka-fs-design-theory.html

如果Kafka重启，所有的In-Process Cache都会失效，而OS管理的PageCache依然可以继续使用。

每个Replication集合中的Partition都会选出一个唯一的Leader，所有的读写请求都由Leader处理。
其他Replicas从Leader处把数据更新同步到本地，过程类似大家熟悉的MySQL中的Binlog同步。